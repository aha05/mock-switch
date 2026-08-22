package com.iso8583.mock_switch.exception;

public class ClientException extends RuntimeException {
    public ClientException(String statusCode) {
        super(statusCode);
    }
}
