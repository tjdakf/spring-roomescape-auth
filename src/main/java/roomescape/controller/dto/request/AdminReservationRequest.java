package roomescape.controller.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record AdminReservationRequest(

        @NotNull(message = "memberId는 비어 있을 수 없습니다.")
        @Positive(message = "memberId는 양수이어야 합니다.")
        Long memberId,

        @NotNull(message = "date는 비어 있을 수 없습니다.")
        LocalDate date,

        @NotNull(message = "timeId는 비어 있을 수 없습니다.")
        @Positive(message = "timeId는 양수이어야 합니다.")
        Long timeId,

        @NotNull(message = "themeId는 비어 있을 수 없습니다.")
        @Positive(message = "themeId는 양수이어야 합니다.")
        Long themeId
) {
}
