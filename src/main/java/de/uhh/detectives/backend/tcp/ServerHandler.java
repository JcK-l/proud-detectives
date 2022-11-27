package de.uhh.detectives.backend.tcp;

import de.uhh.detectives.backend.service.api.TcpMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

    private final ServerSocket serverSocket;
    private final List<ClientHandler> connections;
    private final TcpMessageService tcpMessageService;
    private final ExecutorService threadPool;

    public ServerHandler(final int port, final TcpMessageService tcpMessageService) throws IOException {
        this.connections = new ArrayList<>();
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newCachedThreadPool();
        this.tcpMessageService = tcpMessageService;
    }

    public void broadcastMessage(final String message) {
        for (final ClientHandler handler : connections) {
            if (handler.isClosed()) {
                connections.remove(handler);
            } else {
                handler.sendMessage(message);
            }
        }
    }

    @Override
    public void run() {
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                LOG.info("Somebody connected.");
            } catch (IOException e) {
                LOG.error("Accept failed.");
                break;
            }
            final ClientHandler clientHandler = new ClientHandler(this, clientSocket, tcpMessageService);
            connections.add(clientHandler);
            threadPool.execute(clientHandler);
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
