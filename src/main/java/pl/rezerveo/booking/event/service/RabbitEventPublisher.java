package pl.rezerveo.booking.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pl.rezerveo.booking.config.RabbitConfig;
import pl.rezerveo.booking.event.BookingEvent;
import pl.rezerveo.booking.event.MailEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendBookingEvent(BookingEvent event) {
        log.info("Sending BookingEvent to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(RabbitConfig.BOOKING_EXCHANGE, RabbitConfig.BOOKING_EVENT_ROUTING_KEY, event);
    }

    public void sendMailEvent(MailEvent event) {
        log.info("Sending MailEvent to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(RabbitConfig.BOOKING_EXCHANGE, RabbitConfig.MAIL_EVENT_ROUTING_KEY, event);
    }
}