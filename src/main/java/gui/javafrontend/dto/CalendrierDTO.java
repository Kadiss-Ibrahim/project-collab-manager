package gui.javafrontend.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendrierDTO {

    private Long id;
    private String nom;

    private boolean estPartage;
    @JsonIgnore
    private UtilisateurDTO proprietaire;

    public CalendrierDTO(Long id, String nom, boolean estPartage, UtilisateurDTO proprietaire) {
        this.id = id;
        this.nom = nom;
        this.estPartage = estPartage;
        this.proprietaire = proprietaire;
    }

    public CalendrierDTO() {
    }

    public boolean isEstPartage() {
        return estPartage;
    }

    public void setEstPartage(boolean estPartage) {
        this.estPartage = estPartage;
    }

    public UtilisateurDTO getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(UtilisateurDTO proprietaire) {
        this.proprietaire = proprietaire;
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
}
