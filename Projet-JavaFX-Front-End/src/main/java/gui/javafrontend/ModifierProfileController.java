package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import utils.Validation;

import static utils.Validation.*;

public class ModifierProfileController {

    @FXML
    private Button EnregistrerButton;

    @FXML
    private TextField email;

    @FXML
    private TextField firstName;

    @FXML
    private ImageView image2;

    @FXML
    private TextField lastName;

    @FXML
    private TextField password;

    @FXML
    private TextField phoneNumber;

    @FXML
    private Label modificationFailed;

    @FXML
    void Enregistrer(ActionEvent event) {
        resetFieldStyles();
        modificationFailed.setVisible(false);

        if (!validateAllFieldsFilled()) return;
        if (!Validation.validateEmail(email, modificationFailed)) return;
        if (!validatePhoneNumber()) return;
        if (!Validation.validatePassword((PasswordField) password, modificationFailed)) return;

        showSuccess("Mofication réussie !", modificationFailed);
//        // TODO: Ajouter la logique d'enregistrement
    }

    private boolean validatePhoneNumber() {
        if (!phoneNumber.getText().matches("\\d{10}")) {
            highlightError(phoneNumber);
            showError("Le numéro doit contenir 10 chiffres", modificationFailed);
            return false;
        }
        return true;
    }
    private boolean validateAllFieldsFilled() {
        boolean isValid = true;

        if (Validation.isEmpty(firstName)) { highlightError(firstName); isValid = false; }
        if (Validation.isEmpty(lastName)) { highlightError(lastName); isValid = false; }
        if (Validation.isEmpty(email)) { highlightError(email); isValid = false; }
        if (Validation.isEmpty(phoneNumber)) { highlightError(phoneNumber); isValid = false; }
        if (Validation.isEmpty(password)) { highlightError(password); isValid = false; }

        if (!isValid) showError("Tous les champs sont obligatoires", modificationFailed);
        return isValid;
    }
    private void resetFieldStyles() {
        firstName.setStyle("");
        lastName.setStyle("");
        email.setStyle("");
        phoneNumber.setStyle("");
        password.setStyle("");
    }

}
