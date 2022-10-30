package com.example.weatherinformator;

class Day{
    String temperature;
    String wet;
    String pressure;
    String windSpeed;
    String date;
    String weather;

    Day(String temperature, String wet, String pressure, String windSpeed, String weather, String date){
        this.pressure = pressure;
        this.wet = wet;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.weather = weather;
        this.date = date;
    }
}
