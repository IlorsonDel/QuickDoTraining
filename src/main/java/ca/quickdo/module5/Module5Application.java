package ca.quickdo.module5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Module5Application extends Application {
    private Module5Controller2 controller;

    @Override
    public void stop() throws Exception {
        if (controller != null)
            controller.shutdown();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        var loader = new FXMLLoader();
        loader.setLocation(Module5Application.class.getResource("module-5.fxml"));
        final var parent = (Parent) loader.load();
        controller = loader.getController();
        var scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Module 5");
        stage.show();
    }
}