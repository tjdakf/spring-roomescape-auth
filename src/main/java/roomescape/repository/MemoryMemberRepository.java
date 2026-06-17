package roomescape.repository;

import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryMemberRepository implements MemberRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, Member> members = new ConcurrentHashMap<>();

    @Override
    public Member insert(Member member) {
        Long memberId = resolveMemberId(member);
        Member savedMember = member.withMemberId(memberId);
        members.put(memberId, savedMember);
        return savedMember;
    }

    @Override
    public List<Member> findAll() {
        return members.values().stream()
                .sorted(Comparator.comparing(Member::getMemberId))
                .toList();
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

    @Override
    public void updateRole(Long memberId, Role role) {
        Member member = members.get(memberId);
        if (member != null) {
            members.put(memberId, member.withRole(role));
        }
    }

    private Long resolveMemberId(Member member) {
        if (member.getMemberId() != null) {
            return member.getMemberId();
        }
        return sequence.getAndIncrement();
    }
}
