package gui.javafrontend;

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

public class SignUpController {

    @FXML
    private Button backToLoginButton;

    @FXML
    private TextField email;

    @FXML
    private TextField firstName;

    @FXML
    private ImageView image1;

    @FXML
    private ImageView image2;

    @FXML
    private TextField lastName;

    @FXML
    private PasswordField password;

    @FXML
    private Button registerButton;

    @FXML
    private Label registerFailed;

    @FXML
    private TextField identifiant;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    void backToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
            HelloApplication.getScene().setRoot(loader.load());
        } catch (IOException e) {
            showError("Erreur lors du chargement de la page de login", registerFailed);
            e.printStackTrace();
        }
    }

    @FXML
    void register(ActionEvent event) {
        resetFieldStyles();
        registerFailed.setVisible(false);

        registerButton.setDisable(true);
        registerButton.setText("Inscription en cours...");

        if (!validateAllFieldsFilled()) {
            enableRegisterButton();
            return;
        }
        if (!Validation.validateEmail(email, registerFailed)) {
            enableRegisterButton();
            return;
        }

        if (!Validation.validatePassword((PasswordField) password, registerFailed)) {
            enableRegisterButton();
            return;
        }

        if (!validateIdentifiant()) {
            enableRegisterButton();
            return;
        }

        UtilisateurDTO utilisateur = new UtilisateurDTO();
        utilisateur.setIdentifiant(identifiant.getText().trim());
        utilisateur.setNom(lastName.getText().trim());
        utilisateur.setPrenom(firstName.getText().trim());
        utilisateur.setEmail(email.getText().trim());

        System.out.println("=== DONNÉES ENVOYÉES ===");
        System.out.println("Identifiant: " + utilisateur.getIdentifiant());
        System.out.println("Nom: " + utilisateur.getNom());
        System.out.println("Prénom: " + utilisateur.getPrenom());
        System.out.println("Email: " + utilisateur.getEmail());
        System.out.println("Password length: " + password.getText().length());

        authService.register(utilisateur, password.getText())
                .thenAccept(successMessage -> {
                    Platform.runLater(() -> {
                        System.out.println("=== INSCRIPTION RÉUSSIE ===");
                        System.out.println("Message du serveur: " + successMessage);

                        showSuccess("Inscription réussie ! " + successMessage, registerFailed);


                        new Thread(() -> {
                            try {
                                Thread.sleep(2000); // Attendre 2 secondes pour que l'utilisateur voit le message
                                Platform.runLater(() -> {
                                    try {

                                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
                                        HelloApplication.getScene().setRoot(loader.load());
                                    } catch (IOException e) {
                                        showError("Erreur lors de la redirection vers la page de login", registerFailed);
                                        e.printStackTrace();
                                    }
                                });
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        enableRegisterButton();
                        String errorMessage = throwable.getMessage();

                        // Logs détaillés pour debug
                        System.err.println("=== ERREUR INSCRIPTION ===");
                        System.err.println("Message: " + errorMessage);
                        throwable.printStackTrace();

                        // Adapter le message d'erreur selon le type d'erreur
                        if (errorMessage.contains("409") || errorMessage.toLowerCase().contains("existe")) {
                            showError("Cet identifiant ou cette adresse email est déjà utilisé", registerFailed);
                        } else if (errorMessage.contains("400")) {
                            showError("Données invalides. Vérifiez vos informations", registerFailed);
                        } else if (errorMessage.contains("500")) {
                            showError("Erreur serveur. Réessayez plus tard", registerFailed);
                        } else if (errorMessage.toLowerCase().contains("connection") || errorMessage.toLowerCase().contains("timeout")) {
                            showError("Impossible de contacter le serveur", registerFailed);
                        } else {
                            showError("Erreur lors de l'inscription: " +
                                    (errorMessage.length() > 50 ?
                                            errorMessage.substring(0, 50) + "..." :
                                            errorMessage), registerFailed);
                        }
                    });
                    return null;
                });
    }

    private void enableRegisterButton() {
        registerButton.setDisable(false);
        registerButton.setText("Enregistrer");
    }

    private void redirectToMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
            HelloApplication.getScene().setRoot(loader.load());
        } catch (IOException e) {
            showError("Erreur lors de la redirection", registerFailed);
            e.printStackTrace();
        }
    }


    private boolean validateIdentifiant() {
        String identifiantText = identifiant.getText().trim();

        if (identifiantText.isEmpty()) {
            highlightError(identifiant);
            showError("L'identifiant est obligatoire", registerFailed);
            return false;
        }


        if (identifiantText.length() < 3) {
            highlightError(identifiant);
            showError("L'identifiant doit contenir au moins 3 caractères", registerFailed);
            return false;
        }

        if (identifiantText.length() > 20) {
            highlightError(identifiant);
            showError("L'identifiant ne peut pas dépasser 20 caractères", registerFailed);
            return false;
        }


        if (!identifiantText.matches("^[a-zA-Z0-9_]+$")) {
            highlightError(identifiant);
            showError("L'identifiant ne peut contenir que des lettres, chiffres et underscores", registerFailed);
            return false;
        }

        return true;
    }

    private boolean validateAllFieldsFilled() {
        boolean isValid = true;

        if (Validation.isEmpty(identifiant)) {
            highlightError(identifiant);
            isValid = false;
        }
        if (Validation.isEmpty(firstName)) {
            highlightError(firstName);
            isValid = false;
        }
        if (Validation.isEmpty(lastName)) {
            highlightError(lastName);
            isValid = false;
        }
        if (Validation.isEmpty(email)) {
            highlightError(email);
            isValid = false;
        }
        if (Validation.isEmpty(password)) {
            highlightError(password);
            isValid = false;
        }

        if (!isValid) {
            showError("Tous les champs sont obligatoires", registerFailed);
        }
        return isValid;
    }

    private void resetFieldStyles() {
        identifiant.setStyle("");
        firstName.setStyle("");
        lastName.setStyle("");
        email.setStyle("");
        password.setStyle("");
    }

    @FXML
    private void initialize() {

        registerFailed.setVisible(false);


        identifiant.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                identifiant.setStyle("");
            }
        });

        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                email.setStyle("");
            }
        });

        password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                password.setStyle("");
            }
        });

        firstName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                firstName.setStyle("");
            }
        });

        lastName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                lastName.setStyle("");
            }
        });
    }
}