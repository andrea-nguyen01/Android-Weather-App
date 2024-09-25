package com.example.myweatherapp;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView textView;
    private TextView textView2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews
        textView = findViewById(R.id.cityName);
        textView2 = findViewById(R.id.temperature);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // URL for Open Meteo Weather Forecast API
        String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=30.2672&longitude=-97.7431&hourly=temperature_2m,relative_humidity_2m,rain";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String output = "";
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray timeArray = jsonObject.getJSONObject("hourly").getJSONArray("time");
                            JSONArray temperatureArray = jsonObject.getJSONObject("hourly").getJSONArray("temperature_2m");
                            JSONArray humidityArray = jsonObject.getJSONObject("hourly").getJSONArray("relative_humidity_2m");
                            JSONArray rainArray = jsonObject.getJSONObject("hourly").getJSONArray("rain");

                            String[] dates = new String[7];
                            double[] temperatureData = new double[7];
                            double[] humidityData = new double[7];
                            double[] rainData = new double[7];
                            // Outer for loop runs for each day
                            for (int i = 0; i < 7; i++) {
                                double temperatureSum = 0;
                                double humiditySum = 0;
                                double rainSum = 0;
                                // Inner for loop runs for each hour of the day
                                for (int j = i * 24; j < i * 24 + 24; j++) {
                                    temperatureSum += temperatureArray.getDouble(j);
                                    humiditySum += humidityArray.getDouble(j);
                                    rainSum += rainArray.getDouble(j);
                                }
                                dates[i] = timeArray.getString(i * 24).substring(0, 10);
                                temperatureData[i] = roundToOneDecimalPlace(temperatureSum / 24) + 32;
                                humidityData[i] = roundToOneDecimalPlace(humiditySum / 24);
                                rainData[i] = roundToOneDecimalPlace(rainSum / 24);
                                output += dates[i] + "       " + temperatureData[i] + "°F\n"
                                        + "Humidity: " + humidityData[i] + "%  " + "Rain: " + rainData[i] + "%\n\n";
                            }
                            String todaysTemperature = temperatureData[0] + "°F";
                            textView2.setText(todaysTemperature);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        textView.setText(output);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error fetching data: " + error.getMessage());
                textView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private double roundToOneDecimalPlace(double number) {
        String formattedNum = String.format("%.1f", number);
        return Double.parseDouble(formattedNum);
    }

}