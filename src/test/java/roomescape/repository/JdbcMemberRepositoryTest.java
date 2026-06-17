package roomescape.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
class JdbcMemberRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JdbcMemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new JdbcMemberRepository(jdbcTemplate);
        jdbcTemplate.update("DELETE FROM member;");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1;");
    }

    @Test
    void 회원을_저장하면_memberId를_부여한다() {
        Member member = new Member(null, "gugu", "password", "구구");

        Member savedMember = memberRepository.insert(member);

        assertThat(savedMember.getMemberId()).isEqualTo(1L);
    }

    @Test
    void memberId로_회원을_조회한다() {
        Member savedMember = memberRepository.insert(new Member(null, "gugu", "password", "구구"));

        Member foundMember = memberRepository.findByMemberId(savedMember.getMemberId()).get();

        assertThat(foundMember)
                .usingRecursiveComparison()
                .isEqualTo(savedMember);
    }

    @Test
    void 존재하지_않는_memberId로_조회하면_빈_값을_반환한다() {
        assertThat(memberRepository.findByMemberId(999L)).isEmpty();
    }

    @Test
    void loginId로_회원을_조회한다() {
        Member savedMember = memberRepository.insert(new Member(null, "gugu", "password", "구구"));

        Member foundMember = memberRepository.findByLoginId("gugu").get();

        assertThat(foundMember)
                .usingRecursiveComparison()
                .isEqualTo(savedMember);
    }

    @Test
    void 존재하지_않는_loginId로_조회하면_빈_값을_반환한다() {
        assertThat(memberRepository.findByLoginId("unknown")).isEmpty();
    }

    @Test
    void loginId는_중복될_수_없다() {
        memberRepository.insert(new Member(null, "gugu", "password", "구구"));

        assertThatThrownBy(() -> memberRepository.insert(new Member(null, "gugu", "password", "다른이름")))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void 회원_등급을_변경한다() {
        Member saved = memberRepository.insert(new Member(null, "gugu", "password", "구구"));

        memberRepository.updateRole(saved.getMemberId(), Role.MANAGER);

        assertThat(memberRepository.findByMemberId(saved.getMemberId()).get().getRole())
                .isEqualTo(Role.MANAGER);
    }
}
