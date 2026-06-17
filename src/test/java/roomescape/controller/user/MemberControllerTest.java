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
import roomescape.service.MemberService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    void 회원가입_테스트() throws Exception {
        when(memberService.join(any(), any(), any()))
                .thenReturn(new Member(1L, "gugu", "password", "구구"));

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "gugu",
                                  "password": "password",
                                  "name": "구구"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loginId").value("gugu"));
    }

    @Test
    void 회원가입_요청값이_유효하지_않으면_에러_응답() throws Exception {
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "",
                                  "password": "password",
                                  "name": "구구"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));

        verifyNoInteractions(memberService);
    }

    @Test
    void 중복_loginId이면_에러_응답() throws Exception {
        when(memberService.join(any(), any(), any()))
                .thenThrow(new RoomescapeException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 로그인 ID입니다."));

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "loginId": "gugu",
                                  "password": "password",
                                  "name": "구구"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_RESOURCE"));
    }
}
