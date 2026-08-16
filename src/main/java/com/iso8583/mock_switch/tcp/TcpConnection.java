package com.iso8583.mock_switch.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpConnection {

    private final Socket socket;

    private final BufferedReader reader;
    private final PrintWriter writer;

    public TcpConnection(Socket socket) throws IOException {
        this.socket = socket;

        this.reader = new BufferedReader( // to read char as string
                new InputStreamReader( // this convert bytes into char
                        socket.getInputStream(), // the works with bytes - from
                        StandardCharsets.UTF_8 // char - to
                )
        );

        this.writer = new PrintWriter( // work in text, the text will be converted to bytes
                socket.getOutputStream(), // work in bytes
                true // auto-flush - meaning the text or string will automatically write, when new line detected, no explicit flush required for writer
        );
    }

    public String receive() throws IOException {
        return reader.readLine();
    }

    public void send(String message) {
        writer.println(message);
    }

    public String getRemoteAddress() {
        return socket.getRemoteSocketAddress().toString();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(
                    "Failed to close connection: " + e.getMessage()
            );
        }
    }
}
