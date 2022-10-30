package com.example.weatherinformator;

import java.sql.Connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Controller {
    public static String currentSite;
    ArrayList<String> WeatherDays= new ArrayList<>();
    ObservableList<String> sites = FXCollections.observableArrayList("Yandex","Meteoprog");

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
        findweather.setOnAction(actionEvent -> {
            if (siteChange.getSelectionModel().getSelectedItem().equals("Meteoprog")) {
                currentSite = "meteoprog";
                try {
                    location = new URL("https://www.meteoprog.com/ru/weather/Penza/month/");
                    StringBuilder siteHTML = new StringBuilder();
                    try {
                        URLConnection uc = location.openConnection();
                        uc.connect();
                        uc = location.openConnection();
                        uc.addRequestProperty("User-Agent",
                                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
                            String i;
                            while ((i = br.readLine()) != null) {
                                siteHTML.append(i);
                            }
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    String[] weatherData = siteHTML.toString().split("<div class=\"city-month__days\">")[1].split("°</span>");
                    Pattern pattern = Pattern.compile("<div class=\"city-month__day(.*)");

                    for (int i = 0; i < weatherData.length; i++) {
                        Matcher matcher = pattern.matcher(weatherData[i]);
                        if (matcher.find()) {
                            WeatherDays.add(matcher.group());
                        }
                    }



                    Pattern temperatureRegExp = Pattern.compile("[-+](\\d)|\\d°");
                    Pattern dateRegExp = Pattern.compile("(\\d\\d|\\d) [А-я]+");
                    Pattern weatherRegExp = Pattern.compile("title=\"([А-я]|,| )+");

                    ArrayList<Day> dayArray = new ArrayList<>();

                    for (int i = 0; i < WeatherDays.size(); i++) {
                        String temperature = "";
                        String date = "";
                        String pressure = "";
                        String wet = "";
                        String windSpeed = "";
                        String weather = "";
                        Matcher matcher = temperatureRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            temperature = matcher.group().replaceAll("\\+|°","");
                        }
                        matcher = dateRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            date = matcher.group();
                        }
                        matcher = weatherRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            weather = matcher.group().replace("title=\"","");
                        }
                        dayArray.add(new Day(temperature, wet, pressure, windSpeed, weather, date));
                    }
                    try{
                        String url = "jdbc:mysql://localhost/weather";
                        String username = "root";
                        String password = "password";
                        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                        try (Connection conn = DriverManager.getConnection(url, username, password)){
                            System.out.println("Connection to DB succesfull!");
                            Statement statement = conn.createStatement();
                            statement.executeUpdate("DELETE FROM meteoprog");
                            for (Day item: dayArray
                            ) {
                                statement.executeUpdate("INSERT meteoprog(temperature, wet, pressure, windSpeed, date, weather) VALUES ('" +item.temperature +"', '" + item.wet+"', '" +item.pressure +"', '" + item.windSpeed+"', '" +item.date +"', '" +item.weather+"')" );
                            }


                        }
                    }
                    catch(Exception ex){
                        System.out.println("Connection failed...");

                        System.out.println(ex.getMessage());
                    }
                    createWindow("meteoprog");
                } catch (MalformedURLException ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (siteChange.getSelectionModel().getSelectedItem().equals("Yandex")) {
                currentSite = "yandex";
                try {
                    location = new URL("https://yandex.ru/pogoda/month");
                    StringBuilder siteHTML = new StringBuilder();
                    try {
                        URLConnection uc = location.openConnection();
                        uc.connect();
                        uc = location.openConnection();
                        uc.addRequestProperty("User-Agent",
                                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()))) {
                            String i;
                            while ((i = br.readLine()) != null) {
                                siteHTML.append(i);
                            }
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    String[] weatherData = siteHTML.toString().split("</span></div></div></div></div></div></div></div>");

                    Pattern pattern = Pattern.compile("<div class=\"climate-calendar-day( climate-calendar-day_holiday | climate-calendar-day_colorless_yes |\\s)climate-calendar-day_with-history\"(.*)");

                    for (int i = 0; i < weatherData.length; i++) {
                        Matcher matcher = pattern.matcher(weatherData[i]);
                        if (matcher.find()) {
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

                    for (int i = 0; i < WeatherDays.size(); i++) {
                        String temperature = "";
                        String date = new String();
                        String pressure = new String();
                        String wet = new String();
                        String windSpeed = new String();
                        String weather = new String();
                        Matcher matcher = temperatureRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            temperature = matcher.group().replaceAll("temp__value temp__value_with-unit\">|<", "");
                        }
                        matcher = dateRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            date = matcher.group().replaceAll("\"climate-calendar-day__detailed-day\">", "");
                        }
                        matcher = pressureRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            pressure = matcher.group();
                        }
                        matcher = wetRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            wet = matcher.group();
                        }
                        matcher = windSpeedRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            windSpeed = matcher.group().replace("\"wind-speed\">", "") + " м/c";
                        }
                        matcher = weatherRegExp.matcher(WeatherDays.get(i));
                        if (matcher.find()) {
                            weather = matcher.group().replaceAll("src=\"//yastatic\\.net/weather/i/icons/funky/dark/|\"|\\.svg", "");
                            switch (weather) {
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
                                case"ovc_sn":
                                    weather = "Снег";
                                    break;
                                case"skc_d":
                                    weather = "Солнечно";
                                    break;
                            }
                        }
                        dayArray.add(new Day(temperature, wet, pressure, windSpeed, weather, date));

                    }


                    try{
                        String url = "jdbc:mysql://localhost/weather";
                        String username = "root";
                        String password = "password";
                        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                        try (Connection conn = DriverManager.getConnection(url, username, password)){
                            System.out.println("Connection to DB succesfull!");
                            Statement statement = conn.createStatement();
                            statement.executeUpdate("DELETE FROM yandex");
                            for (Day item: dayArray
                            ) {
                                statement.executeUpdate("INSERT yandex(temperature, wet, pressure, windSpeed, date, weather) VALUES ('" +item.temperature +"', '" + item.wet+"', '" +item.pressure +"', '" + item.windSpeed+"', '" +item.date +"', '" +item.weather+"')" );
                            }

                        }
                    }
                    catch(Exception ex){
                        System.out.println("Connection failed...");

                        System.out.println(ex.getMessage());
                    }
                    createWindow("yandex");

                } catch (MalformedURLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    public void createWindow(String site){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WeatherInfo.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage1 = new Stage();
            stage1.setScene(new Scene(root1));
            stage1.setTitle(site);
            stage1.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
