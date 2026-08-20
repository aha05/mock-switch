package com.iso8583.mock_switch.tcp;

import com.iso8583.mock_switch.iso8583.Iso8583Builder;
import com.iso8583.mock_switch.iso8583.Iso8583Message;
import com.iso8583.mock_switch.iso8583.Iso8583Parser;
import com.iso8583.mock_switch.transaction.TransactionRouter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ConnectionHandler {
    private final Iso8583Parser iso8583Parser;
    private final Iso8583Builder iso8583Builder;
    private final TransactionRouter transactionRouter;

    public ConnectionHandler(
            Iso8583Parser iso8583Parser,
            Iso8583Builder iso8583Builder,
            TransactionRouter transactionRouter
    ) {
        this.iso8583Parser = iso8583Parser;
        this.iso8583Builder = iso8583Builder;
        this.transactionRouter = transactionRouter;
    }

    public void handle(Socket socket) {

        TcpConnection connection = null;

        try {
            connection = new TcpConnection(socket);

            System.out.println(
                    "Connection established: " +
                            connection.getRemoteAddress()
            );

            List<String> messages = new ArrayList<>();

            String rawMessage;

            while ((rawMessage = connection.receive()) != null) {

                messages.add(rawMessage);

                System.out.println(
                        "Received from POS: " + rawMessage
                );

                try {
                    byte[] rawBytes =
                            rawMessage.getBytes(StandardCharsets.UTF_8);

                    Iso8583Message request =
                            iso8583Parser.parse(rawBytes);

                    System.out.println(
                            "ISO8583 request parsed. MTI: " +
                                    request.getMti() + "\nISO 8583 fields: " +
                                    request.getFields()
                    );

                    Iso8583Message response =
                            transactionRouter.route(request);

                    /*
                     * 3. Build ISO8583 response
                     */

                    byte[] responseBytes =
                            iso8583Builder.build(response);

                    System.out.println("Response Bytes: " +
                            new String(responseBytes, StandardCharsets.UTF_8));

                    String responseMessage =
                            new String(responseBytes, StandardCharsets.UTF_8);

                    connection.send(responseMessage);

                    System.out.println(
                            "Response sent to POS. MTI: " +
                                    response.getMti()
                    );

                }
                catch (Exception e) {
                    System.err.println(
                            "Error processing ISO8583 message: " +
                                    e.getMessage()
                    );

                    e.printStackTrace();
                }
            }

        } catch (IOException e) {

            System.err.println(
                    "Connection error: " + e.getMessage()
            );

        } finally {

            if (connection != null) {
                connection.close();
            }

            System.out.println(
                    "POS connection closed: " +
                            socket.getRemoteSocketAddress()
            );
        }
    }
}
