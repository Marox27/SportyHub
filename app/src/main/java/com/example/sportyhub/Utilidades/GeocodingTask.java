package com.example.sportyhub.Utilidades;

import com.example.sportyhub.BuildConfig;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeocodingTask {
    private static final String API_KEY = BuildConfig.MAPS_API_KEY;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public interface GeocodingCallback {
        void onSuccess(double latitude, double longitude);
        void onError(String errorMessage);
    }

    public static void getCoordinates(String province, GeocodingCallback callback) {
        executor.execute(() -> {
            try {
                String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address="
                        + URLEncoder.encode(province, "UTF-8")
                        + "&key=" + API_KEY;
                System.out.println(API_KEY);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray results = jsonObject.getJSONArray("results");

                if (results.length() > 0) {
                    JSONObject location = results.getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location");

                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");

                    // Enviar resultado al hilo principal
                    handler.post(() -> callback.onSuccess(lat, lng));
                } else {
                    handler.post(() -> callback.onError("No se encontraron resultados"));
                }

            } catch (Exception e) {
                handler.post(() -> callback.onError("Error en la solicitud: " + e.getMessage()));
            }
        });
    }
}
