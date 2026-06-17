package roomescape.service;

import org.junit.jupiter.api.Test;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private final MemberRepository memberRepository = mock();
    private final LoginService loginService = new LoginService(memberRepository);

    @Test
    void 로그인_성공() {
        // given
        Member member = new Member(1L, "gugu", "password", "구구");
        when(memberRepository.findByLoginId("gugu"))
                .thenReturn(Optional.of(member));

        // when
        Member result = loginService.login("gugu", "password");

        // then
        assertThat(result).isEqualTo(member);
        verify(memberRepository, times(1)).findByLoginId("gugu");
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void 존재하지_않는_loginId이면_예외() {
        // given
        when(memberRepository.findByLoginId("unknown"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loginService.login("unknown", "password"))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");

        verify(memberRepository, times(1)).findByLoginId("unknown");
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void 비밀번호가_일치하지_않으면_예외() {
        // given
        when(memberRepository.findByLoginId("gugu"))
                .thenReturn(Optional.of(new Member(1L, "gugu", "password", "구구")));

        // when & then
        assertThatThrownBy(() -> loginService.login("gugu", "wrong-password"))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");

        verify(memberRepository, times(1)).findByLoginId("gugu");
        verifyNoMoreInteractions(memberRepository);
    }
}
