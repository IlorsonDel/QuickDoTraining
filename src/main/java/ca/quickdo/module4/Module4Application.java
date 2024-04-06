package ca.quickdo.module4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Module4Application extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        var scene = new Scene(FXMLLoader.load(Module4Application.class.getResource("module-4.fxml")));
        stage.setScene(scene);
        stage.setTitle("Module 4 - Collection, maps, more...");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
