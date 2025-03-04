package com.example.sportyhub.Api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class ApiClient {
    private static final String BASE_URL = "http://192.168.0.14:8080";//192.168.0.14:8080 192.168.228.108 https://sportyrest.onrender.com // URL base de tu API
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        TokenManager tokenManager = new TokenManager(context);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(tokenManager)) // Añadir interceptor
                .build();


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create()) // Para manejar respuestas de tipo String
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            // Inicializar TokenManager
            TokenManager tokenManager = new TokenManager(context);

            // Configurar OkHttpClient con AuthInterceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)       // Tiempo máximo para conectar
                    .readTimeout(15, TimeUnit.SECONDS)          // Tiempo máximo para leer datos
                    .writeTimeout(15, TimeUnit.SECONDS)         // Tiempo máximo para escribir datos
                    .addInterceptor(new AuthInterceptor(tokenManager)) // Añadimos el interceptor
                    .build();

            // Configurar Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Usamos el cliente con el interceptor
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }
}

