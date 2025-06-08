package gui.javafrontend.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReunionDTO {


    private Long id;

    private String titre;

    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    private Date date;

    private String lienMeet;

    private int duree;
    private  Long idProjet;


    private boolean estObligatoire;

    public ReunionDTO(Long id, String titre, String description, Date date, String lienMeet, int duree, Long idProjet, boolean estObligatoire) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.lienMeet = lienMeet;
        this.duree = duree;
        this.idProjet = idProjet;
        this.estObligatoire = estObligatoire;
    }

    public ReunionDTO() {
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLienMeet() {
        return lienMeet;
    }

    public void setLienMeet(String lienMeet) {
        this.lienMeet = lienMeet;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public Long getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(Long idProjet) {
        this.idProjet = idProjet;
    }

    public boolean isEstObligatoire() {
        return estObligatoire;
    }

    public void setEstObligatoire(boolean estObligatoire) {
        this.estObligatoire = estObligatoire;
    }
}
