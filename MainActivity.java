package com.example.weather_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    Button search;
    TextView show;
    String url;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityName.getText().toString().trim();
                if (!city.isEmpty()) {
                    url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "API key here";
                    fetchWeather(url);
                } else {
                    Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchWeather(String urlString) {
        executorService.execute(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }

                runOnUiThread(() -> parseWeather(result.toString()));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> show.setText("Cannot retrieve weather data."));
            }
        });
    }

    private void parseWeather(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject mainObject = jsonObject.getJSONObject("main");

            String weatherInfo = "Temperature: " + mainObject.getDouble("temp") + "°C\n" +
                    "Feels Like: " + mainObject.getDouble("feels_like") + "°C\n" +
                    "Humidity: " + mainObject.getInt("humidity") + "%\n" +
                    "Pressure: " + mainObject.getInt("pressure") + " hPa";

            show.setText(weatherInfo);
        } catch (Exception e) {
            e.printStackTrace();
            show.setText("Error parsing weather data.");
        }
    }
}
