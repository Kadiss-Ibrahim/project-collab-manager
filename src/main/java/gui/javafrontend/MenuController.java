package gui.javafrontend;

import gui.javafrontend.service.AuthService;
import gui.javafrontend.dto.UtilisateurDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class MenuController implements Initializable {
    @FXML private Button accueilButton;
    @FXML private Button calendrierButton;
    @FXML private Button deconnexionButton;
    @FXML private ImageView image1;
    @FXML private Label nomUser;
    @FXML private Button profileButton;
    @FXML private Button projetsButton;
    @FXML private Button tachesButton;
    @FXML private Button informatiqueButton;
    @FXML private Button chimieButton;
    @FXML private Button systemeEmbarqueButton;
    @FXML private Button biologieButton;
    @FXML private Button rechercheButton;

    private AuthService authService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            authService = AuthService.getInstance();
            if (authService == null || !authService.isLoggedIn()) {
                redirectToLogin();
                return;
            }
            loadUserInfo();
            setupUI();
        } catch (Exception e) {
            redirectToLogin();
        }
    }

    private void loadUserInfo() {
        try {
            if (authService != null && authService.isLoggedIn()) {
                authService.getCurrentUser().thenAccept(utilisateur -> {
                    Platform.runLater(() -> {
                        try {
                            nomUser.setText(buildDisplayName(utilisateur));
                        } catch (Exception e) {
                            nomUser.setText("Utilisateur");
                        }
                    });
                }).exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        nomUser.setText("Utilisateur");
                        redirectToLogin();
                    });
                    return null;
                });
            } else {
                nomUser.setText("Utilisateur");
            }
        } catch (Exception e) {
            nomUser.setText("Utilisateur");
        }
    }

    private String buildDisplayName(UtilisateurDTO utilisateur) {
        if (utilisateur == null) return "Utilisateur";
        String prenom = utilisateur.getPrenom() != null ? utilisateur.getPrenom().trim() : "";
        String nom = utilisateur.getNom() != null ? utilisateur.getNom().trim() : "";
        return (!prenom.isEmpty() || !nom.isEmpty()) ? (prenom + " " + nom).trim() : (utilisateur.getEmail() != null ? utilisateur.getEmail().trim() : "Utilisateur");
    }

    private void setupUI() {
        // Any UI setup if necessary
    }

    @FXML
    void goToAccueil(ActionEvent event) { navigate("accueil.fxml"); }

    @FXML
    void goToProjets(ActionEvent event) { navigate("projets.fxml"); }

    @FXML
    void goToRecherche(ActionEvent event) { navigate("recherche.fxml"); }

    @FXML
    void goToTaches(ActionEvent event) { navigate("taches.fxml"); }

    @FXML
    void goToCalendrier(ActionEvent event) { navigate("calendrier.fxml"); }

    @FXML
    void goToProfile(ActionEvent event) { navigate("profile.fxml"); }

    @FXML
    void goToInformatique(ActionEvent event) { navigateToThemeProjects("INFORMATIQUE"); }

    @FXML
    void goToChimie(ActionEvent event) { navigateToThemeProjects("CHIMIE"); }

    @FXML
    void goToSystemeEmbarque(ActionEvent event) { navigateToThemeProjects("SYSTEME_EMBARQUE"); }

    @FXML
    void goToBiologie(ActionEvent event) { navigateToThemeProjects("BIOLOGIE"); }

    private void navigateToThemeProjects(String theme) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("recherche.fxml"));
            HelloApplication.getScene().setRoot(loader.load());
            Platform.runLater(() -> {
                try {
                    RechercheController controller = loader.getController();
                    controller.rechercherAvecTerme(theme.toLowerCase());
                } catch (Exception ignored) {}
            });
        } catch (IOException ignored) {}
    }

    private void navigate(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            HelloApplication.getScene().setRoot(loader.load());
        } catch (IOException e) {
            showError("Erreur de navigation: " + e.getMessage());
        }
    }

    @FXML
    void deconnecter(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Confirmer la déconnexion");
        alert.setContentText("Voulez-vous vraiment vous déconnecter ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                performLogout();
            }
        });
    }

    private void performLogout() {
        try {
            if (authService != null) authService.logout();
        } finally {
            redirectToLogin();
        }
    }

    private void redirectToLogin() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
                HelloApplication.getScene().setRoot(loader.load());
            } catch (IOException e) {
                showCriticalError("Erreur de chargement de la page de connexion.");
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showCriticalError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur critique");
        alert.setHeaderText("Erreur système");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshUserInfo() { loadUserInfo(); }

    public boolean isUserLoggedIn() {
        return authService != null && authService.isLoggedIn();
    }

    public CompletableFuture<UtilisateurDTO> getCurrentUserAsync() {
        if (authService != null && authService.isLoggedIn()) {
            return authService.getCurrentUser();
        } else {
            return CompletableFuture.failedFuture(new RuntimeException("Utilisateur non connecté"));
        }
    }

    public void cleanup() {
        // Nettoyage des ressources
    }
}
