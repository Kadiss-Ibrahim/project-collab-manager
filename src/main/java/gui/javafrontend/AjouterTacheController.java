package gui.javafrontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

import static utils.Validation.showSuccess;

public class AjouterTacheController implements Initializable {

    @FXML
    private Label ajouterSucces;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextArea descriptionField;

    @FXML
    private Spinner<String> difficulteSpinner;

    @FXML
    private ComboBox<?> etatComboBox;

    @FXML
    private ImageView image2;
    
    @FXML
    private Spinner<Integer> prioriteSpinner;

    @FXML
    private TextField titreField;

    @FXML
    void ajouterTache(ActionEvent event) {
        showSuccess("Tache bien Ajouter ",ajouterSucces);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
        prioriteSpinner.setValueFactory(valueFactory);
        ObservableList<String> niveaux = FXCollections.observableArrayList("Facile", "Moyenne", "Difficile"
        );

        SpinnerValueFactory<String> valFactory =
                new SpinnerValueFactory.ListSpinnerValueFactory<>(niveaux);
        valFactory.setValue("Facile");

        difficulteSpinner.setValueFactory(valFactory);
    }

}

