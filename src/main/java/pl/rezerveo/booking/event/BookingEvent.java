package pl.rezerveo.booking.event;

public record BookingEvent(String targetEmail, String title, String message) {}