package com.iso8583.mock_switch.transaction;

import com.iso8583.mock_switch.iso8583.Iso8583Message;
import org.springframework.stereotype.Component;

@Component
public class TransactionRouter {

    private final PurchaseHandler purchaseHandler;
    private final ReversalHandler reversalHandler;
    private final HandleAuthorization handleAuthorization;

    public TransactionRouter(
            PurchaseHandler purchaseHandler,
            ReversalHandler reversalHandler,
            HandleAuthorization handleAuthorization
    ) {
        this.purchaseHandler = purchaseHandler;
        this.reversalHandler = reversalHandler;
        this.handleAuthorization = handleAuthorization;
    }

    public Iso8583Message route(Iso8583Message request) {

        String mti = request.getMti();

        if (mti == null || mti.isBlank()) {
            throw new IllegalArgumentException(
                    "MTI is missing"
            );
        }

        return switch (mti) {

            case "0010" -> handleAuthorization.handle(request);

            case "0200" -> purchaseHandler.handle(request);

            case "0400" -> reversalHandler.handle(request);

            default -> throw new IllegalArgumentException(
                    "Unsupported MTI: " + mti
            );
        };
    }
}