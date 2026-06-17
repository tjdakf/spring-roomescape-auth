package roomescape.domain;

import java.time.LocalDateTime;

public class ReservationWaiting {

    private final Long id;
    private final Long memberId;
    private final String name;
    private final ReservationSlot slot;

    public ReservationWaiting(Long id, String name, ReservationSlot slot) {
        this(id, null, name, slot);
    }

    public ReservationWaiting(Long id, Long memberId, String name, ReservationSlot slot) {
        validateName(name);
        validateSlot(slot);

        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.slot = slot;
    }

    public ReservationWaiting withId(Long id) {
        return new ReservationWaiting(id, memberId, name, slot);
    }

    public boolean isPast(LocalDateTime now) {
        return slot.isPast(now);
    }

    public boolean isOwnedBy(Long memberId) {
        return this.memberId != null && this.memberId.equals(memberId);
    }

    public Reservation promoteToReservation() {
        return new Reservation(null, memberId, name, slot);
    }

    public ReservationSlot getSlot() {
        return slot;
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
