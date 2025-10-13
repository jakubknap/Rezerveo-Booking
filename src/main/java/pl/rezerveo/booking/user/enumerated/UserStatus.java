package pl.rezerveo.booking.user.enumerated;

public enum UserStatus {
    REGISTERED,
    ACTIVE,
    BLOCKED,
    DEACTIVATED;

    public boolean isActive() {
        return ACTIVE == this;
    }

    public boolean isNotActive() {
        return !isActive();
    }

    public boolean isBlocked() {
        return BLOCKED == this;
    }
}