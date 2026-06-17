package roomescape.controller.dto.response;

import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

public record LoginMemberResponse(
        String name,
        Role role
) {

    public static LoginMemberResponse from(Member member) {
        return new LoginMemberResponse(member.getName(), member.getRole());
    }
}
