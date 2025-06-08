package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

import java.io.IOException;

public class MenuGuestController {

    @FXML
    private Button accueilButton;

    @FXML
    private Button deconnexionButton;

    @FXML
    void accueil(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil-guest.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void deconnexion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

}
