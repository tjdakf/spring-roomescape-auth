package roomescape.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.domain.member.Member;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void 로그인_성공시_세션에_회원_id를_저장한다() throws Exception {
        when(authService.login(any(), any()))
                .thenReturn(new Member(1L, "gugu", "password", "구구"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "gugu",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(request().sessionAttribute("loginMemberId", 1L));
    }

    @Test
    void 로그인_요청값이_유효하지_않으면_에러_응답() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));

        verifyNoInteractions(authService);
    }

    @Test
    void 로그인_실패시_에러_응답() throws Exception {
        when(authService.login(any(), any()))
                .thenThrow(new RoomescapeException(ErrorCode.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "gugu",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void 현재_로그인_사용자를_조회한다() throws Exception {
        when(authService.findLoginMember(1L))
                .thenReturn(new Member(1L, "gugu", "password", "구구"));

        mockMvc.perform(get("/me")
                        .sessionAttr("loginMemberId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("구구"));
    }

    @Test
    void 현재_로그인_사용자_조회시_세션이_없으면_에러_응답() throws Exception {
        mockMvc.perform(get("/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

        verifyNoMoreInteractions(authService);
    }

    @Test
    void 로그아웃을_한다() throws Exception {
        mockMvc.perform(post("/logout")
                        .sessionAttr("loginMemberId", 1L))
                .andExpect(status().isNoContent())
                .andExpect(request().sessionAttributeDoesNotExist("loginMemberId"));
    }

    @Test
    void 세션이_없어도_로그아웃은_성공한다() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isNoContent());
    }
}
