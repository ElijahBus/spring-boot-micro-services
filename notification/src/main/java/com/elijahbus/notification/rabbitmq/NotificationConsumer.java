package com.elijahbus.notification.rabbitmq;

import com.elijahbus.clients.notification.NotificationRequest;
import com.elijahbus.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void consume(NotificationRequest notificationRequest) {
        log.info("Consuming from {}", notificationRequest);
        notificationService.send(notificationRequest);
        log.info("Consumed from {}", notificationRequest);
    }
}
