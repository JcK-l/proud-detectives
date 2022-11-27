package de.uhh.detectives.backend.tcp;

import de.uhh.detectives.backend.service.api.TcpMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket clientSocket;
    private final ServerHandler server;
    private final TcpMessageService tcpMessageService;

    private PrintWriter out;
    private ObjectInputStream in;

    public ClientHandler(final ServerHandler server, final Socket client, final TcpMessageService tcpMessageService) {
        this.clientSocket = client;
        this.server = server;
        this.tcpMessageService = tcpMessageService;
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
                final String toBroadcast = tcpMessageService.receiveMessage(inputMessage);
                if (toBroadcast != null) {
                    server.broadcastMessage(toBroadcast);
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

    public boolean isClosed() {
        return clientSocket.isClosed();
    }
}
