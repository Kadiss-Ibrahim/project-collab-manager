package gui.javafrontend.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gui.javafrontend.enums.TypeSalle;

import java.util.Date;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalleDiscussionDTO {
    private Long id;
    private int idSalle;
    private String nom;
    private String description;
    private TypeSalle typeSalle;
    private boolean estPublique;
    private Date dateCreation;
    private Long idProjet;
    private String nomProjet;
    private Long idGroupe;
    private String nomGroupe;
    private Long idCreateur;
    private String nomCreateur;
    private List<UtilisateurDTO> membres;
    private MessageDTO dernierMessage;
    private int nombreMessagesNonLus;

    public SalleDiscussionDTO(Long id, int idSalle, String nom, String description, TypeSalle typeSalle, boolean estPublique, Date dateCreation, Long idProjet, String nomProjet, Long idGroupe, String nomGroupe, Long idCreateur, String nomCreateur, List<UtilisateurDTO> membres, MessageDTO dernierMessage, int nombreMessagesNonLus) {
        this.id = id;
        this.idSalle = idSalle;
        this.nom = nom;
        this.description = description;
        this.typeSalle = typeSalle;
        this.estPublique = estPublique;
        this.dateCreation = dateCreation;
        this.idProjet = idProjet;
        this.nomProjet = nomProjet;
        this.idGroupe = idGroupe;
        this.nomGroupe = nomGroupe;
        this.idCreateur = idCreateur;
        this.nomCreateur = nomCreateur;
        this.membres = membres;
        this.dernierMessage = dernierMessage;
        this.nombreMessagesNonLus = nombreMessagesNonLus;
    }

    public SalleDiscussionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdSalle() {
        return idSalle;
    }

    public void setIdSalle(int idSalle) {
        this.idSalle = idSalle;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeSalle getTypeSalle() {
        return typeSalle;
    }

    public void setTypeSalle(TypeSalle typeSalle) {
        this.typeSalle = typeSalle;
    }

    public boolean isEstPublique() {
        return estPublique;
    }

    public void setEstPublique(boolean estPublique) {
        this.estPublique = estPublique;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Long getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(Long idProjet) {
        this.idProjet = idProjet;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public Long getIdGroupe() {
        return idGroupe;
    }

    public void setIdGroupe(Long idGroupe) {
        this.idGroupe = idGroupe;
    }

    public String getNomGroupe() {
        return nomGroupe;
    }

    public void setNomGroupe(String nomGroupe) {
        this.nomGroupe = nomGroupe;
    }

    public Long getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(Long idCreateur) {
        this.idCreateur = idCreateur;
    }

    public String getNomCreateur() {
        return nomCreateur;
    }

    public void setNomCreateur(String nomCreateur) {
        this.nomCreateur = nomCreateur;
    }

    public List<UtilisateurDTO> getMembres() {
        return membres;
    }

    public void setMembres(List<UtilisateurDTO> membres) {
        this.membres = membres;
    }

    public MessageDTO getDernierMessage() {
        return dernierMessage;
    }

    public void setDernierMessage(MessageDTO dernierMessage) {
        this.dernierMessage = dernierMessage;
    }

    public int getNombreMessagesNonLus() {
        return nombreMessagesNonLus;
    }

    public void setNombreMessagesNonLus(int nombreMessagesNonLus) {
        this.nombreMessagesNonLus = nombreMessagesNonLus;
    }
}

