package com.iso8583.mock_switch.client.config;

import com.iso8583.mock_switch.exception.ClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
   @Value("${api.baseUrl}")
   private String baseUrl;

   @Bean
    public RestClient restClient(RestClient.Builder builder){
       return builder
               .baseUrl(baseUrl)
               .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                  // Log or throw a global app exception
                  throw new ClientException("API Error: " + response.getStatusCode());
               })
               .build();
   }
}
