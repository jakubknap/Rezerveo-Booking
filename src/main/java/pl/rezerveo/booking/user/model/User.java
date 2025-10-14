package pl.rezerveo.booking.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.rezerveo.booking.common.entity.auditable.DateAuditEntity;
import pl.rezerveo.booking.security.encryption.EncryptedStringConverter;
import pl.rezerveo.booking.token.model.Token;
import pl.rezerveo.booking.user.enumerated.Role;
import pl.rezerveo.booking.user.enumerated.UserStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User extends DateAuditEntity implements UserDetails {

    private static final String ROLE_PREFIX = "ROLE_";

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    @Convert(converter = EncryptedStringConverter.class)
    private String firstName;

    @Column(nullable = false)
    @Convert(converter = EncryptedStringConverter.class)
    private String lastName;

    @Column(nullable = false, unique = true)
    @Convert(converter = EncryptedStringConverter.class)
    private String email;

    @Convert(converter = EncryptedStringConverter.class)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = REMOVE, orphanRemoval = true)
    private List<Token> tokens = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !status.isBlocked();
    }

    @Override
    public boolean isEnabled() {
        return status.isActive();
    }
}