package com.iso8583.mock_switch.authorization;

import com.iso8583.mock_switch.iso8583.Iso8583Message;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthorizationService {

    public AuthorizationResult authorize(Iso8583Message request) {

        String pan = request.getField(2);
        String processingCode = request.getField(3);
        String amount = request.getField(4);

        // Basic validation
        if (pan == null || pan.isBlank()) {
            return AuthorizationResult.declined("14");
        }

        if (amount == null || amount.isBlank()) {
            return AuthorizationResult.declined("13");
        }

        // Simulate authorization decision
        int decision = ThreadLocalRandom.current().nextInt(100);

        if (decision < 90) {

            String authorizationCode = generateAuthorizationCode();

            return AuthorizationResult.approved(
                    authorizationCode
            );
        }

        return AuthorizationResult.declined("05");
    }

    private String generateAuthorizationCode() {

        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);

        return String.valueOf(code);
    }
}

