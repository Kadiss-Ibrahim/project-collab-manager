package gui.javafrontend;

import gui.javafrontend.dto.ProjetDTO;
import gui.javafrontend.service.ProjetService;
import gui.javafrontend.service.AuthService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static utils.Validation.showError;
import static utils.Validation.showSuccess;

/**
 * Controller for adding new projects with improved validation, error handling, and user experience
 */
public class AjouterProjetController implements Initializable {

    // FXML Components
    @FXML private Button ajouterProjetButton;
    @FXML private Button viderButton;
    @FXML private Label ajouterProjetSucces;
    @FXML private TextArea descriptionField;
    @FXML private CheckBox estPublicCheck;
    @FXML private ImageView image2;
    @FXML private TextField nomCourtField;
    @FXML private TextField nomLongField;
    @FXML private TextField themeField;
    @FXML private TextField typeField;
    @FXML private TextField licenseField;
    @FXML private ComboBox<String> groupeCombo;
    @FXML private VBox mainContainer;
    @FXML private ProgressIndicator progressIndicator;

    // Services
    private ProjetService projetService;
    private AuthService authService;

    // Constants
    private static final int MAX_SHORT_NAME_LENGTH = 50;
    private static final int MAX_LONG_NAME_LENGTH = 200;
    private static final String DEFAULT_LICENSE = "MIT";
    private static final Pattern VALID_SHORT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s_-]*$");

    // Group mapping for better maintainability
    private static final Map<String, Long> GROUP_ID_MAP = new HashMap<>();
    static {
        GROUP_ID_MAP.put("Informatique", 1L);
        GROUP_ID_MAP.put("Systeme-Embarquee", 2L);
        GROUP_ID_MAP.put("Chimie", 3L);
        GROUP_ID_MAP.put("Biologie", 4L);
    }

    // Styles
    private static final String DEFAULT_FIELD_STYLE = "-fx-border-color: #bdc3c7; -fx-border-width: 1px; -fx-border-radius: 3px;";
    private static final String VALID_FIELD_STYLE = "-fx-border-color: #27ae60; -fx-border-width: 1px; -fx-border-radius: 3px;";
    private static final String INVALID_FIELD_STYLE = "-fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 3px;";
    private static final String COMBO_STYLE = "-fx-font-size: 14px; -fx-padding: 8px; -fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5px;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        initializeComponents();
        setupValidation();
        setupAnimations();
    }

    private void initializeServices() {
        projetService = ProjetService.getInstance();
        authService = AuthService.getInstance();
    }

    private void initializeComponents() {
        setupComboBoxes();
        setupEventHandlers();
        setupProgressIndicator();
        setupTooltips();
    }

    private void setupComboBoxes() {
        groupeCombo.setItems(FXCollections.observableArrayList(GROUP_ID_MAP.keySet()));
        groupeCombo.setValue("Informatique"); // Default selection
        groupeCombo.setStyle(COMBO_STYLE);
    }

    private void setupEventHandlers() {
        ajouterProjetButton.setOnAction(this::ajouterProjet);

        if (viderButton != null) {
            viderButton.setOnAction(e -> viderFormulaire());
        }

        setupRealTimeValidation();
    }

    private void setupRealTimeValidation() {
        // Short name validation with length and character restrictions
        nomCourtField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > MAX_SHORT_NAME_LENGTH) {
                nomCourtField.setText(oldText);
                showTemporaryMessage("Le nom court ne peut pas dépasser " + MAX_SHORT_NAME_LENGTH + " caractères", "warning");
                return;
            }

            if (!VALID_SHORT_NAME_PATTERN.matcher(newText).matches() && !newText.isEmpty()) {
                nomCourtField.setText(oldText);
                showTemporaryMessage("Le nom court ne peut contenir que des lettres, chiffres, espaces, tirets et underscores", "warning");
                return;
            }

            updateFieldStyle(nomCourtField, !newText.trim().isEmpty());
        });

        // Long name validation with length restriction and multiple spaces cleanup
        nomLongField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > MAX_LONG_NAME_LENGTH) {
                nomLongField.setText(oldText);
                showTemporaryMessage("Le nom long ne peut pas dépasser " + MAX_LONG_NAME_LENGTH + " caractères", "warning");
                return;
            }

            // Clean up multiple spaces
            String cleanedText = newText.replaceAll(" +", " ");
            if (!cleanedText.equals(newText)) {
                Platform.runLater(() -> nomLongField.setText(cleanedText));
                return;
            }

            updateFieldStyle(nomLongField, !newText.trim().isEmpty());
        });

        // Other field validations
        descriptionField.textProperty().addListener((obs, oldText, newText) ->
                updateFieldStyle(descriptionField, !newText.trim().isEmpty()));

        themeField.textProperty().addListener((obs, oldText, newText) ->
                updateFieldStyle(themeField, !newText.trim().isEmpty()));

        typeField.textProperty().addListener((obs, oldText, newText) ->
                updateFieldStyle(typeField, !newText.trim().isEmpty()));

        if (licenseField != null) {
            licenseField.textProperty().addListener((obs, oldText, newText) ->
                    updateFieldStyle(licenseField, !newText.trim().isEmpty()));
        }
    }

    private void setupTooltips() {
        nomCourtField.setTooltip(new Tooltip("Nom court du projet (max " + MAX_SHORT_NAME_LENGTH + " caractères)"));
        nomLongField.setTooltip(new Tooltip("Nom complet du projet (max " + MAX_LONG_NAME_LENGTH + " caractères)"));
        descriptionField.setTooltip(new Tooltip("Description détaillée du projet"));
        themeField.setTooltip(new Tooltip("Domaine ou thématique du projet"));
        typeField.setTooltip(new Tooltip("Type de projet (IA, Web, Mobile, etc.)"));

        if (licenseField != null) {
            licenseField.setTooltip(new Tooltip("License du projet (MIT, GPL, Apache, etc.)"));
        }

        groupeCombo.setTooltip(new Tooltip("Groupe auquel appartient le projet"));
        estPublicCheck.setTooltip(new Tooltip("Cochez si le projet doit être visible publiquement"));
    }

    private void setupProgressIndicator() {
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
            progressIndicator.setStyle("-fx-accent: #3498db;");
        }
    }

    private void setupAnimations() {
        if (mainContainer != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), mainContainer);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    @FXML
    void ajouterProjet(ActionEvent event) {
        clearMessages();

        if (!validerChamps()) {
            return;
        }

        // Check authentication before proceeding
        if (!authService.isLoggedIn()) {
            showError("Session expirée. Veuillez vous reconnecter.", ajouterProjetSucces);
            return;
        }

        setLoadingState(true);

        try {
            ProjetDTO nouveauProjet = creerProjetDTO();
            logProjetData(nouveauProjet);

            projetService.ajouterProjetAvecRefresh(nouveauProjet)
                    .whenComplete(this::handleProjetCreationResult);

        } catch (Exception e) {
            setLoadingState(false);
            handleError(e);
        }
    }

    private void handleProjetCreationResult(ProjetDTO projetCree, Throwable throwable) {
        Platform.runLater(() -> {
            setLoadingState(false);
            if (throwable != null) {
                handleError(throwable);
            } else {
                handleSuccess(projetCree);
            }
        });
    }

    private void handleSuccess(ProjetDTO projetCree) {
        String message = "✓ Projet '" + projetCree.getNomLong() + "' créé avec succès!";
        showSuccess(message, ajouterProjetSucces);
        viderFormulaire();
        animateSuccessMessage();

        System.out.println("✓ " + message);
    }

    private void handleError(Throwable throwable) {
        String message = extractErrorMessage(throwable);
        showError("✗ " + message, ajouterProjetSucces);
        animateErrorMessage();

        System.err.println("✗ Erreur création projet: " + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    private String extractErrorMessage(Throwable throwable) {
        if (throwable == null) {
            return "Erreur inconnue lors de l'ajout du projet";
        }

        String message = throwable.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Erreur lors de l'ajout du projet";
        }

        return message;
    }

    private void setLoadingState(boolean loading) {
        ajouterProjetButton.setDisable(loading);
        ajouterProjetButton.setText(loading ? "Ajout en cours..." : "Ajouter le Projet");

        if (viderButton != null) {
            viderButton.setDisable(loading);
        }

        if (progressIndicator != null) {
            progressIndicator.setVisible(loading);
        }

        setFieldsDisabled(loading);
    }

    private void setFieldsDisabled(boolean disabled) {
        nomCourtField.setDisable(disabled);
        nomLongField.setDisable(disabled);
        descriptionField.setDisable(disabled);
        themeField.setDisable(disabled);
        typeField.setDisable(disabled);

        if (licenseField != null) {
            licenseField.setDisable(disabled);
        }

        groupeCombo.setDisable(disabled);
        estPublicCheck.setDisable(disabled);
    }

    private void clearMessages() {
        ajouterProjetSucces.setText("");
        ajouterProjetSucces.setStyle("");
    }

    private void showTemporaryMessage(String message, String type) {
        ajouterProjetSucces.setText(message);
        String style = "warning".equals(type) ?
                "-fx-text-fill: #f39c12; -fx-font-size: 12px;" :
                "-fx-text-fill: #e74c3c; -fx-font-size: 12px;";
        ajouterProjetSucces.setStyle(style);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> clearMessages()));
        timeline.play();
    }

    private void updateFieldStyle(Control field, boolean isValid) {
        field.setStyle(isValid ? VALID_FIELD_STYLE : DEFAULT_FIELD_STYLE);
    }

    private void animateSuccessMessage() {
        if (ajouterProjetSucces != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(300), ajouterProjetSucces);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    private void animateErrorMessage() {
        if (ajouterProjetSucces != null) {
            TranslateTransition shake = new TranslateTransition(Duration.millis(50), ajouterProjetSucces);
            shake.setFromX(0);
            shake.setToX(10);
            shake.setCycleCount(6);
            shake.setAutoReverse(true);
            shake.play();
        }
    }

    private boolean validerChamps() {
        StringBuilder erreurs = new StringBuilder();
        boolean isValid = true;

        isValid &= validateRequiredField(nomCourtField, "Le nom court", erreurs);
        isValid &= validateRequiredField(nomLongField, "Le nom long", erreurs);
        isValid &= validateRequiredField(descriptionField, "La description", erreurs);
        isValid &= validateRequiredField(themeField, "Le thème", erreurs);
        isValid &= validateRequiredField(typeField, "Le type", erreurs);

        // Critical group validation
        if (groupeCombo.getValue() == null || groupeCombo.getValue().trim().isEmpty()) {
            erreurs.append("- Le groupe est obligatoire\n");
            groupeCombo.setStyle(INVALID_FIELD_STYLE);
            isValid = false;
        } else {
            groupeCombo.setStyle(COMBO_STYLE);
        }

        if (!isValid) {
            showError("Veuillez corriger les erreurs suivantes:\n" + erreurs.toString(), ajouterProjetSucces);
            animateErrorMessage();
        }

        return isValid;
    }

    private boolean validateRequiredField(TextInputControl field, String fieldName, StringBuilder erreurs) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            erreurs.append("- ").append(fieldName).append(" est obligatoire\n");
            field.setStyle(INVALID_FIELD_STYLE);
            return false;
        } else {
            field.setStyle(VALID_FIELD_STYLE);
            return true;
        }
    }

    private ProjetDTO creerProjetDTO() {
        ProjetDTO projet = new ProjetDTO();

        // Required fields
        projet.setNomCourt(nomCourtField.getText().trim());
        projet.setNomLong(nomLongField.getText().trim());
        projet.setDescription(descriptionField.getText().trim());
        projet.setTheme(themeField.getText().trim());
        projet.setType(typeField.getText().trim());
        projet.setEstPublic(estPublicCheck.isSelected());

        // License handling
        String license = (licenseField != null && !licenseField.getText().trim().isEmpty())
                ? licenseField.getText().trim()
                : DEFAULT_LICENSE;
        projet.setLicense(license);

        // Group ID conversion
        String groupeSelectionne = groupeCombo.getValue();
        if (groupeSelectionne == null) {
            throw new IllegalStateException("Aucun groupe sélectionné");
        }

        Long groupeId = GROUP_ID_MAP.get(groupeSelectionne);
        if (groupeId == null) {
            throw new IllegalStateException("ID du groupe invalide pour: " + groupeSelectionne);
        }

        projet.setGroupeId(groupeId);
        return projet;
    }

    private void logProjetData(ProjetDTO projet) {
        System.out.println("=== DONNÉES DU PROJET ===");
        System.out.println("Nom court: " + projet.getNomCourt());
        System.out.println("Nom long: " + projet.getNomLong());
        System.out.println("Description: " + projet.getDescription());
        System.out.println("Thème: " + projet.getTheme());
        System.out.println("Type: " + projet.getType());
        System.out.println("License: " + projet.getLicense());
        System.out.println("GroupeId: " + projet.getGroupeId());
        System.out.println("Est Public: " + projet.isEstPublic());
        System.out.println("========================");
    }

    private void viderFormulaire() {
        Platform.runLater(() -> {
            try {
                // Clear text fields
                nomCourtField.clear();
                nomLongField.clear();
                descriptionField.clear();
                themeField.clear();
                typeField.clear();

                if (licenseField != null) {
                    licenseField.clear();
                }

                // Reset combo box to default
                groupeCombo.setValue("Informatique");

                // Reset checkbox
                estPublicCheck.setSelected(false);

                // Reset field styles
                resetFieldStyles();

                // Clear messages
                clearMessages();

                // Focus on first field
                nomCourtField.requestFocus();

                System.out.println("✓ Formulaire vidé avec succès");

            } catch (Exception e) {
                System.err.println("✗ Erreur lors du vidage du formulaire: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void resetFieldStyles() {
        nomCourtField.setStyle(DEFAULT_FIELD_STYLE);
        nomLongField.setStyle(DEFAULT_FIELD_STYLE);
        descriptionField.setStyle(DEFAULT_FIELD_STYLE);
        themeField.setStyle(DEFAULT_FIELD_STYLE);
        typeField.setStyle(DEFAULT_FIELD_STYLE);

        if (licenseField != null) {
            licenseField.setStyle(DEFAULT_FIELD_STYLE);
        }

        groupeCombo.setStyle(COMBO_STYLE);
    }

    public void rafraichir() {
        Platform.runLater(() -> {
            try {
                setupComboBoxes();

                if (!authService.isLoggedIn()) {
                    showError("Session expirée. Veuillez vous reconnecter.", ajouterProjetSucces);
                    setFieldsDisabled(true);
                    return;
                }

                System.out.println("✓ Interface rafraîchie");

            } catch (Exception e) {
                System.err.println("✗ Erreur lors du rafraîchissement: " + e.getMessage());
                showError("Erreur lors du rafraîchissement de l'interface", ajouterProjetSucces);
            }
        });
    }

    // Additional validation setup method
    private void setupValidation() {
        // This method can be extended for more complex validation rules
        System.out.println("✓ Validation configurée");
    }
}