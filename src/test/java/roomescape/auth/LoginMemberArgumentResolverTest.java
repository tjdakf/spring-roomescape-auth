package roomescape.auth;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginMemberArgumentResolverTest {

    private final LoginMemberArgumentResolver resolver = new LoginMemberArgumentResolver();

    @Test
    void LoginMember가_붙은_LoginMemberInfo_파라미터를_지원한다() throws Exception {
        MethodParameter parameter = methodParameter("supported", 0);

        assertThat(resolver.supportsParameter(parameter)).isTrue();
    }

    @Test
    void LoginMember가_없으면_지원하지_않는다() throws Exception {
        MethodParameter parameter = methodParameter("withoutAnnotation", 0);

        assertThat(resolver.supportsParameter(parameter)).isFalse();
    }

    @Test
    void LoginMemberInfo_타입이_아니면_지원하지_않는다() throws Exception {
        MethodParameter parameter = methodParameter("notMember", 0);

        assertThat(resolver.supportsParameter(parameter)).isFalse();
    }

    @Test
    void 세션의_회원_id를_로그인_사용자_정보로_전달한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 1L);

        Object result = resolver.resolveArgument(
                methodParameter("supported", 0),
                null,
                new ServletWebRequest(request),
                null
        );

        assertThat(result).isEqualTo(new LoginMemberInfo(1L));
    }

    @Test
    void 세션이_없으면_예외() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThatThrownBy(() -> resolver.resolveArgument(
                methodParameter("supported", 0),
                null,
                new ServletWebRequest(request),
                null
        ))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }

    @Test
    void 세션에_로그인_회원_id가_없으면_예외() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession();

        assertThatThrownBy(() -> resolver.resolveArgument(
                methodParameter("supported", 0),
                null,
                new ServletWebRequest(request),
                null
        ))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED);
    }

    private MethodParameter methodParameter(String methodName, int parameterIndex) throws NoSuchMethodException {
        Method method = SampleController.class.getDeclaredMethod(methodName, methodParameterTypes(methodName));
        return new MethodParameter(method, parameterIndex);
    }

    private Class<?>[] methodParameterTypes(String methodName) {
        if (methodName.equals("notMember")) {
            return new Class<?>[]{String.class};
        }
        return new Class<?>[]{LoginMemberInfo.class};
    }

    private static class SampleController {

        void supported(@LoginMember LoginMemberInfo loginMemberInfo) {
        }

        void withoutAnnotation(LoginMemberInfo loginMemberInfo) {
        }

        void notMember(@LoginMember String name) {
        }
    }
}
