package roomescape.controller.dto.response;

import roomescape.domain.member.Member;

public record MemberCreateResponse(
        String loginId
) {

    public static MemberCreateResponse from(Member member) {
        return new MemberCreateResponse(member.getLoginId());
    }
}
