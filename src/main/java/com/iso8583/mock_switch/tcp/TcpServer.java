package com.iso8583.mock_switch.tcp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TcpServer {

    @Value("${mock-switch.tcp.port:8583}")
    private int port;

    private ServerSocket serverSocket;

    private final ExecutorService executorService =
            Executors.newCachedThreadPool();

    private final ConnectionHandler connectionHandler;

    public TcpServer(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @PostConstruct // it is life-cycle annotation, After Spring creates this object (new TcpServer()) and finishes injecting its dependencies, call the method marked with @PostConstruct.
    public void start() {
        executorService.submit(() -> { // this will create a background task, submit() - Execute this code using a thread managed by the executor.
            try {
                serverSocket = new ServerSocket(port);

                System.out.println("Mock Switch TCP server started on port " + port);

                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();

                    System.out.println(
                            "POS connected: " +
                                    socket.getRemoteSocketAddress()
                    );

                    executorService.submit(
                            () -> connectionHandler.handle(socket)
                    );
                }

            } catch (IOException e) {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    System.err.println("TCP server error: " + e.getMessage());
                }
            }
        });
    }

    @PreDestroy
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to stop TCP server: " + e.getMessage());
        }

        executorService.shutdown();
    }
}