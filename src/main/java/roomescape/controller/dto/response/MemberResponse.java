package roomescape.controller.dto.response;

import roomescape.domain.member.Member;

public record MemberResponse(
        Long memberId,
        String loginId,
        String name
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getLoginId(),
                member.getName());
    }
}
