package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.Validation;

import java.io.IOException;
import java.util.regex.Pattern;

import static javafx.beans.binding.Bindings.isEmpty;
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
    private TextField password;

    @FXML
    private TextField phoneNumber;

    @FXML
    private Button registerButton;

    @FXML
    private Label registerFailed;


    @FXML
    void backToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
            HelloApplication.getScene().setRoot(loader.load());
        } catch (IOException e) {
            showError("Erreur lors du chargement de la page de login",registerFailed);
            e.printStackTrace();
        }
    }



    @FXML
    void register(ActionEvent event) {
        resetFieldStyles();
        registerFailed.setVisible(false);

        if (!validateAllFieldsFilled()) return;
        if (!Validation.validateEmail(email,registerFailed)) return;
        if (!validatePhoneNumber()) return;
        if (!Validation.validatePassword((PasswordField) password,registerFailed)) return;

        showSuccess("Inscription réussie !",registerFailed);
//        // TODO: Ajouter la logique d'enregistrement
    }



    private boolean validatePhoneNumber() {
        if (!phoneNumber.getText().matches("\\d{10}")) {
            highlightError(phoneNumber);
            showError("Le numéro doit contenir 10 chiffres",registerFailed);
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

        if (!isValid) showError("Tous les champs sont obligatoires",registerFailed);
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
