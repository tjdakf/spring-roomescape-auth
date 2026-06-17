package roomescape.auth;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginCheckInterceptorTest {

    private final LoginCheckInterceptor interceptor = new LoginCheckInterceptor();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final Object handler = new Object();

    @Test
    void 세션에_로그인_회원_id가_있으면_요청을_통과시킨다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 1L);

        // when
        boolean result = interceptor.preHandle(request, response, handler);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 세션이_없으면_예외() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when & then
        assertThatThrownBy(() -> interceptor.preHandle(request, response, handler))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }

    @Test
    void 세션에_로그인_회원_id가_없으면_예외() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession();

        // when & then
        assertThatThrownBy(() -> interceptor.preHandle(request, response, handler))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }
}
