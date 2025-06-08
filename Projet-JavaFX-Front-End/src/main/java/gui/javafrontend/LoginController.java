package gui.javafrontend;


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

import static java.lang.Thread.sleep;
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

    @FXML
    void enterAsGuest(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil-guest.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
   void login(ActionEvent event) throws Exception {
        resetFieldStyles();

        if (!Validation.validateEmail(email,loginIncorrecte)) return;
        if (!Validation.validatePassword(password,loginIncorrecte)) return;

        showSuccess("Login réussie !",loginIncorrecte);
        System.out.println(email.getText());
        System.out.println(password.getText());

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil.fxml"));
        HelloApplication.getScene().setRoot(loader.load());

    }



//        if (user.isEmpty() || pass.isEmpty()) {
//            loginIncorrecte.setText("Veuillez remplir tous les champs");
//            loginIncorrecte.setVisible(true);
//            return;
//        }
//
//        // Ici, vous devriez appeler votre service d'authentification Spring
//        // boolean isAuthenticated = authService.authenticate(user, pass);
//        boolean isAuthenticated = true; // Temporaire pour le test
//
//        if (isAuthenticated) {
//            try {
//                // Charger la vue principale après authentification
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
//                loader.setControllerFactory(applicationContext::getBean);
//                Parent root = loader.load();
//
//                Stage stage = (Stage) loginButton.getScene().getWindow();
//                stage.setScene(new Scene(root));
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                loginIncorrecte.setText("Erreur de chargement de la vue principale");
//                loginIncorrecte.setVisible(true);
//            }
//        } else {
//            loginIncorrecte.setText("Nom d'utilisateur ou mot de passe incorrect");
//            loginIncorrecte.setVisible(true);
//        }
//    }

    @FXML
    void signUp(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("sign-up.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }
    private void resetFieldStyles() {
        email.setStyle("");
        password.setStyle("");
    }


}
