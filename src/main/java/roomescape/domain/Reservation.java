package roomescape.domain;

import java.time.LocalDateTime;

public class Reservation {

    private final Long id;
    private final Long memberId;
    private final String name;
    private final ReservationSlot slot;

    public Reservation(Long id, String name, ReservationSlot slot) {
        this(id, null, name, slot);
    }

    public Reservation(Long id, Long memberId, String name, ReservationSlot slot) {
        validateName(name);
        validateSlot(slot);

        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.slot = slot;
    }

    public Reservation withId(Long id) {
        return new Reservation(id, memberId, name, slot);
    }

    public boolean isOwnedBy(Long memberId) {
        return this.memberId != null && this.memberId.equals(memberId);
    }

    public boolean isPast(LocalDateTime now) {
        return slot.isPast(now);
    }

    public boolean hasSameSchedule(Reservation other) {
        return slot.hasSameSchedule(other.getSlot());
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public ReservationSlot getSlot() {
        return slot;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("예약자 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateSlot(ReservationSlot slot) {
        if (slot == null) {
            throw new IllegalArgumentException("slot은 비어 있을 수 없습니다.");
        }
    }
}
