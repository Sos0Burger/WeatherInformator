package com.example.weatherinformator;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class weatherController {

    public static String site;

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private URL location;

    @FXML
    private Label Temperature;

    @FXML
    private Label dateLabel;

    @FXML
    private Button leftbutton;

    @FXML
    private Label pressureLabel;

    @FXML
    private Button recomendationButton;

    @FXML
    private Label recomendationText;

    @FXML
    private Button rightButton;

    @FXML
    private Label weatherLabel;

    @FXML
    private Label wetLabel;

    @FXML
    private Label windSpeedLabel;

    @FXML
    void initialize() {
        int position = 0;
        assert Temperature != null : "fx:id=\"Temperature\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert anchorPane != null : "fx:id=\"anchorPane\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert dateLabel != null : "fx:id=\"dateLabel\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert leftbutton != null : "fx:id=\"leftbutton\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert pressureLabel != null : "fx:id=\"pressureLabel\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert recomendationButton != null : "fx:id=\"recomendationButton\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert recomendationText != null : "fx:id=\"recomendationText\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert rightButton != null : "fx:id=\"rightButton\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert weatherLabel != null : "fx:id=\"weatherLabel\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert wetLabel != null : "fx:id=\"wetLabel\" was not injected: check your FXML file 'WeatherInfo.fxml'.";
        assert windSpeedLabel != null : "fx:id=\"windxSpeedLabel\" was not injected: check your FXML file 'WeatherInfo.fxml'.";

        ArrayList<Day> weatherDays = new ArrayList<>();
        String siteName = site;

        try{
            String url = "jdbc:mysql://localhost/weather";
            String username = "root";
            String password = "password";
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            try (Connection conn = DriverManager.getConnection(url, username, password)){
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM yandex");

                while(resultSet.next()){
                    weatherDays.add(new Day(resultSet.getString("temperature"),resultSet.getString("wet"),resultSet.getString("pressure"),resultSet.getString("windSpeed"),resultSet.getString("date"),resultSet.getString("weather")));
                }

            }
            catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        catch(Exception ex){
            System.out.println("Connection failed...");
            System.out.println(ex.getMessage());
        }

        if(!weatherDays.get(position).temperature.equals("")){
            Temperature.setVisible(true);
            Temperature.setText(Temperature.getText()+weatherDays.get(position).temperature);
        }
        if(!weatherDays.get(position).date.equals("")){
            dateLabel.setVisible(true);
            dateLabel.setText(dateLabel.getText()+weatherDays.get(position).date);
        }
        if(!weatherDays.get(position).weather.equals("")){
            weatherLabel.setVisible(true);
            weatherLabel.setText(weatherLabel.getText()+weatherDays.get(position).weather);
        }
        if(!weatherDays.get(position).wet.equals("")){
            wetLabel.setVisible(true);
            wetLabel.setText(wetLabel.getText()+weatherDays.get(position).wet);
        }
        if(!weatherDays.get(position).windSpeed.equals("")){
            windSpeedLabel.setVisible(true);
            windSpeedLabel.setText(windSpeedLabel.getText()+weatherDays.get(position).windSpeed);
        }
        if(!weatherDays.get(position).pressure.equals("")){
            pressureLabel.setVisible(true);
            pressureLabel.setText(pressureLabel.getText()+weatherDays.get(position).pressure);
        }


    }

}
