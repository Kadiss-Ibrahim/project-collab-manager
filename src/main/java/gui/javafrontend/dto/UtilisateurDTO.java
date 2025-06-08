package gui.javafrontend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gui.javafrontend.enums.RoleSecondaire;
import gui.javafrontend.enums.RoleType;

import java.util.Date;
@JsonIgnoreProperties(ignoreUnknown = true)
public class UtilisateurDTO {
    private Long id;
    private String identifiant;
    private String nom;
    private String prenom;
    private String email;
    private byte[] photoProfile;
    private String avatar;
    private boolean actif;
    private boolean estEnLigne;
    private boolean estConnecte;
    private Date derniereConnexion;
    private String motDePasse;
    private RoleType role;
    private RoleSecondaire roleSecondaire;

    // OAuth2 fields
    private String providerId;
    private String provider;

    private String refreshToken;

    private Date dateInscription;
    private Date dateCreation;
    private Date dateModification;
    private Long projetId;    // ID du projet concerné
    private Long adminId;

    public UtilisateurDTO() {
    }

    public UtilisateurDTO(Long id, String identifiant, String nom, String prenom, String email, byte[] photoProfile, String avatar, boolean actif, boolean estEnLigne, boolean estConnecte, Date derniereConnexion, String motDePasse, RoleType role, RoleSecondaire roleSecondaire, String providerId, String provider, String refreshToken, Date dateInscription, Date dateCreation, Date dateModification, Long projetId, Long adminId) {
        this.id = id;
        this.identifiant = identifiant;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.photoProfile = photoProfile;
        this.avatar = avatar;
        this.actif = actif;
        this.estEnLigne = estEnLigne;
        this.estConnecte = estConnecte;
        this.derniereConnexion = derniereConnexion;
        this.motDePasse = motDePasse;
        this.role = role;
        this.roleSecondaire = roleSecondaire;
        this.providerId = providerId;
        this.provider = provider;
        this.refreshToken = refreshToken;
        this.dateInscription = dateInscription;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
        this.projetId = projetId;
        this.adminId = adminId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(byte[] photoProfile) {
        this.photoProfile = photoProfile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean isEstEnLigne() {
        return estEnLigne;
    }

    public void setEstEnLigne(boolean estEnLigne) {
        this.estEnLigne = estEnLigne;
    }

    public boolean isEstConnecte() {
        return estConnecte;
    }

    public void setEstConnecte(boolean estConnecte) {
        this.estConnecte = estConnecte;
    }

    public Date getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(Date derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public RoleSecondaire getRoleSecondaire() {
        return roleSecondaire;
    }

    public void setRoleSecondaire(RoleSecondaire roleSecondaire) {
        this.roleSecondaire = roleSecondaire;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateModification() {
        return dateModification;
    }

    public void setDateModification(Date dateModification) {
        this.dateModification = dateModification;
    }

    public Long getProjetId() {
        return projetId;
    }

    public void setProjetId(Long projetId) {
        this.projetId = projetId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}