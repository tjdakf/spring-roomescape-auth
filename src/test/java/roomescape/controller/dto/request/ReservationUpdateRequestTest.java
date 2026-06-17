package roomescape.controller.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationUpdateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final LocalDate date = LocalDate.parse("2026-05-02");

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void timeId가_양수가_아니면_예외(Long timeId) {
        // when
        Set<ConstraintViolation<ReservationUpdateRequest>> result = validator.validate(
                new ReservationUpdateRequest(date, timeId));

        // then
        assertThat(result).extracting(ConstraintViolation::getMessage)
                .contains("timeId는 양수이어야 합니다.");
    }

    @Test
    void 날짜와_시간이_모두_null이면_예외() {
        // when
        Set<ConstraintViolation<ReservationUpdateRequest>> result = validator.validate(
                new ReservationUpdateRequest(null, null));

        // then
        assertThat(result).extracting(ConstraintViolation::getMessage)
                .contains("변경할 날짜 또는 시간이 필요합니다.");
    }

    @ParameterizedTest
    @MethodSource("validRequests")
    void 정상_생성_테스트(LocalDate date, Long timeId) {
        // when
        ReservationUpdateRequest result = new ReservationUpdateRequest(date, timeId);

        // then
        assertAll(
                () -> assertThat(validator.validate(result)).isEmpty(),
                () -> assertThat(result.date()).isEqualTo(date),
                () -> assertThat(result.timeId()).isEqualTo(timeId));
    }

    private static Stream<Arguments> validRequests() {
        return Stream.of(
                Arguments.of(LocalDate.parse("2026-05-02"), 1L),
                Arguments.of(LocalDate.parse("2026-05-02"), null),
                Arguments.of(null, 1L)
        );
    }
}
