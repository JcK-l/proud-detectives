package de.uhh.detectives.backend.tcp.config;

import de.uhh.detectives.backend.service.api.TcpMessageService;
import de.uhh.detectives.backend.tcp.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class TcpServerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(TcpServerConfig.class);

    @Value("${tcp.server.port}")
    private int port;

    private final TcpMessageService tcpMessageService;


    public TcpServerConfig(final TcpMessageService tcpMessageService) {
        this.tcpMessageService = tcpMessageService;
    }

    @Bean
    public void serverConnectionFactory() {
        try {
            final Runnable serverHandler = new ServerHandler(port, tcpMessageService);
            final Thread connectionThread = new Thread(serverHandler);
            connectionThread.start();
        } catch (final IOException e) {
            LOG.error("Could not open connection on server socket. Critical error");
            e.printStackTrace();
            System.exit(1);
        }
    }

}
