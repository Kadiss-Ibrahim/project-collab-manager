package gui.javafrontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TacheDTO {

    private Long id;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private int priorite;
    private int difficulte;
    private String etat;
    private int notation;
    private ProjetDTO projet;

    public TacheDTO() {
    }

    public TacheDTO(Long id, String titre, String description, Date dateDebut, Date dateFin, int priorite, int difficulte, String etat, int notation, ProjetDTO projet, UtilisateurDTO assigneA) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.priorite = priorite;
        this.difficulte = difficulte;
        this.etat = etat;
        this.notation = notation;
        this.projet = projet;
        this.assigneA = assigneA;
    }

    @JsonIgnore
    private UtilisateurDTO assigneA;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    public int getDifficulte() {
        return difficulte;
    }

    public void setDifficulte(int difficulte) {
        this.difficulte = difficulte;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public int getNotation() {
        return notation;
    }

    public void setNotation(int notation) {
        this.notation = notation;
    }

    public ProjetDTO getProjet() {
        return projet;
    }

    public void setProjet(ProjetDTO projet) {
        this.projet = projet;
    }

    public UtilisateurDTO getAssigneA() {
        return assigneA;
    }

    public void setAssigneA(UtilisateurDTO assigneA) {
        this.assigneA = assigneA;
    }
}