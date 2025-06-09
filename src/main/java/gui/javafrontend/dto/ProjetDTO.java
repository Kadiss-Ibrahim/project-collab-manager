package gui.javafrontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gui.javafrontend.enums.StatutProjet;

import java.sql.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjetDTO {
    private Long id;
    private Long createurId;
    @JsonProperty("groupeId")  // Important pour la sérialisation JSON
    private Long groupeId;// Correction: camelCase au lieu de CreateurId

    private Long utilisateurId; // Ajouté pour les opérations de mise à jour/suppression
    private Long adminId;     // Ajouté pour les opérations d'administration
    private String nomCourt;
    private String nomLong;
    private String description;
    private String theme;
    private String type;
    private boolean estPublic;
    private String license;
    private StatutProjet statutProjet;
    private Date dateAcceptation;
    private Date dateRejet;
    private Date dateCreation;
    private Date dateCloture;

    public ProjetDTO(Long id, Long createurId, Long groupeId, Long utilisateurId, Long adminId, String nomCourt, String nomLong, String description, String theme, String type, boolean estPublic, String license, StatutProjet statutProjet, Date dateAcceptation, Date dateRejet, Date dateCreation, Date dateCloture) {
        this.id = id;
        this.createurId = createurId;
        this.groupeId = groupeId;
        this.utilisateurId = utilisateurId;
        this.adminId = adminId;
        this.nomCourt = nomCourt;
        this.nomLong = nomLong;
        this.description = description;
        this.theme = theme;
        this.type = type;
        this.estPublic = estPublic;
        this.license = license;
        this.statutProjet = statutProjet;
        this.dateAcceptation = dateAcceptation;
        this.dateRejet = dateRejet;
        this.dateCreation = dateCreation;
        this.dateCloture = dateCloture;
    }

    public ProjetDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateurId() {
        return createurId;
    }

    public void setCreateurId(Long createurId) {
        this.createurId = createurId;
    }

    public Long getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Long groupeId) {
        this.groupeId = groupeId;
    }

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getNomCourt() {
        return nomCourt;
    }

    public void setNomCourt(String nomCourt) {
        this.nomCourt = nomCourt;
    }

    public String getNomLong() {
        return nomLong;
    }

    public void setNomLong(String nomLong) {
        this.nomLong = nomLong;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEstPublic() {
        return estPublic;
    }

    public void setEstPublic(boolean estPublic) {
        this.estPublic = estPublic;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public StatutProjet getStatutProjet() {
        return statutProjet;
    }

    public void setStatutProjet(StatutProjet statutProjet) {
        this.statutProjet = statutProjet;
    }

    public Date getDateAcceptation() {
        return dateAcceptation;
    }

    public void setDateAcceptation(Date dateAcceptation) {
        this.dateAcceptation = dateAcceptation;
    }

    public Date getDateRejet() {
        return dateRejet;
    }

    public void setDateRejet(Date dateRejet) {
        this.dateRejet = dateRejet;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(Date dateCloture) {
        this.dateCloture = dateCloture;
    }

    /**
     * Gets the project status as a string
     * @return the status string representation
     */
    public String getStatut() {
        return statutProjet != null ? statutProjet.name() : null;
    }

    /**
     * Sets the project status from a string
     * @param statut the status string
     */
    public void setStatut(String statut) {
        if (statut != null) {
            try {
                this.statutProjet = StatutProjet.valueOf(statut.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Handle invalid status values gracefully
                System.err.println("Invalid status value: " + statut);
                this.statutProjet = null;
            }
        } else {
            this.statutProjet = null;
        }
    }

    /**
     * Gets the project name (alias for getNomCourt for consistency)
     * @return the short name of the project
     */
    public String getNom() {
        return nomCourt;
    }

    /**
     * Sets the project name (alias for setNomCourt for consistency)
     * @param nom the short name of the project
     */
    public void setNom(String nom) {
        this.nomCourt = nom;
    }
}