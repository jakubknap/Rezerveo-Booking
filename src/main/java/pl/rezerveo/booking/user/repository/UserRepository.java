package pl.rezerveo.booking.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.rezerveo.booking.user.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = """
            SELECT EXISTS(SELECT 1
                          FROM _user u
                          WHERE u.email = :email)
            """, nativeQuery = true)
    boolean existsByEmail(String email);

    @Query(value = """
            SELECT EXISTS(SELECT 1
                          FROM _user u
                          WHERE u.email = :email
                             AND u.uuid != :userUuid)
            """, nativeQuery = true)
    boolean isEmailTaken(String email, UUID userUuid);
}