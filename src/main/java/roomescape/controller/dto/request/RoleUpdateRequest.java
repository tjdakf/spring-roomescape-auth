package roomescape.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import roomescape.domain.member.Role;

public record RoleUpdateRequest(
        @NotNull(message = "role은 비어 있을 수 없습니다.")
        Role role
) {
}
