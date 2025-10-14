package pl.rezerveo.booking.user.dto.response;

import pl.rezerveo.booking.user.enumerated.Role;

import java.util.UUID;

public record UserDetailsResponse(UUID uuid, String firstName, String lastName, String email, String phoneNumber, Role role) {}