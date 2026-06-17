package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member join(String loginId, String password, String name) {
        validateDuplicateLoginId(loginId);
        Member member = new Member(null, loginId, password, name);
        return memberRepository.save(member);
    }

    private void validateDuplicateLoginId(String loginId) {
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new RoomescapeException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 로그인 ID입니다.");
        }
    }
}
