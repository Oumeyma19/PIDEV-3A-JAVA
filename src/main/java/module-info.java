module com.example.pidev {
    requires javafx.fxml;
    requires java.sql;
    requires bcrypt;
    requires org.controlsfx.controls;
    requires javafx.media;


    opens com.example.pidev to javafx.fxml;
    opens com.example.pidev.models to javafx.base;

    exports com.example.pidev;
    exports com.example.pidev.controllers;
    exports com.example.pidev.models;
    opens com.example.pidev.controllers to javafx.fxml;
}