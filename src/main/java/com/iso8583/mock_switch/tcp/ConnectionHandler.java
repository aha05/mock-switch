package com.iso8583.mock_switch.tcp;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Socket;

@Component
public class ConnectionHandler {

    public void handle(Socket socket) {

        TcpConnection connection = null;

        try {
            connection = new TcpConnection(socket);

            System.out.println(
                    "Connection established: " +
                            connection.getRemoteAddress()
            );

            String message;

            while ((message = connection.receive()) != null) {

                System.out.println(
                        "Received from POS: " + message
                );

                // Temporary response.
                // Later this will be replaced by:
                //
                // ISO8583 Parser
                //       ↓
                // Transaction Router
                //       ↓
                // ISO8583 Builder

                connection.send(
                        "HELLO FROM MOCK SWITCH"
                );

                System.out.println("message sent!");
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
