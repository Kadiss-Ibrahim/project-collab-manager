package gui.javafrontend.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gui.javafrontend.enums.MessageType;

import java.util.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {
    private Long id;
    private String contenu;
    private Date dateEnvoi;
    private boolean estLu;
    private MessageType type;
    private Long idExpediteur;
    private String nomExpediteur;
    private String avatarExpediteur;
    private Long idSalle;
    private String nomSalle;

    public MessageDTO(Long id, String contenu, Date dateEnvoi, boolean estLu, MessageType type, Long idExpediteur, String nomExpediteur, String avatarExpediteur, Long idSalle, String nomSalle) {
        this.id = id;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.estLu = estLu;
        this.type = type;
        this.idExpediteur = idExpediteur;
        this.nomExpediteur = nomExpediteur;
        this.avatarExpediteur = avatarExpediteur;
        this.idSalle = idSalle;
        this.nomSalle = nomSalle;
    }

    public MessageDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(Date dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public boolean isEstLu() {
        return estLu;
    }

    public void setEstLu(boolean estLu) {
        this.estLu = estLu;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Long getIdExpediteur() {
        return idExpediteur;
    }

    public void setIdExpediteur(Long idExpediteur) {
        this.idExpediteur = idExpediteur;
    }

    public String getNomExpediteur() {
        return nomExpediteur;
    }

    public void setNomExpediteur(String nomExpediteur) {
        this.nomExpediteur = nomExpediteur;
    }

    public String getAvatarExpediteur() {
        return avatarExpediteur;
    }

    public void setAvatarExpediteur(String avatarExpediteur) {
        this.avatarExpediteur = avatarExpediteur;
    }

    public Long getIdSalle() {
        return idSalle;
    }

    public void setIdSalle(Long idSalle) {
        this.idSalle = idSalle;
    }

    public String getNomSalle() {
        return nomSalle;
    }

    public void setNomSalle(String nomSalle) {
        this.nomSalle = nomSalle;
    }
}

