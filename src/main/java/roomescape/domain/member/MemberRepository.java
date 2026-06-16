package roomescape.domain.member;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findByMemberId(Long memberId);

    Optional<Member> findByLoginId(String loginId);
}
