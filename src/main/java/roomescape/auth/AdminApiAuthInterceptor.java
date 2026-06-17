package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.member.Role;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

/**
 * 관리자 API(/admin/** 중 데이터 엔드포인트) 인가. 실패 시 JSON 에러를 응답한다.
 */
public class AdminApiAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConstants.LOGIN_MEMBER_ID) == null) {
            throw new RoomescapeException(ErrorCode.UNAUTHORIZED);
        }
        if (!(session.getAttribute(SessionConstants.LOGIN_MEMBER_ROLE) instanceof Role role) || !role.canAccessAdmin()) {
            throw new RoomescapeException(ErrorCode.FORBIDDEN_RESOURCE, "관리자 권한이 필요합니다.");
        }
        return true;
    }
}
