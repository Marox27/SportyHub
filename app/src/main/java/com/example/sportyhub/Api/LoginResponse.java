package com.example.sportyhub.Api;

public class LoginResponse {
    private int idUsuario;
    private String token;

    public LoginResponse(int idUsuario, String token) {
        this.idUsuario = idUsuario;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Getters y setters
}
