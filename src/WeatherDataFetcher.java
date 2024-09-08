import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataFetcher {
    private static final String TAG = "WeatherDataFetcher";

    // Use the Application context to ensure a single RequestQueue across the app
    private Context context;

    public WeatherDataFetcher(Context context) {
        this.context = context;
    }

    public void fetchData() {
        // API URL for weather data
        String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=30.269146&longitude=-97.75339&hourly=temperature_2m";

        // Create a RequestQueue using the Volley.newRequestQueue() method
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Create a JsonObjectRequest to fetch the JSON response from the provided URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse JSON
                        parseWeatherData(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching data: " + error.getMessage());
                    }
                });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    private void parseWeatherData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Extract necessary data from JSON
            double latitude = jsonObject.getDouble("latitude");
            double longitude = jsonObject.getDouble("longitude");

            JSONObject hourlyData = jsonObject.getJSONObject("hourly");
            JSONArray timeArray = hourlyData.getJSONArray("time");
            JSONArray temperatureArray = hourlyData.getJSONArray("temperature_2m");

            // Loop through the data
            for (int i = 0; i < timeArray.length(); i++) {
                String time = timeArray.getString(i);
                double temperature = temperatureArray.getDouble(i);

                // Now you can use the time and temperature data as needed
                Log.d(TAG, "Time: " + time + ", Temperature: " + temperature);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        }
    }
}