package roomescape.controller.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.MemberCreateRequest;
import roomescape.controller.dto.response.MemberCreateResponse;
import roomescape.domain.member.Member;
import roomescape.service.MemberService;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberCreateResponse> createMember(@Valid @RequestBody MemberCreateRequest request) {
        Member member = memberService.join(
                request.loginId(),
                request.password(),
                request.name());
        return ResponseEntity.status(201)
                .body(MemberCreateResponse.from(member));
    }
}
