package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class AccueilGuestController {

    @FXML
    private Button chimieButton;

    @FXML
    private ImageView image2;

    @FXML
    private Button informatiqueButton;

    @FXML
    private Button projet4Button;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button systemeEmbarqueButton;

    @FXML
    void chimie(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("chimie-projet-guest.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void informatique(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("informatique-projet-guest.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void biologie(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("biologie-projet-guest.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void systemeEmbarque(ActionEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("s-e-projet-guest.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

}

