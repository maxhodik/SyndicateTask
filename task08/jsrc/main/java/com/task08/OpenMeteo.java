package com.task08;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenMeteo {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

    public String getWeatherForecast() throws IOException {
        String urlString = String.format("%s?latitude=%s&longitude=%s&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m",
                BASE_URL, "50.4375", "30.5");

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("HTTP Error: " + conn.getResponseCode());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();
        System.out.println(response.toString());
        return response.toString();
    }
}