package roomescape.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMember;
import roomescape.auth.LoginMemberInfo;
import roomescape.controller.dto.request.RoleUpdateRequest;
import roomescape.controller.dto.response.MemberResponse;
import roomescape.domain.member.Member;
import roomescape.service.MemberService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getMembers() {
        List<MemberResponse> members = memberService.findAll().stream()
                .map(MemberResponse::from)
                .toList();
        return ResponseEntity.ok(members);
    }

    @PatchMapping("/{memberId}/role")
    public ResponseEntity<MemberResponse> changeRole(
            @LoginMember LoginMemberInfo loginMemberInfo,
            @PathVariable @Positive(message = "memberId는 양수이어야 합니다.") Long memberId,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        Member member = memberService.changeRole(loginMemberInfo.memberId(), memberId, request.role());
        return ResponseEntity.ok(MemberResponse.from(member));
    }
}
