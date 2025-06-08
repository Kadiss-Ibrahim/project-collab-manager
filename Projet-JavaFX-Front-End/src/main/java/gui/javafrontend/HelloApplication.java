package gui.javafrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static Scene scene;

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene newScene) {
        scene = newScene;
    }

    @Override
    public void start(Stage stage) throws IOException {
       stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Projet de Recherche");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}