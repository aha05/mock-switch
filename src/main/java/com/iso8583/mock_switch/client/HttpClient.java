package com.iso8583.mock_switch.client;

import com.iso8583.mock_switch.client.dto.JsonRequest;
import com.iso8583.mock_switch.client.dto.JsonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
@RequiredArgsConstructor
public class HttpClient {
    private final RestClient restClient;
    private final ClientProperty clientProperty;

    public JsonResponse postPayment(JsonRequest request) {
        System.out.println(request);
        return restClient.post()
                .uri(clientProperty.getPaymentApi())
                .contentType(MediaType.APPLICATION_JSON)
                .body(JsonRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, (req, res) -> {
                    System.out.println("post payment success!");
                })
                .body(JsonResponse.class);

    }

}
