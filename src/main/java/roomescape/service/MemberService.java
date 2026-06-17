package roomescape.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
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

    private void validateDuplicateLoginId(String loginId) {
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw duplicateLoginId();
        }
    }

    private RoomescapeException duplicateLoginId() {
        return new RoomescapeException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 로그인 ID입니다.");
    }
}
