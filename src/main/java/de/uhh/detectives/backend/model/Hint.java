package de.uhh.detectives.backend.model;

import de.uhh.detectives.backend.model.entity.Player;
import de.uhh.detectives.backend.model.enumeration.HintItem;
import lombok.Data;

@Data
public class Hint {

    private HintItem item;
    private Player possessor;

    private Float longitude;
    private Float latitude;

    public Hint(final HintItem item) {
        this.item = item;
    }
}
