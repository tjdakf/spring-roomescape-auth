package roomescape.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.LoginRequest;
import roomescape.domain.member.Member;
import roomescape.service.LoginService;

@RestController
public class AuthController {

    private static final String LOGIN_MEMBER_ID = "loginMemberId";

    private final LoginService loginService;

    public AuthController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        Member member = loginService.login(request.loginId(), request.password());
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute(LOGIN_MEMBER_ID, member.getMemberId());
        return ResponseEntity.ok().build();
    }
}
