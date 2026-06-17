package roomescape.controller.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.SessionConstants;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.service.MemberService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMemberController.class)
class AdminMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Test
    void 회원_목록을_등급과_함께_조회한다() throws Exception {
        given(memberService.findAll()).willReturn(List.of(
                new Member(1L, "admin", "password", "관리자", Role.ADMIN),
                new Member(2L, "gugu", "password", "구구", Role.USER)));

        mockMvc.perform(get("/admin/members")
                        .sessionAttr(SessionConstants.LOGIN_MEMBER_ID, 1L)
                        .sessionAttr(SessionConstants.LOGIN_MEMBER_ROLE, Role.ADMIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(1))
                .andExpect(jsonPath("$[0].role").value("ADMIN"))
                .andExpect(jsonPath("$[1].role").value("USER"));
    }

    @Test
    void 관리자가_회원을_매니저로_승격한다() throws Exception {
        given(memberService.changeRole(1L, 2L, Role.MANAGER))
                .willReturn(new Member(2L, "gugu", "password", "구구", Role.MANAGER));

        mockMvc.perform(patch("/admin/members/2/role")
                        .sessionAttr(SessionConstants.LOGIN_MEMBER_ID, 1L)
                        .sessionAttr(SessionConstants.LOGIN_MEMBER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"MANAGER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(2))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    void 변경할_등급이_없으면_에러_응답() throws Exception {
        mockMvc.perform(patch("/admin/members/2/role")
                        .sessionAttr(SessionConstants.LOGIN_MEMBER_ID, 1L)
                        .sessionAttr(SessionConstants.LOGIN_MEMBER_ROLE, Role.ADMIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(memberService);
    }
}
