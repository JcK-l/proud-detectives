package de.uhh.detectives.backend.tcp;

import de.uhh.detectives.backend.service.api.GameService;
import de.uhh.detectives.backend.service.api.TcpMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientHandlerTest {

    @InjectMocks
    private ClientHandler testee;

    @Mock
    private Socket clientSocket;

    @Mock
    private ServerHandler server;

    @Mock
    private TcpMessageService tcpMessageService;

    @Mock
    private GameService gameService;

    @Mock
    private ObjectInputStream in;

    @Mock
    private ObjectOutputStream out;

    @Test
    public void testRun() throws IOException {
        // given
        when(in.readUTF()).thenReturn("CLOSE_CONNECTION_FOR:123456789");

        // when
        testee.run();

        // then
        verify(out).close();
        verify(in).close();
        verify(clientSocket).close();
    }

    @Test
    public void testHandleAFewMessages() throws IOException {
        // given
        when(in.readUTF()).thenReturn("OPEN_CONNECTION_FOR:123456789",
                "TYPE:REGISTER_MESSAGE;userId=123456789;pseudonym=testPseudonym;prename=testPrename;surname=testSurname",
                "TYPE:JOIN_GAME_MESSAGE;senderId=123456789",
                "TYPE:START_GAME_MESSAGE;userId=123456789;longitude=-0.158670367;latitude=51.52406527;radius=500",
                "TYPE:WIN_GAME_MESSAGE;senderId=123456789",
                "CLOSE_CONNECTION_FOR:123456789");

        // when
        testee.run();

        // then
        verify(out).close();
        verify(in).close();
        verify(clientSocket).close();
    }

    // TODO write tests for handling of specific messages
}
