package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class TachesController {

    @FXML
    private Button ajouterTacheButton;

    @FXML
    private TableColumn<?, ?> colChercheur;

    @FXML
    private TableColumn<?, ?> colStatut;

    @FXML
    private TableColumn<?, ?> colTitre;

    @FXML
    private ImageView image2;

    @FXML
    private TableView<?> tableTaches;

    @FXML
    void ajouterTache(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("ajouter-tache.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

}
