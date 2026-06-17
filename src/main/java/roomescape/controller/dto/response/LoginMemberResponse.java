package roomescape.controller.dto.response;

import roomescape.domain.member.Member;

public record LoginMemberResponse(
        String name
) {

    public static LoginMemberResponse from(Member member) {
        return new LoginMemberResponse(member.getName());
    }
}
