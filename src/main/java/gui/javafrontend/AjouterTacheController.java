package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.util.HashMap;

import static utils.Validation.showSuccess;

public class AjouterTacheController {

    @FXML
    private Label ajouterSucces;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private TextArea descriptionField;

    @FXML
    private Spinner<?> difficulteSpinner;

    @FXML
    private ComboBox<?> etatComboBox;

    @FXML
    private ImageView image2;

    @FXML
    private Spinner<?> notationSpinner;

    @FXML
    private Spinner<?> prioriteSpinner;

    @FXML
    private TextField titreField;

    @FXML
    void ajouterTache(ActionEvent event) {
        showSuccess("Tache bien Ajouter ",ajouterSucces);
    }

}

