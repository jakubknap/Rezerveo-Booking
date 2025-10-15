package pl.rezerveo.booking.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.rezerveo.booking.booking.enumerated.BookingStatus;
import pl.rezerveo.booking.common.entity.auditable.FullAuditEntity;
import pl.rezerveo.booking.slot.model.Slot;
import pl.rezerveo.booking.user.model.User;

import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking extends FullAuditEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Enumerated(STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
}