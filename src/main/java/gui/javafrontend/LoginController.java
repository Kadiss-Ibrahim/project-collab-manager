package gui.javafrontend;

import gui.javafrontend.dto.AuthRequestDTO;
import gui.javafrontend.dto.AuthResponseDTO;
import gui.javafrontend.dto.UtilisateurDTO;
import gui.javafrontend.service.AuthService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import utils.Validation;

import java.io.IOException;

import static utils.Validation.*;

public class LoginController {

    @FXML
    private Button enterAsGuestButton;

    @FXML
    private ImageView image1;

    @FXML
    private ImageView image2;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginIncorrecte;

    @FXML
    private PasswordField password;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField email;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    void enterAsGuest(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil-guest.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void login(ActionEvent event) {
        // Reset styles et messages d'erreur
        resetFieldStyles();
        loginIncorrecte.setText("");
        loginIncorrecte.setVisible(false);

        // Désactiver le bouton pendant la connexion
        loginButton.setDisable(true);
        loginButton.setText("Connexion...");

        // Validation des champs
        if (!Validation.validateEmail(email, loginIncorrecte)) {
            resetLoginButton();
            return;
        }
        if (!Validation.validatePassword(password, loginIncorrecte)) {
            resetLoginButton();
            return;
        }

        // Créer la requête d'authentification
        AuthRequestDTO authRequest = new AuthRequestDTO(
                email.getText().trim(),
                password.getText()
        );

        // Appel API asynchrone
        authService.login(authRequest)
                .thenAccept(this::handleLoginSuccess)
                .exceptionally(this::handleLoginError);
    }

    private void handleLoginSuccess(AuthResponseDTO authResponse) {
        Platform.runLater(() -> {
            try {
                // Sauvegarder l'utilisateur connecté
                authService.setCurrentUser(authResponse.getUtilisateur());

                // Afficher message de succès
                showSuccess("Connexion réussie !", loginIncorrecte);

                // Debug
                System.out.println("Utilisateur connecté: " + authResponse.getUtilisateur().getEmail());
                System.out.println("Token: " + authResponse.getAccessToken());

                // Attendre un peu pour que l'utilisateur voie le message
                Thread.sleep(1000);

                // Rediriger vers l'accueil
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil.fxml"));
                HelloApplication.getScene().setRoot(loader.load());

            } catch (Exception e) {
                handleLoginError(e);
            }
        });
    }

    private Void handleLoginError(Throwable throwable) {
        Platform.runLater(() -> {
            resetLoginButton();

            String errorMessage = "Erreur de connexion";

            // Gérer les différents types d'erreurs
            if (throwable.getMessage().contains("401")) {
                errorMessage = "Email ou mot de passe incorrect";
            } else if (throwable.getMessage().contains("404")) {
                errorMessage = "Service non disponible";
            } else if (throwable.getMessage().contains("timeout")) {
                errorMessage = "Timeout - Vérifiez votre connexion";
            } else if (throwable.getMessage().contains("Connection refused")) {
                errorMessage = "Impossible de se connecter au serveur";
            }

            loginIncorrecte.setText(errorMessage);
            loginIncorrecte.setVisible(true);

            // Marquer les champs en rouge
            email.setStyle("-fx-border-color: red;");
            password.setStyle("-fx-border-color: red;");

            System.err.println("Erreur de connexion: " + throwable.getMessage());
        });
        return null;
    }

    @FXML
    void signUp(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("sign-up.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    private void resetFieldStyles() {
        email.setStyle("");
        password.setStyle("");
    }

    private void resetLoginButton() {
        loginButton.setDisable(false);
        loginButton.setText("se connecter");
    }

    // Méthode pour vérifier si un utilisateur est déjà connecté
    @FXML
    public void initialize() {
        // Vérifier si un token valide existe déjà
        authService.getCurrentUser()
                .thenAccept(user -> {
                    if (user != null) {
                        Platform.runLater(() -> {
                            try {
                                // Rediriger directement vers l'accueil
                                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil.fxml"));
                                HelloApplication.getScene().setRoot(loader.load());
                            } catch (IOException e) {
                                System.err.println("Erreur lors de la redirection automatique: " + e.getMessage());
                            }
                        });
                    }
                })
                .exceptionally(throwable -> {
                    // Pas d'utilisateur connecté, rester sur la page de login
                    return null;
                });
    }
}
