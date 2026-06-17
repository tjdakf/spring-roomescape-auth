package roomescape.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import roomescape.domain.member.Role;

import static org.assertj.core.api.Assertions.assertThat;

class AdminPageAuthInterceptorTest {

    private final AdminPageAuthInterceptor interceptor = new AdminPageAuthInterceptor();
    private final Object handler = new Object();

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ADMIN", "MANAGER"})
    void 관리자_권한이면_페이지_요청을_통과시킨다(Role role) throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 1L);
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ROLE, role);

        // when
        boolean result = interceptor.preHandle(request, response, handler);

        // then
        assertThat(result).isTrue();
        assertThat(response.getRedirectedUrl()).isNull();
    }

    @Test
    void 로그인하지_않으면_관리자_로그인_페이지로_리다이렉트한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        boolean result = interceptor.preHandle(request, response, handler);

        // then
        assertThat(result).isFalse();
        assertThat(response.getRedirectedUrl()).isEqualTo("/admin/login");
    }

    @Test
    void 일반_사용자면_관리자_로그인_페이지로_리다이렉트한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 2L);
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ROLE, Role.USER);

        // when
        boolean result = interceptor.preHandle(request, response, handler);

        // then
        assertThat(result).isFalse();
        assertThat(response.getRedirectedUrl()).isEqualTo("/admin/login");
    }
}
