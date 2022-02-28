package com.elijahbus.customer;

import com.elijahbus.clients.fraud.FraudCheckResponse;
import com.elijahbus.clients.fraud.FraudClient;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public record CustomerService(RestTemplate restTemplate,
                              CustomerRepository customerRepository,
                              FraudClient fraudClient) {

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

    }
}
