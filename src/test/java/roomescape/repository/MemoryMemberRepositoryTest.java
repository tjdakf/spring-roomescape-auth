package roomescape.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roomescape.domain.member.Member;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryMemberRepositoryTest {

    private MemoryMemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository = new MemoryMemberRepository();
    }

    @Test
    void 회원을_저장하면_memberId를_부여한다() {
        Member member = new Member(null, "gugu", "password", "구구");

        Member savedMember = memberRepository.save(member);

        assertThat(savedMember.getMemberId()).isNotNull();
    }

    @Test
    void memberId로_회원을_조회한다() {
        Member savedMember = memberRepository.save(new Member(null, "gugu", "password", "구구"));

        assertThat(memberRepository.findByMemberId(savedMember.getMemberId()))
                .contains(savedMember);
    }

    @Test
    void 존재하지_않는_memberId로_조회하면_빈_값을_반환한다() {
        assertThat(memberRepository.findByMemberId(999L)).isEmpty();
    }

    @Test
    void loginId로_회원을_조회한다() {
        Member savedMember = memberRepository.save(new Member(null, "gugu", "password", "구구"));

        assertThat(memberRepository.findByLoginId("gugu"))
                .contains(savedMember);
    }

    @Test
    void 존재하지_않는_loginId로_조회하면_빈_값을_반환한다() {
        assertThat(memberRepository.findByLoginId("unknown")).isEmpty();
    }
}
