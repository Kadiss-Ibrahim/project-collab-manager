package gui.javafrontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class ProfileController {

    @FXML
    private ImageView image2;

    @FXML
    private Button modifierButton;

    @FXML
    void modifierProfil(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("modifier-profile.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

}
