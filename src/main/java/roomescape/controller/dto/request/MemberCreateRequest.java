package roomescape.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberCreateRequest(

        @NotBlank(message = "loginId는 비어 있을 수 없습니다.")
        @Size(max = 255, message = "loginId는 255자를 넘을 수 없습니다.")
        String loginId,

        @NotBlank(message = "password는 비어 있을 수 없습니다.")
        @Size(max = 255, message = "password는 255자를 넘을 수 없습니다.")
        String password,

        @NotBlank(message = "name은 비어 있을 수 없습니다.")
        @Size(max = 255, message = "name은 255자를 넘을 수 없습니다.")
        String name
) {
}
