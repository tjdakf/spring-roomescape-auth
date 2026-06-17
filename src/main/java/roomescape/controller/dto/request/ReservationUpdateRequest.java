package roomescape.controller.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ReservationUpdateRequest(
        LocalDate date,

        @Positive(message = "timeId는 양수이어야 합니다.")
        Long timeId
) {

    @AssertTrue(message = "변경할 날짜 또는 시간이 필요합니다.")
    public boolean hasUpdateValue() {
        return date != null || timeId != null;
    }
}
