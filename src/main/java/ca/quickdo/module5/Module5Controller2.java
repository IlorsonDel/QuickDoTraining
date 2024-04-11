package ca.quickdo.module5;

import ca.quickdo.module5.model.Person;
import ca.quickdo.module5.services.PeopleService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Module5Controller2 {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final ObservableList<Person> people = FXCollections.observableArrayList();
    private final ObservableList<String> cities = FXCollections.observableArrayList();

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfPhone;

    @FXML
    private TextField tfEmail;

    @FXML
    private ComboBox<String> cbCity;

    @FXML
    private Button btnPickImage;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<Person> tvPeople;

    @FXML
    private TableColumn<Person, Node> colAvatar;

    @FXML
    private TableColumn<Person, String> colName;

    @FXML
    private TableColumn<Person, String> colPhone;

    @FXML
    private TableColumn<Person, String> colEmail;

    @FXML
    private TableColumn<Person, String> colCity;

    @FXML
    private TableColumn<Person, Object> colAction;

    @FXML
    private void initialize() {
        colName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNames()));
        colPhone.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPhone()));
        colEmail.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEmail()));
        colCity.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCity()));
        colAvatar.setCellFactory(param -> new TableCell<Person, Node>() {
            @Override
            protected void updateItem(Node node, boolean isEmpty) {
                super.updateItem(node, isEmpty);
                if (!isEmpty) {
                    setGraphic(node);
                } else {
                    setGraphic(null);
                }
            }
        });
        colAvatar.setCellValueFactory(param -> {
            ImageView imageView = new ImageView();
            executorService.submit(() -> {
                try (final var imageStream = new URL(param.getValue().getAvatar()).openConnection().getInputStream()) {
                    Platform.runLater(() -> imageView.setImage(new Image(imageStream)));
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            });
            return new SimpleObjectProperty<>(imageView);
        });
        tvPeople.setItems(people);
        cbCity.setItems(cities);
        executorService.submit(() -> {
            try {
                final var cities = PeopleService.findAllCities();
                Platform.runLater(() -> Module5Controller2.this.cities.setAll(cities));
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        });
        executorService.submit(() -> {
            try {
                final var results = PeopleService.getPeople(Integer.MAX_VALUE);
                Platform.runLater(() -> people.setAll(results));
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        });
        btnSave.disableProperty().bind(
                Bindings.or(
                        tfName.textProperty().isEmpty(),
                        tfPhone.textProperty().isEmpty()
                ).or(tfPhone.textProperty().isEmpty()
                        ).or(cbCity.valueProperty().isNull())
        );
        btnSave.setOnAction(e -> executorService.submit(() -> {
            try {
                final var person = PeopleService.save(Person.builder()
                        .names(tfName.getText())
                        .email(tfEmail.getText())
                        .phone(tfPhone.getText())
                        .city(cbCity.getValue())
                        .build());
                Platform.runLater(() -> {
                    people.add(person);
                    // Clear the text fields
                    tfName.clear();
                    tfEmail.clear();
                    tfPhone.clear();
                    cbCity.getSelectionModel().clearSelection();
                });
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }));
    }

    public void shutdown() throws SQLException {
        PeopleService.shutdown();
        executorService.shutdown();
    }

}