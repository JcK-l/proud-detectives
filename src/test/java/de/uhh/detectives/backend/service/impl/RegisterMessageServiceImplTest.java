package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.model.Player;
import de.uhh.detectives.backend.model.RegisterMessage;
import de.uhh.detectives.backend.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RegisterMessageServiceImplTest {

    @InjectMocks
    private RegisterMessageServiceImpl testee;

    @Mock
    private PlayerRepository playerRepository;

    @Test
    public void testHandle(){
        // given
        final RegisterMessage registerMessage = new RegisterMessage();
        registerMessage.setSurname("testSurname");
        registerMessage.setPrename("testPrename");
        registerMessage.setUserId(123456789L);
        registerMessage.setPseudonym("testPseudonym");

        final ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);

        // when
        final String result = testee.handle(registerMessage);

        // then
        assertEquals("ACKNOWLEDGED", result);
        verify(playerRepository).save(captor.capture());
        final Player player = captor.getValue();

        assertEquals("testSurname", player.getSurname());
        assertEquals("testPrename", player.getPrename());
        assertEquals(123456789, player.getId());
        assertEquals("testPseudonym", player.getPseudonym());
    }


}
