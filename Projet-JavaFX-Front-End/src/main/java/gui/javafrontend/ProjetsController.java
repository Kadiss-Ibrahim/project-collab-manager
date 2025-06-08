package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class ProjetsController {

    @FXML
    private Button ajouterProjetButton;

    @FXML
    private TableColumn<?, ?> colChercheur;

    @FXML
    private TableColumn<?, ?> colStatut;

    @FXML
    private TableColumn<?, ?> colTitre;

    @FXML
    private ImageView image2;

    @FXML
    private TableView<?> tableProjets;

    @FXML
    void ajouterProjet(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("ajouter-projet.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    public void initialize() {
        // Tu dois définir les valeurs de colonnes ici avec PropertyValueFactory

        /**
         * // Double-clic code bach tmchi mn titre de projet l l'interface de chat
         */
//        tableProjets.setOnMouseClicked(event -> {
//            if (event.getClickCount() == 2) {
//                Projet projetSelectionne = tableProjets.getSelectionModel().getSelectedItem();
//                if (projetSelectionne != null) {
//                    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("chat.fxml"));
//                    try {
//                        HelloApplication.getScene().setRoot(loader.load());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
    }

}


