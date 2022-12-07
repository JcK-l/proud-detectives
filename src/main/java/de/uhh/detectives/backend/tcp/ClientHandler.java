package de.uhh.detectives.backend.tcp;

import de.uhh.detectives.backend.model.entity.Game;
import de.uhh.detectives.backend.model.entity.Participant;
import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.TcpMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket clientSocket;
    private final ServerHandler server;
    private final TcpMessageService tcpMessageService;
    private final GameService gameService;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    private Long clientUserId;

    public ClientHandler(final ServerHandler server, final Socket client, final TcpMessageService tcpMessageService,
                         final ObjectInputStream in, final ObjectOutputStream out, final GameService gameService) {
        this.clientSocket = client;
        this.server = server;
        this.tcpMessageService = tcpMessageService;
        this.gameService = gameService;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            while (true) {
                final String inputMessage = in.readUTF();
                LOG.info("Receiving message " + inputMessage);
                if (inputMessage.contains("OPEN_CONNECTION_FOR:")) {
                    clientUserId = Long.valueOf(inputMessage.substring(inputMessage.indexOf(':') + 1));
                    continue;
                }
                if (inputMessage.contains("CLOSE_CONNECTION_FOR:")) {
                    break;
                }
                final String toBroadcast = tcpMessageService.receiveMessage(inputMessage);
                broadcastToClient(toBroadcast);
                Game gameForUser;
                if (toBroadcast != null && toBroadcast.startsWith("TYPE:END_GAME_MESSAGE;status=200")) {
                    gameForUser = gameService.findLatestCompletedGameForUser(clientUserId);
                } else {
                    gameForUser = gameService.findActiveGameForUser(clientUserId);
                }
                if (toBroadcast != null && gameForUser != null) {
                    broadcastToOtherPlayers(toBroadcast, gameForUser);
                }
            }
            shutdown();
        } catch (final IOException e) {
            LOG.error(e.getMessage());
            shutdown();
        }
    }

    private void broadcastToClient(final String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    private void broadcastToOtherPlayers(final String toBroadcast, final Game game) throws IOException {
        if (toBroadcast != null && game != null) {
            final Set<Long> userIds = game.getParticipants().stream()
                    .map(Participant::getPlayer)
                    .map(Player::getId)
                    .collect(Collectors.toSet());
            userIds.remove(clientUserId);
            LOG.info("Broadcasting message " + toBroadcast + " to users of game with id " + game.getGameId());
            server.broadcastMessage(toBroadcast, userIds);
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

    public void sendMessage(final String message) throws IOException {
        out.writeUTF(message);
        out.flush();
    }

    public Long getClientUserId() {
        return clientUserId;
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }
}
