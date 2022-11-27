package de.uhh.detectives.backend.tcp.config;

import de.uhh.detectives.backend.service.api.TcpMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Configuration
public class TcpServerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(TcpServerConfig.class);

    @Value("${tcp.server.port}")
    private int port;

    ServerSocket serverSocket = null;

    final TcpMessageService tcpMessageService;

    public TcpServerConfig(final TcpMessageService tcpMessageService) {
        this.tcpMessageService = tcpMessageService;
    }

    @Bean
    public void serverConnectionFactory() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOG.error("Could not listen on port: " + port);
            System.exit(1);
        }
        Socket clientSocket;

        while (true) {

            try {
                clientSocket = serverSocket.accept();
                LOG.info("Somebody connected.");
            } catch (IOException e) {
                LOG.error("Accept failed.");
                break;
            }
            final Thread clientThread = new Thread(handleClientSocket(clientSocket));
            clientThread.start();
        }
        serverSocket.close();
    }

    private Runnable handleClientSocket(final Socket clientSocket) {
        return () -> {
            Object input;
            PrintWriter out;
            ObjectInputStream in;

            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new ObjectInputStream(clientSocket.getInputStream());
                while ((input = in.readObject()) != null) {
                    final String inputMessage = (String) input;
                    LOG.info("Received:" + inputMessage);
                    out.println("this comes from the backend");
                }
                out.close();
                in.close();
                clientSocket.close();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        };
    }

}
