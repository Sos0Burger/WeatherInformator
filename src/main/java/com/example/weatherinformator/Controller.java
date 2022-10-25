package com.example.weatherinformator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class Controller {
    ArrayList<String> WeatherDays= new ArrayList<>();
    ObservableList<String> sites = FXCollections.observableArrayList("Yandex","Gismeteo");

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button findweather;

    @FXML
    private AnchorPane MainWindow;

    @FXML
    private ListView<String> siteChange;
    @FXML
    void initialize() {
        siteChange.setItems(sites);
        findweather.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (siteChange.getSelectionModel().getSelectedItem().equals("Yandex")) {
                    try {
                        location = new URL("https://yandex.ru/pogoda/month");
                    } catch (MalformedURLException ex) {
                        System.out.println(ex.getMessage());
                    }
                } else if (siteChange.getSelectionModel().getSelectedItem().equals("Gismeteo")) {
                    //доделать
                }

                StringBuilder siteHTML = new StringBuilder();
                try {
                    URLConnection uc = location.openConnection();
                    uc.connect();
                    uc = location.openConnection();
                    uc.addRequestProperty("User-Agent",
                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                    try(BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()))){
                        String i;
                        while((i = br.readLine())!=null ){
                            siteHTML.append(i);
                        }
                    }
                    catch (Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
                catch (Exception ex){
                    System.out.println(ex.getMessage());
                }
                String[] weatherData = siteHTML.toString().split("</span></div></div></div></div></div></div></div>");

                Pattern pattern = Pattern.compile("<div class=\"climate-calendar-day( climate-calendar-day_holiday | climate-calendar-day_colorless_yes |\\s)climate-calendar-day_with-history\"(.*)");

                for (int i = 0; i< weatherData.length;i++){
                    Matcher matcher = pattern.matcher(weatherData[i]);
                    if(matcher.find()){
                        WeatherDays.add(matcher.group());
                    }
                }

                Pattern temperatureRegExp = Pattern.compile("temp__value temp__value_with-unit\">([+−])(\\d|\\d\\d)<");
                Pattern dateRegExp = Pattern.compile("(\\d\\d|\\d) [А-я]+");
                Pattern pressureRegExp = Pattern.compile("\\d\\d\\d мм рт\\. ст\\.");
                Pattern wetRegExp = Pattern.compile("\\d\\d%");
                Pattern windSpeedRegExp = Pattern.compile("\"wind-speed\">((\\d\\.\\d)|(\\d))");
                Pattern weatherRegExp = Pattern.compile("src=\"//yastatic\\.net/weather/i/icons/funky/dark/(\\w|-)+\\.svg\"");

                ArrayList<Day> dayArray = new ArrayList<>();

                for (int i = 0; i< WeatherDays.size();i++){
                    int temperature = 0;
                    String date = new String();
                    String pressure = new String();
                    String wet = new String();
                    String windSpeed = new String();
                    String weather = new String();
                    Matcher matcher = temperatureRegExp.matcher(WeatherDays.get(i));
                    if(matcher.find()){
                        temperature = Integer.parseInt(matcher.group().replaceAll("temp__value temp__value_with-unit\">|<",""));
                    }
                    matcher = dateRegExp.matcher(WeatherDays.get(i));
                    if(matcher.find()){
                        date = matcher.group().replaceAll("\"climate-calendar-day__detailed-day\">","");
                    }
                    matcher = pressureRegExp.matcher(WeatherDays.get(i));
                    if(matcher.find()){
                        pressure = matcher.group();
                    }
                    matcher = wetRegExp.matcher(WeatherDays.get(i));
                    if(matcher.find()){
                        wet = matcher.group();
                    }
                    matcher = windSpeedRegExp.matcher(WeatherDays.get(i));
                    if(matcher.find()){
                        windSpeed = matcher.group().replace("\"wind-speed\">","")+" м/c";
                    }
                    matcher = weatherRegExp.matcher(WeatherDays.get(i));
                    if(matcher.find()){
                        weather = matcher.group().replaceAll("src=\"//yastatic\\.net/weather/i/icons/funky/dark/|\"|\\.svg","");
                        switch (weather){
                            case "ovc_-ra":
                            case "ovc_ra":
                                weather = "Дождь";
                                break;
                            case "bkn_d":
                                weather = "Облачно с прояснениями";
                                break;
                            case "ovc":
                                weather = "Облачно";
                                break;
                            case "ovc_-sn":
                                weather = "Снег";
                                break;
                        }
                    }
                    dayArray.add(new Day(temperature,wet,pressure,windSpeed,weather,date));
                }


            }
        });
    }

}
class Day{
    int temperature;
    String wet;
    String pressure;
    String windSpeed;
    String date;
    String weather;

    Day(int temperature, String wet, String pressure, String windSpeed, String weather, String date){
        this.pressure = pressure;
        this.wet = wet;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.weather = weather;
        this.date = date;
    }
}