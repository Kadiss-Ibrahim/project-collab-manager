package gui.javafrontend;

import gui.javafrontend.dto.UtilisateurDTO;
import gui.javafrontend.service.AuthService;
import gui.javafrontend.service.UserService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private ImageView profileImage;
    @FXML private Label nomLabel;
    @FXML private Label prenomLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Button modifierButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private AnchorPane viewPane;
    @FXML private AnchorPane editPane;

    private final UserService userService = UserService.getInstance();
    private final AuthService authService = AuthService.getInstance();
    private UtilisateurDTO currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserData();
        switchToViewMode();
    }

    private void loadUserData() {
        showLoading(true);

        // Just use getCurrentUser() - no need for the second API call
        authService.getCurrentUser()
                .thenAccept(user -> {
                    currentUser = user;
                    updateUI(user);
                })
                .exceptionally(throwable -> {
                    System.err.println("Error loading user data: " + throwable.getMessage());
                    throwable.printStackTrace();
                    showError("Erreur de chargement", "Impossible de charger les données du profil: " + throwable.getMessage());
                    return null;
                })
                .thenRun(() -> showLoading(false));
    }

    private void updateUI(UtilisateurDTO user) {
        Platform.runLater(() -> {
            if (user != null) {
                nomLabel.setText(user.getNom() != null ? user.getNom() : "N/A");
                prenomLabel.setText(user.getPrenom() != null ? user.getPrenom() : "N/A");
                emailLabel.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                roleLabel.setText(user.getRole() != null ? user.getRole().toString() : "N/A");

                // Set fields for edit mode
                nomField.setText(user.getNom() != null ? user.getNom() : "");
                prenomField.setText(user.getPrenom() != null ? user.getPrenom() : "");
                emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            } else {
                // Handle null user case
                nomLabel.setText("N/A");
                prenomLabel.setText("N/A");
                emailLabel.setText("N/A");
                roleLabel.setText("N/A");

                nomField.setText("");
                prenomField.setText("");
                emailField.setText("");
            }
        });
    }

    @FXML
    private void modifierProfil(ActionEvent event) {
        switchToEditMode();
    }

    @FXML
    private void saveProfile(ActionEvent event) {
        if (currentUser == null) {
            showError("Erreur", "Aucun utilisateur connecté");
            return;
        }

        // Validate input
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            showError("Erreur de validation", "Tous les champs sont obligatoires");
            return;
        }

        showLoading(true);

        // Update user object with new values
        currentUser.setNom(nom);
        currentUser.setPrenom(prenom);
        currentUser.setEmail(email);

        userService.updateUser(currentUser.getId(), currentUser)
                .thenAccept(updatedUser -> {
                    currentUser = updatedUser;
                    // Update the AuthService's current user as well
                    authService.setCurrentUser(updatedUser);
                    updateUI(updatedUser);
                    switchToViewMode();
                    showSuccess("Succès", "Profil mis à jour avec succès");
                })
                .exceptionally(throwable -> {
                    System.err.println("Error updating user: " + throwable.getMessage());
                    throwable.printStackTrace();
                    showError("Erreur de mise à jour", "Impossible de mettre à jour le profil: " + throwable.getMessage());
                    return null;
                })
                .thenRun(() -> showLoading(false));
    }

    @FXML
    private void cancelEdit(ActionEvent event) {
        // Reset fields to original values
        if (currentUser != null) {
            updateUI(currentUser);
        }
        switchToViewMode();
    }

    private void switchToViewMode() {
        viewPane.setVisible(true);
        editPane.setVisible(false);
        modifierButton.setVisible(true);
    }

    private void switchToEditMode() {
        viewPane.setVisible(false);
        editPane.setVisible(true);
        modifierButton.setVisible(false);
    }

    private void showLoading(boolean show) {
        Platform.runLater(() -> {
            loadingIndicator.setVisible(show);
            modifierButton.setDisable(show);
            saveButton.setDisable(show);
            cancelButton.setDisable(show);
        });
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}