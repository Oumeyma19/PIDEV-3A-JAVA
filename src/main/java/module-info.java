module com.example.pidev {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pidev to javafx.fxml;
    exports com.example.pidev;
}