package ca.quickdo.module2;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private Label lblSelectedFile;

    @FXML
    private Label lblFileDescription;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private StackPane root;

    @FXML
    private void initialize() {

    }

    @FXML
    private void onBtnPickFileClicked() {
        /*
         * Handling Null case !
         * */

        var fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        var chosenFile = fileChooser.showOpenDialog(null);
        if (chosenFile == null) {
            lblSelectedFile.setText("");
            // Display an alert with the error message
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText("No file was chosen");
            alert.showAndWait();
            return;
        }
        lblSelectedFile.setText(chosenFile.getName());
        /*
         * Setting or writing into the file (Giving the name and size of the file selected)
         * */
        var stringBuilder = new StringBuilder();
        try (var stream = new BufferedInputStream(new FileInputStream(chosenFile))) {
            final var fileSize = stream.readAllBytes().length;
            stringBuilder.append("File Name; ").append(chosenFile.getName()).append("\n");
            stringBuilder.append("File Path; ").append(chosenFile.getAbsolutePath()).append("\n");
            stringBuilder.append("File Size; ").append(fileSize).append("bytes").append("\n");
        } catch (IOException ex) {
            lblSelectedFile.setText("");
            lblFileDescription.setText("");
            return;
        }
        lblFileDescription.setText(stringBuilder.toString());
    }

    @FXML
    private void onBtnPickFileClicked2() throws IOException {
        /*
         * Handling Null case !
         * */

        var fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        var chosenFile = fileChooser.showOpenDialog(null);
        if (chosenFile == null) {
            lblSelectedFile.setText("");
            return;
        }
        lblSelectedFile.setText(chosenFile.getName());
//        javafx.scene.control.Alert
        /*
         * Setting or writing into the file (Giving the name and size of the file selected)
         * */
        var stringBuilder = new StringBuilder();
        final var outputPath = Paths.get(System.getProperty("user.home"),"Desktop");

        try (var stream = new FileOutputStream(outputPath.toString())) {
            final var fileSize = Files.readAllBytes(Paths.get(chosenFile.getPath())).length;
            stringBuilder.append("File Name: ").append(chosenFile.getName()).append("\n");
            stringBuilder.append("File Path: ").append(chosenFile.getAbsolutePath()).append("\n");
            stringBuilder.append("File Size: ").append(fileSize).append(" bytes").append("\n");

            stream.write(stringBuilder.toString().getBytes());
            stream.flush();
            } /*catch (IOException ex) {
                System.err.println(ex.getMessage());
                lblSelectedFile.setText("");
                lblFileDescription.setText("");
                return;
              }*/
        lblFileDescription.setText(stringBuilder.toString());
    }
    @FXML
    private void onBtnPickFileClicked3() {
        try {
            var fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            var chosenFile = fileChooser.showOpenDialog(null);
            lblSelectedFile.setText(chosenFile.getName());
        } catch (NullPointerException ex) {
            lblSelectedFile.setText("");
            System.err.println("%n Error occurred while choosing the file.");
        }
    }
}
