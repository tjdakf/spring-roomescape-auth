package roomescape.domain.member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member insert(Member member);

    List<Member> findAll();

    Optional<Member> findByMemberId(Long memberId);

    Optional<Member> findByLoginId(String loginId);

    void updateRole(Long memberId, Role role);
}
