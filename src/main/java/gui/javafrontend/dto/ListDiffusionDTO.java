package gui.javafrontend.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListDiffusionDTO {
    private String type;
    private String chemin;
    private String nom;
    private Long id ;
    private String description;
    private boolean estSysteme;
    private Long projetId;
    private Date dateCreation;

    public ListDiffusionDTO(String type, String chemin, String nom, Long id, String description, boolean estSysteme, Long projetId, Date dateCreation) {
        this.type = type;
        this.chemin = chemin;
        this.nom = nom;
        this.id = id;
        this.description = description;
        this.estSysteme = estSysteme;
        this.projetId = projetId;
        this.dateCreation = dateCreation;
    }

    public ListDiffusionDTO() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getProjetId() {
        return projetId;
    }

    public void setProjetId(Long projetId) {
        this.projetId = projetId;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}
