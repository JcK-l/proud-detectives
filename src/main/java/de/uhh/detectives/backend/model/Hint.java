package de.uhh.detectives.backend.model;

import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.enumeration.HintItem;
import lombok.Data;

@Data
public class Hint {

    private HintItem item;
    private Player possessor;

    private float longitude;
    private float latitude;
}
