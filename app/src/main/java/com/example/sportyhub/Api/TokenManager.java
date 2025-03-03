package com.example.sportyhub.Api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {

    private static final String PREFS_NAME = "app_prefs";
    private static final String TOKEN_KEY = "auth_token";
    private SharedPreferences sharedPreferences;
    private SharedPreferences authPreferences;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        authPreferences = context.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
    }

    // Guardar token
    public void saveToken(String token) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply();
        Log.d("TOKEN_MANAGER", "Token al guardar: " + token);
    }

    // Obtener token
    public String getToken() {
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        Log.d("TOKEN_MANAGER", "Token recuperado: " + token);
        return token;
    }

    // Borrar token
    public void clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply();
    }

    public int getIdUser(){
        return authPreferences.getInt("id_user", -1);
    }

    public String getUsername(){
        return authPreferences.getString("username", "NOT FOUND");
    }

    public String getUserKey(){
        return authPreferences.getString("password", "NOT FOUND");
    }

}

