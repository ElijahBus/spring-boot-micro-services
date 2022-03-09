package com.elijahbus.customer;

import com.elijahbus.amqp.RabbitMQMessageProducer;
import com.elijahbus.clients.fraud.FraudCheckResponse;
import com.elijahbus.clients.fraud.FraudClient;
import com.elijahbus.clients.notification.NotificationClient;
import com.elijahbus.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
    private final CustomerRepository customerRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public void registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        Customer customer = Customer.builder()
                .firstName(customerRegistrationRequest.firstName())
                .lastName(customerRegistrationRequest.lastName())
                .email(customerRegistrationRequest.email())
                .build();

        customerRepository.saveAndFlush(customer);

        // Check if customer is fraudster
        FraudCheckResponse fraudCheckResponse;
        fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        assert fraudCheckResponse != null;
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("You are a fraudster");
        }

        // Send a notification to the customer
        // TODO: make it async, i.e: Add to the queue
        sendNotification(customer);

    }

    private void sendNotification(Customer customer) {
        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi, welcome %s to elijahbus services!", customer.getFirstName())
        );

        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }
}
