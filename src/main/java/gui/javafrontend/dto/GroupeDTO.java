package gui.javafrontend.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Date;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupeDTO {


        private Long id;
        private String nom;
        private String description;
        private boolean estSysteme;
        private Date dateCreation;

        private List<UtilisateurDTO> membres; // Only include essential member info

    public GroupeDTO(Long id, String nom, String description, boolean estSysteme, Date dateCreation, List<UtilisateurDTO> membres) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.estSysteme = estSysteme;
        this.dateCreation = dateCreation;
        this.membres = membres;
    }

    public GroupeDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isEstSysteme() {
        return estSysteme;
    }

    public void setEstSysteme(boolean estSysteme) {
        this.estSysteme = estSysteme;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<UtilisateurDTO> getMembres() {
        return membres;
    }

    public void setMembres(List<UtilisateurDTO> membres) {
        this.membres = membres;
    }
}
