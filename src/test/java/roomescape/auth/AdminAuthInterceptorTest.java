package roomescape.auth;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import roomescape.domain.member.Role;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminAuthInterceptorTest {

    private final AdminAuthInterceptor interceptor = new AdminAuthInterceptor();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final Object handler = new Object();

    @Test
    void 관리자_역할이면_요청을_통과시킨다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 1L);
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ROLE, Role.ADMIN);

        // when
        boolean result = interceptor.preHandle(request, response, handler);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 로그인하지_않으면_401_예외() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when & then
        assertThatThrownBy(() -> interceptor.preHandle(request, response, handler))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }

    @Test
    void 관리자가_아니면_403_예외() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 2L);
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ROLE, Role.USER);

        // when & then
        assertThatThrownBy(() -> interceptor.preHandle(request, response, handler))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_RESOURCE);
    }
}
