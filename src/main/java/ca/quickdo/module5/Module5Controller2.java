package ca.quickdo.module5;

import ca.quickdo.module5.model.Person;
import ca.quickdo.module5.services.PeopleService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
    private TableColumn<Person, String> colAvatar;

    @FXML
    private TableColumn<Person, String> colName;

    @FXML
    private TableColumn<Person, String> colPhone;

    @FXML
    private TableColumn<Person, String> colEmail;

    @FXML
    private TableColumn<Person, String> colCity;

    @FXML
    private TableColumn<Person, Integer> colAction;

    private void deletePerson(Person person) {
        executorService.submit(() -> {
            try {
                PeopleService.deleteById(person.getId());
                Platform.runLater(() -> people.remove(person));
            } catch (Exception e) {
                System.err.println(e);
            }
        });
    }

    @FXML
    private void initialize() {
        colAction.setSortable(false);
        colAction.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Integer o, boolean empty) {
                super.updateItem(o, empty);
                if (!empty) {
                    final var node = new Button("Delete");

                    node.setOnAction(e -> deletePerson(getTableRow().getItem()));
                    setGraphic(node);
                } else {
                    setGraphic(null);
                }
            }
        });
        colName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNames()));
        colPhone.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPhone()));
        colEmail.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEmail()));
        colCity.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCity()));
        colAvatar.setCellFactory(param -> new TableCell<Person, String>() {
            @Override
            protected void updateItem(String node, boolean isEmpty) {
                super.updateItem(node, isEmpty);
                if (!isEmpty && node != null && !node.isEmpty()) {
                    final var iv = new ImageView();
                    executorService.submit(() -> {
                        try (var stream = new URL(node).openConnection().getInputStream()) {
                            Platform.runLater(() -> {
                                final var image = new Image(stream);
                                iv.setImage(image);
                            });
                        } catch (Exception ex) {
                            System.err.println(ex);
                        }
                    });
                    setGraphic(iv);
                } else {
                    setGraphic(null);
                }
            }
        });
        colAvatar.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAvatar()));
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
                        tfEmail.textProperty().isEmpty()
                ).or(cbCity.valueProperty().isNull())
        );
        btnSave.setOnAction(e -> executorService.submit(() -> {
            try {
                final var person = PeopleService.save(Person.builder()
                        .names(tfName.getText())
                        .email(tfEmail.getText())
//                        .phone(tfPhone.getText())
                        .city(cbCity.getValue())
                        .build());
                Platform.runLater(() -> people.add(person));
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