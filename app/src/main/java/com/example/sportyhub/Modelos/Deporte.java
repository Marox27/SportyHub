package com.example.sportyhub.Modelos;

import java.io.Serializable;

public class Deporte implements Serializable {
    private int idDeporte;
    private String nombre;
    private String normativa;
    private int minJugadores;
    private int maxJugadores;
    private int imagenResId;

    public Deporte(){}

    public Deporte(int idDeporte, String nombre, String normativa, int minJugadores, int maxJugadores, int imagenResId) {
        this.idDeporte = idDeporte;
        this.nombre = nombre;
        this.normativa = normativa;
        this.minJugadores = minJugadores;
        this.maxJugadores = maxJugadores;
        this.imagenResId = imagenResId;
    }

    public int getIdDeporte() { return idDeporte; }
    public String getNombre() { return nombre; }
    public String getNormativa() { return normativa; }
    public int getMinJugadores() { return minJugadores; }
    public int getMaxJugadores() { return maxJugadores; }
    public int getImagenResId() { return imagenResId; }

    public void setIdDeporte(int idDeporte) {
        this.idDeporte = idDeporte;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNormativa(String normativa) {
        this.normativa = normativa;
    }

    public void setMinJugadores(int minJugadores) {
        this.minJugadores = minJugadores;
    }

    public void setMaxJugadores(int maxJugadores) {
        this.maxJugadores = maxJugadores;
    }

    public void setImagenResId(int imagenResId) {
        this.imagenResId = imagenResId;
    }
}
