package roomescape.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member join(String loginId, String password, String name) {
        validateDuplicateLoginId(loginId);
        Member member = new Member(null, loginId, password, name);
        try {
            return memberRepository.insert(member);
        } catch (DuplicateKeyException e) {
            throw duplicateLoginId();
        }
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Member findByMemberId(Long memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RoomescapeException(ErrorCode.NOT_FOUND, "존재하지 않는 회원입니다."));
    }

    public Member changeRole(Long actingMemberId, Long targetMemberId, Role newRole) {
        Member actor = findByMemberId(actingMemberId);
        if (!actor.isAdmin()) {
            throw new RoomescapeException(ErrorCode.FORBIDDEN_RESOURCE, "관리자만 회원 등급을 변경할 수 있습니다.");
        }
        validateAssignableRole(newRole);

        Member target = findByMemberId(targetMemberId);
        memberRepository.updateRole(target.getMemberId(), newRole);
        return target.withRole(newRole);
    }

    private void validateAssignableRole(Role role) {
        if (role == null) {
            throw new RoomescapeException(ErrorCode.INVALID_INPUT, "role은 비어 있을 수 없습니다.");
        }
        if (role == Role.ADMIN) {
            throw new RoomescapeException(ErrorCode.INVALID_INPUT, "ADMIN 권한은 부여할 수 없습니다.");
        }
    }

    private void validateDuplicateLoginId(String loginId) {
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw duplicateLoginId();
        }
    }

    private RoomescapeException duplicateLoginId() {
        return new RoomescapeException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 로그인 ID입니다.");
    }
}
