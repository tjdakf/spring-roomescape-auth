package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

@Service
public class AuthService {

    private static final String LOGIN_FAILED_MESSAGE = "아이디 또는 비밀번호가 올바르지 않습니다.";

    private final MemberRepository memberRepository;

    public AuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member login(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(this::loginFailed);
        validatePassword(member, password);
        return member;
    }

    public Member findLoginMember(Long memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(this::unauthorized);
    }

    private void validatePassword(Member member, String password) {
        if (!member.hasPassword(password)) {
            throw loginFailed();
        }
    }

    private RoomescapeException loginFailed() {
        return new RoomescapeException(ErrorCode.UNAUTHORIZED, LOGIN_FAILED_MESSAGE);
    }

    private RoomescapeException unauthorized() {
        return new RoomescapeException(ErrorCode.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getDetail());
    }
}
