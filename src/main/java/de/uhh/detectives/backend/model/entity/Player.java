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
}
