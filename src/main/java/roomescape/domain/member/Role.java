package roomescape.domain.member;

public enum Role {
    USER,
    MANAGER,
    ADMIN;

    public boolean canAccessAdmin() {
        return this == ADMIN || this == MANAGER;
    }
}
