package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.member.Role;

/**
 * 관리자 페이지(/admin 등 화면 라우트) 인가. 권한이 없으면 관리자 로그인 페이지로 리다이렉트한다.
 */
public class AdminPageAuthInterceptor implements HandlerInterceptor {

    private static final String ADMIN_LOGIN_PAGE = "/admin/login";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute(SessionConstants.LOGIN_MEMBER_ROLE) instanceof Role role)
                || !role.canAccessAdmin()) {
            response.sendRedirect(ADMIN_LOGIN_PAGE);
            return false;
        }
        return true;
    }
}
