package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class RechercheController {

    @FXML
    private Button backToLoginButton;

    @FXML
    private TableColumn<?, ?> colChercheur;

    @FXML
    private TableColumn<?, ?> colStatut;

    @FXML
    private TableColumn<?, ?> colTitre;

    @FXML
    private ImageView image2;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<?> tableProjets;

    @FXML
    void backToAccueil(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void recherche(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("recherche-view.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

}
