package ca.quickdo.module4;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemsController {

    private final ObservableList<String> items = FXCollections.observableArrayList();

    @FXML
    private TextField tfInput;

    @FXML
    private Button btnAddItem;

    @FXML
    private ListView<String> lvItems;

    @FXML
    private void initialize() {
        lvItems.setItems(items);
        btnAddItem.disableProperty().bind(tfInput.textProperty().isEmpty());
        btnAddItem.setOnAction(actionEvent ->{
            items.add(tfInput.getText());
            tfInput.clear();
            // You must add the non-redundant function
        });
        lvItems.setCellFactory(stringListView -> new ListCell<>() {
            @Override
            protected void updateItem(String s, boolean isEmpty) {
                super.updateItem(s, isEmpty);
                if (!isEmpty) {
                    setText(s);
                    setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
                            final var index = getIndex();
                            items.remove(index);
                        }
                    });
                }else {
                    setText(null);
                }
            }
        });
        items.addListener((ListChangeListener<? super String>) change -> {
            System.out.println();
            for (int i = 0; i <= items.size() -1; i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    if(items.get(j).equals(items.get(i))) {
                        System.out.println(items.get(j));
                        break;
                    }
                }
            }
        });
    }


}
