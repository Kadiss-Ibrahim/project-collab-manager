package gui.javafrontend.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepotDocumentDTO {

    private Long id;

    private String nom;

    private String type;

    private String chemin;

    private Date dateCreation;
    private boolean estPublic;
    @JsonIgnore
    private ProjetDTO projet;

    public DepotDocumentDTO(Long id, String nom, String type, String chemin, Date dateCreation, boolean estPublic, ProjetDTO projet) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.chemin = chemin;
        this.dateCreation = dateCreation;
        this.estPublic = estPublic;
        this.projet = projet;
    }

    public DepotDocumentDTO() {
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

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isEstPublic() {
        return estPublic;
    }

    public void setEstPublic(boolean estPublic) {
        this.estPublic = estPublic;
    }

    public ProjetDTO getProjet() {
        return projet;
    }

    public void setProjet(ProjetDTO projet) {
        this.projet = projet;
    }
}
