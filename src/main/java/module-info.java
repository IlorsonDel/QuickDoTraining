module ca.quickdo.intermediatejava {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens ca.quickdo.module2 to javafx.fxml;
    opens ca.quickdo.module4 to javafx.fxml;
    opens ca.quickdo.module5 to javafx.fxml;

    exports ca.quickdo.module2;
    exports ca.quickdo.module4;
    exports ca.quickdo.module5;
}