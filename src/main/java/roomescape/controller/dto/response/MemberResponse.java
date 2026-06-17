package roomescape.controller.dto.response;

import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

public record MemberResponse(
        Long memberId,
        String loginId,
        String name,
        Role role
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getLoginId(),
                member.getName(),
                member.getRole());
    }
}
