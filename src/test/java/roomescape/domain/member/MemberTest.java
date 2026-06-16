package roomescape.domain.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void loginId가_null_또는_blank이면_예외(String loginId) {
        assertThatThrownBy(() -> new Member(null, loginId, "password", "구구"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("loginId는 비어 있을 수 없습니다.");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void password가_null_또는_blank이면_예외(String password) {
        assertThatThrownBy(() -> new Member(null, "gugu", password, "구구"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password는 비어 있을 수 없습니다.");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void name이_null_또는_blank이면_예외(String name) {
        assertThatThrownBy(() -> new Member(null, "gugu", "password", name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name은 비어 있을 수 없습니다.");
    }

    @Test
    void 비밀번호가_같으면_true를_반환한다() {
        Member member = new Member(null, "gugu", "password", "구구");

        assertThat(member.hasPassword("password")).isTrue();
    }

    @Test
    void 비밀번호가_다르면_false를_반환한다() {
        Member member = new Member(null, "gugu", "password", "구구");

        assertThat(member.hasPassword("wrong-password")).isFalse();
    }

    @Test
    void memberId를_부여한_새_Member를_만든다() {
        Member member = new Member(null, "gugu", "password", "구구");

        Member savedMember = member.withMemberId(1L);

        assertThat(savedMember.getMemberId()).isEqualTo(1L);
        assertThat(savedMember.getLoginId()).isEqualTo(member.getLoginId());
        assertThat(savedMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(savedMember.getName()).isEqualTo(member.getName());
    }
}
