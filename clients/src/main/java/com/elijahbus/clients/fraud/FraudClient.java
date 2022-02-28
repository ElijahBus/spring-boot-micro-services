package com.elijahbus.clients.fraud;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "fraud", fallback = FraudClient.FraudClientFallbackFactory.class)
public interface FraudClient {

    @GetMapping(path = "api/v1/fraud-check/{customerId}")
    FraudCheckResponse isFraudster(@PathVariable("customerId") Integer customerId) throws IllegalStateException;

    @Component
    public static class FraudClientFallbackFactory implements FallbackFactory<FraudClient> {

        @Override
        public FraudClient create(Throwable cause) {
            System.out.println("Falling back to the illegal state exception\n");

            return new FraudClient() {
                @Override
                public FraudCheckResponse isFraudster(Integer customerId) throws IllegalStateException{
                    throw new IllegalStateException(
                            "Feign client (Customer) could not connect to FRAUD microservice; Reason was: {}", cause.getCause());
                }
            };
        }
    }
}