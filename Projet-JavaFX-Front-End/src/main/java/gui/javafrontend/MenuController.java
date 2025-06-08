package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class MenuController {

    @FXML
    private Button accueilButton;

    @FXML
    private Button calendrierButton;

    @FXML
    private Button deconnexionButton;

    @FXML
    private ImageView image1;

    @FXML
    private Label nomUser;

    @FXML
    private Button profileButton;

    @FXML
    private Button projetsButton;

    @FXML
    private Button tachesButton;

    @FXML
    void accueil(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void calendrier(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("calendrier.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void deconnexion(ActionEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        HelloApplication.getScene().setRoot(loader.load());

    }

    @FXML
    void profile(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("profile.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void projet(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("projets.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void taches(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("taches.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

}
