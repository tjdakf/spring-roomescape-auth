package roomescape.repository;

import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryMemberRepository implements MemberRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, Member> members = new ConcurrentHashMap<>();

    @Override
    public Member save(Member member) {
        Long memberId = resolveMemberId(member);
        Member savedMember = member.withMemberId(memberId);
        members.put(memberId, savedMember);
        return savedMember;
    }

    @Override
    public Optional<Member> findByMemberId(Long memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return members.values().stream()
                .filter(member -> member.getLoginId().equals(loginId))
                .findAny();
    }

    private Long resolveMemberId(Member member) {
        if (member.getMemberId() != null) {
            return member.getMemberId();
        }
        return sequence.getAndIncrement();
    }
}
