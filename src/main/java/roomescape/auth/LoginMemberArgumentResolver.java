package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class)
                && LoginMemberInfo.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConstants.LOGIN_MEMBER_ID) == null) {
            throw unauthorized();
        }
        Long memberId = (Long) session.getAttribute(SessionConstants.LOGIN_MEMBER_ID);
        return new LoginMemberInfo(memberId);
    }

    private RoomescapeException unauthorized() {
        return new RoomescapeException(ErrorCode.UNAUTHORIZED);
    }
}
