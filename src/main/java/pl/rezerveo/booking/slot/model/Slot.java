package pl.rezerveo.booking.slot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.rezerveo.booking.booking.model.Booking;
import pl.rezerveo.booking.common.entity.auditable.FullAuditEntity;
import pl.rezerveo.booking.slot.enumerate.ServiceType;
import pl.rezerveo.booking.slot.enumerate.SlotStatus;
import pl.rezerveo.booking.user.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Slot extends FullAuditEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Enumerated(STRING)
    @Column(nullable = false)
    private SlotStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mechanic_id", nullable = false)
    private User mechanic;

    @OneToOne(mappedBy = "slot", cascade = ALL)
    private Booking booking;
}