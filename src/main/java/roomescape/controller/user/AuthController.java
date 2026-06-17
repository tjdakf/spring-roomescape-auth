package roomescape.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMember;
import roomescape.auth.LoginMemberInfo;
import roomescape.auth.SessionConstants;
import roomescape.controller.dto.request.LoginRequest;
import roomescape.controller.dto.response.LoginMemberResponse;
import roomescape.domain.member.Member;
import roomescape.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        Member member = authService.login(request.loginId(), request.password());
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute(SessionConstants.LOGIN_MEMBER_ID, member.getMemberId());
        session.setAttribute(SessionConstants.LOGIN_MEMBER_ROLE, member.getRole());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<LoginMemberResponse> getLoginMember(@LoginMember LoginMemberInfo loginMemberInfo) {
        Member member = authService.findLoginMember(loginMemberInfo.memberId());
        return ResponseEntity.ok(LoginMemberResponse.from(member));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }
}
