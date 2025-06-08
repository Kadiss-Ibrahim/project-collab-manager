package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class AccueilController {

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
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("chimie-projet.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void informatique(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("informatique-projet.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void biologie(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("biologie-projet.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void recherche(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("recherche-view.fxml"));
            HelloApplication.getScene().setRoot(loader.load());
            System.out.println(searchField.getText());

    }

    @FXML
    void systemeEmbarque(ActionEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("s-e-projet.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

}

