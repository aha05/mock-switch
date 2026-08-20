package com.iso8583.mock_switch.authorization;

public class AuthorizationResult {

    public enum Status {
        APPROVED,
        DECLINED,
        TIMEOUT
    }

    private final Status status;
    private final String responseCode;
    private final String authorizationCode;

    private AuthorizationResult(
            Status status,
            String responseCode,
            String authorizationCode
    ) {
        this.status = status;
        this.responseCode = responseCode;
        this.authorizationCode = authorizationCode;
    }

    public static AuthorizationResult approved(String authorizationCode) {
        return new AuthorizationResult(
                Status.APPROVED,
                "00",
                authorizationCode
        );
    }

    public static AuthorizationResult declined(String responseCode) {
        return new AuthorizationResult(
                Status.DECLINED,
                responseCode,
                null
        );
    }

    public static AuthorizationResult timeout() {
        return new AuthorizationResult(
                Status.TIMEOUT,
                "91",
                null
        );
    }

    public Status getStatus() {
        return status;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public boolean isApproved() {
        return status == Status.APPROVED;
    }
}
