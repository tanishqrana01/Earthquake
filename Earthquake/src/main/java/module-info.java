module com.example.earthquake {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.earthquake to javafx.fxml;
    exports com.example.earthquake;
}