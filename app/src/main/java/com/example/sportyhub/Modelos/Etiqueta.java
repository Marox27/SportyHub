package com.example.sportyhub.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Etiqueta implements Parcelable {
    private long id;
    private String nombre;

    public Etiqueta() {
    }

    public Etiqueta(long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Métodos Parcelable

    protected Etiqueta(Parcel in) {
        id = in.readLong();
        nombre = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(nombre);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Etiqueta> CREATOR = new Creator<Etiqueta>() {
        @Override
        public Etiqueta createFromParcel(Parcel in) {
            return new Etiqueta(in);
        }

        @Override
        public Etiqueta[] newArray(int size) {
            return new Etiqueta[size];
        }
    };
}
