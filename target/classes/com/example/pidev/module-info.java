module com.example.pidev {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires java.desktop;
    requires javafx.graphics;


    opens com.example.pidev to javafx.fxml;
    exports com.example.pidev;
}