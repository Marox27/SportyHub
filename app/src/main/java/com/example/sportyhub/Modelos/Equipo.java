package com.example.sportyhub.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Equipo implements Parcelable {
    private Integer idequipo;
    private int creador, deporte, miembros;
    private String nombre, provincia, municipio, privacidad, detalles, imagen;

    public Equipo() {
    }

    public Equipo(int creador, int deporte, String nombre, String provincia, String municipio, String privacidad, String detalles, String imagen) {
        this.creador = creador;
        this.deporte = deporte;
        this.nombre = nombre;
        this.provincia = provincia;
        this.municipio = municipio;
        this.privacidad = privacidad;
        this.detalles = detalles;
        this.imagen = imagen;
    }

    public Equipo(int idequipo, int creador, int deporte, int miembros, String nombre, String provincia, String municipio, String privacidad, String detalles, String imagen) {
        this.idequipo = idequipo;
        this.creador = creador;
        this.deporte = deporte;
        this.miembros = miembros;
        this.nombre = nombre;
        this.provincia = provincia;
        this.municipio = municipio;
        this.privacidad = privacidad;
        this.detalles = detalles;
        this.imagen = imagen;
    }



    public Integer getIdequipo() {
        return idequipo;
    }

    public void setIdequipo(int idequipo) {
        this.idequipo = idequipo;
    }

    public int getCreador() {
        return creador;
    }

    public void setCreador(int creador) {
        this.creador = creador;
    }

    public int getDeporte() {
        return deporte;
    }

    public String getDeporteName() {
        switch (deporte) {
            case 1:
                return "Fútbol 7";
            case 2:
                return "Fútbol";
            case 3:
                return "Fútsal";
            case 4:
                    return "Tenis";
            case 5:
                return "Pádel";
            case 6:
                return "Baloncesto";
            case 7:
                return "Béisbol";
        }
        return "UNKNOW";
    }

    public void setDeporte(int deporte) {
        this.deporte = deporte;
    }

    public int getMiembros() {
        return miembros;
    }

    public void setMiembros(int miembros) {
        this.miembros = miembros;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getPrivacidad() {
        return privacidad;
    }

    public void setPrivacidad(String privacidad) {
        this.privacidad = privacidad;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString(){
        return "Información del Equipo:\n" +
                "Id: " + idequipo +
                "\nNombre:" + nombre +
                "\nDeporte:" + deporte +
                "\nCreador:" + creador;
    }


    // Implementación de Parcelable
    protected Equipo(Parcel in) {
        idequipo = in.readInt();
        creador = in.readInt();
        deporte = in.readInt();
        miembros = in.readInt();
        nombre = in.readString();
        provincia = in.readString();
        municipio = in.readString();
        privacidad = in.readString();
        detalles = in.readString();
        imagen = in.readString();
    }

    public static final Creator<Equipo> CREATOR = new Creator<Equipo>() {
        @Override
        public Equipo createFromParcel(Parcel in) {
            return new Equipo(in);
        }

        @Override
        public Equipo[] newArray(int size) {
            return new Equipo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idequipo);
        dest.writeInt(creador);
        dest.writeInt(deporte);
        dest.writeInt(miembros);
        dest.writeString(nombre);
        dest.writeString(provincia);
        dest.writeString(municipio);
        dest.writeString(privacidad);
        dest.writeString(detalles);
        dest.writeString(imagen);
    }
}
