package com.example.weatherinformator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WeatherInformator extends Application {
    public String site = "gay";
    public Scene scene;
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(WeatherInformator.class.getResource("hello-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 700, 400);
        stage.setTitle("WeatherInformator");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}