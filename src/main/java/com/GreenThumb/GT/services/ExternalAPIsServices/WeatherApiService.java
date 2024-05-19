package com.GreenThumb.GT.services.ExternalAPIsServices;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherApiService {

    public String getWeatherData(String location) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://yahoo-weather5.p.rapidapi.com/weather?location=" + location + "&format=json&u=f")
                .get()
                .addHeader("X-RapidAPI-Key", "5a7cfffe9dmshe56a19dd2841945p1d28d4jsnbe4e74179f2f")
                .addHeader("X-RapidAPI-Host", "yahoo-weather5.p.rapidapi.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }
}




