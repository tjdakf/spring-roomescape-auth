package roomescape.controller.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LoginRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void loginId가_null_또는_blank이면_예외(String loginId) {
        // when
        Set<ConstraintViolation<LoginRequest>> result = validator.validate(
                new LoginRequest(loginId, "password"));

        // then
        assertThat(result).extracting(ConstraintViolation::getMessage)
                .containsExactly("loginId는 비어 있을 수 없습니다.");
    }

    @Test
    void loginId가_255자를_초과하면_예외() {
        // given
        String loginId = "a".repeat(256);

        // when
        Set<ConstraintViolation<LoginRequest>> result = validator.validate(
                new LoginRequest(loginId, "password"));

        // then
        assertThat(result).extracting(ConstraintViolation::getMessage)
                .containsExactly("loginId는 255자를 넘을 수 없습니다.");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void password가_null_또는_blank이면_예외(String password) {
        // when
        Set<ConstraintViolation<LoginRequest>> result = validator.validate(
                new LoginRequest("gugu", password));

        // then
        assertThat(result).extracting(ConstraintViolation::getMessage)
                .containsExactly("password는 비어 있을 수 없습니다.");
    }

    @Test
    void password가_255자를_초과하면_예외() {
        // given
        String password = "a".repeat(256);

        // when
        Set<ConstraintViolation<LoginRequest>> result = validator.validate(
                new LoginRequest("gugu", password));

        // then
        assertThat(result).extracting(ConstraintViolation::getMessage)
                .containsExactly("password는 255자를 넘을 수 없습니다.");
    }

    @Test
    void 정상_생성_테스트() {
        // given
        String loginId = "gugu";
        String password = "password";

        // when
        LoginRequest result = new LoginRequest(loginId, password);

        // then
        assertAll(
                () -> assertThat(validator.validate(result)).isEmpty(),
                () -> assertThat(result.loginId()).isEqualTo(loginId),
                () -> assertThat(result.password()).isEqualTo(password));
    }
}
