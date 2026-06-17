package roomescape.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import roomescape.domain.member.Role;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminApiAuthInterceptorTest {

    private final AdminApiAuthInterceptor interceptor = new AdminApiAuthInterceptor();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final Object handler = new Object();

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ADMIN", "MANAGER"})
    void 관리자_권한이면_요청을_통과시킨다(Role role) {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 1L);
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ROLE, role);

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
    void 일반_사용자면_403_예외() {
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
