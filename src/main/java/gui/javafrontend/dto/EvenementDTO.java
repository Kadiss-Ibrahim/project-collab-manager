package gui.javafrontend.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EvenementDTO {

    private Long id;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String lieu;
    private boolean estRecurrent;
    private Long calendrierId;

    public EvenementDTO(Long id, String titre, String description, Date dateDebut, Date dateFin, String lieu, boolean estRecurrent, Long calendrierId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.lieu = lieu;
        this.estRecurrent = estRecurrent;
        this.calendrierId = calendrierId;
    }

    public EvenementDTO() {
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

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public boolean isEstRecurrent() {
        return estRecurrent;
    }

    public void setEstRecurrent(boolean estRecurrent) {
        this.estRecurrent = estRecurrent;
    }

    public Long getCalendrierId() {
        return calendrierId;
    }

    public void setCalendrierId(Long calendrierId) {
        this.calendrierId = calendrierId;
    }
}
