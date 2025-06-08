package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import static utils.Validation.showSuccess;

public class AjouterProjetController {

    @FXML
    private Button ajouterProjetButton;

    @FXML
    private Label ajouterProjetSucces;

    @FXML
    private DatePicker dateAcceptationPicker;

    @FXML
    private DatePicker dateCloturePicker;

    @FXML
    private DatePicker dateCreationPicker;

    @FXML
    private TextArea descriptionField;

    @FXML
    private CheckBox estPublicCheck;

    @FXML
    private ComboBox<?> groupeCombo;

    @FXML
    private ImageView image2;

    @FXML
    private TextField nomCourtField;

    @FXML
    private TextField nomLongField;

    @FXML
    private ComboBox<?> statutProjetCombo;

    @FXML
    private TextField themeField;

    @FXML
    private TextField typeField;

    @FXML
    void ajouterProjet(ActionEvent event) {
        showSuccess("Projet bien Ajouter ",ajouterProjetSucces);
    }

}
