package de.uhh.detectives.backend.tcp;

import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.TcpMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

    private final ServerSocket serverSocket;
    private final List<ClientHandler> connections;
    private final TcpMessageService tcpMessageService;
    private final GameService gameService;
    private final ExecutorService threadPool;

    public ServerHandler(final int port, final TcpMessageService tcpMessageService, final GameService gameService) throws IOException {
        this.connections = new ArrayList<>();
        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newCachedThreadPool();
        this.tcpMessageService = tcpMessageService;
        this.gameService = gameService;
    }

    public void broadcastMessage(final String message, final Set<Long> userIds) throws IOException {
        List<ClientHandler> handlersToRemove = new ArrayList<>();
        for (final ClientHandler handler : connections) {
            if (handler.isClosed()) {
                handlersToRemove.add(handler);
                continue;
            }
            if (userIds.contains(handler.getClientUserId())) {
                handler.sendMessage(message);
            }
        }
        connections.removeAll(handlersToRemove);
    }

    @Override
    public void run() {
        Socket clientSocket;
        ObjectOutputStream out;
        ObjectInputStream in;
        while (true) {
            try {
                LOG.info("Listening for connections on port " + serverSocket.getLocalPort());
                clientSocket = serverSocket.accept();
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                LOG.info("Somebody connected.");
            } catch (IOException e) {
                LOG.error("Accept failed.");
                break;
            }
            final ClientHandler clientHandler = new ClientHandler(this, clientSocket, tcpMessageService, in, out, gameService);
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
