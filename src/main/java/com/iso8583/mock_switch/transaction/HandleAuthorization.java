package com.iso8583.mock_switch.transaction;

import com.iso8583.mock_switch.authorization.AuthorizationResult;
import com.iso8583.mock_switch.authorization.AuthorizationService;
import com.iso8583.mock_switch.iso8583.Iso8583Message;
import org.springframework.stereotype.Component;

@Component
public class HandleAuthorization {
    private final AuthorizationService authorizationService;

    HandleAuthorization(AuthorizationService authorizationService){
        this.authorizationService = authorizationService;
    }

    public Iso8583Message handle(Iso8583Message request) {

        AuthorizationResult result =
                authorizationService.authorize(request);

        Iso8583Message response = new Iso8583Message();

        // Your custom response MTI
        response.setMti("0110");

        response.setField(11, request.getField(11));

        // Response code
        response.setField(
                39,
                result.getResponseCode()
        );

        // Authorization code
        if (result.isApproved()) {
            response.setField(
                    38,
                    result.getAuthorizationCode()
            );
        }

        return response;
    }
}
