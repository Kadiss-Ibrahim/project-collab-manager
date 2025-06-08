package utils;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.util.regex.Pattern;

public class Validation {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static void showError(String message, Label label) {
        label.setTextFill(Color.RED);
        label.setText(message);
        label.setVisible(true);
    }
    public static void showSuccess(String message,Label label) {
        label.setTextFill(Color.GREEN);
        label.setText(message);
        label.setVisible(true);
    }
    public static boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    public static void highlightError(TextField field) {
        field.setStyle("-fx-border-color: red;");
    }


    public static boolean validateEmail(TextField email, Label label) {
        if (!Validation.EMAIL_PATTERN.matcher(email.getText()).matches()) {
            highlightError(email);
            showError("Format d'email invalide",label);
            return false;
        }
        return true;
    }
    public static boolean validatePassword(PasswordField password, Label label) {
        if (password.getText().length() < 6) {
            highlightError(password);
            showError("Le mot de passe doit avoir au moins 8 caractères",label);
            return false;
        }
        return true;
    }
}
