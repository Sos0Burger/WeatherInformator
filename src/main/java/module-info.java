module com.example.weatherinformator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.weatherinformator to javafx.fxml;
    exports com.example.weatherinformator;
}