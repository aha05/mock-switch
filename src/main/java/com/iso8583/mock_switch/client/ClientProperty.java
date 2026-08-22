package com.iso8583.mock_switch.client;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ClientProperty {
    @Value("${api.paymentApi}")
    private String paymentApi;
}
