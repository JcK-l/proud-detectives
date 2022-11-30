package de.uhh.detectives.backend.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Entity
public class Player implements Serializable {

    @Serial
    private static final long serialVersionUID = -8534287358633192608L;

    private Long id;
    private String prename;
    private String surname;

    private String pseudonym;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getSurname() {
        return surname;
    }

    @NotNull
    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (!getId().equals(player.getId())) return false;
        if (getPrename() != null ? !getPrename().equals(player.getPrename()) : player.getPrename() != null)
            return false;
        if (getSurname() != null ? !getSurname().equals(player.getSurname()) : player.getSurname() != null)
            return false;
        return getPseudonym().equals(player.getPseudonym());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getPrename() != null ? getPrename().hashCode() : 0);
        result = 31 * result + (getSurname() != null ? getSurname().hashCode() : 0);
        result = 31 * result + getPseudonym().hashCode();
        return result;
    }
}
