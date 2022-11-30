package de.uhh.detectives.backend.tcp;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.TcpMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket clientSocket;
    private final ServerHandler server;
    private final TcpMessageService tcpMessageService;
    private final GameService gameService;

    private PrintWriter out;
    private ObjectInputStream in;
    private Long clientUserId;

    public ClientHandler(final ServerHandler server, final Socket client, final TcpMessageService tcpMessageService,
                         final GameService gameService) {
        this.clientSocket = client;
        this.server = server;
        this.tcpMessageService = tcpMessageService;
        this.gameService = gameService;
    }

    @Override
    public void run() {
        try {
            Object input;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new ObjectInputStream(clientSocket.getInputStream());
            while ((input = in.readObject()) != null) {
                final String inputMessage = (String) input;
                LOG.info("Receiving message " + inputMessage);
                if (inputMessage.contains("OPEN_CONNECTION_FOR:")) {
                    clientUserId = Long.valueOf(inputMessage.substring(inputMessage.indexOf(':') + 1));
                }
                final String toBroadcast = tcpMessageService.receiveMessage(inputMessage);
                if (toBroadcast != null) {
                    final Game game = gameService.findActiveGameForUser(clientUserId);
                    final List<Long> userIds = game.getParticipants().stream().map(Player::getId).toList();
                    LOG.info("Broadcasting message " + toBroadcast + " to users of game with id " + game.getGameId());
                    server.broadcastMessage(toBroadcast, userIds);
                }
            }
            shutdown();
        } catch (final ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void shutdown() {
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (final IOException ignored) {
            // cannot do anything about it
        }
    }

    public void sendMessage(final String message) {
        out.println(message);
    }

    public Long getClientUserId() {
        return clientUserId;
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }
}
