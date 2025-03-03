package com.example.sportyhub.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Notificacion implements Parcelable {
    private int idNotificacion;
    private Usuario emisor, receptor;
    private String titulo, mensaje, fechaCreacion;
    private boolean leida;

    public Notificacion() {
    }

    public Notificacion(Usuario emisor, Usuario receptor, String titulo, String mensaje) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }

    protected Notificacion(Parcel in) {
        idNotificacion = in.readInt();
        titulo = in.readString();
        mensaje = in.readString();
        fechaCreacion = in.readString();
        emisor = in.readParcelable(Usuario.class.getClassLoader(), Usuario.class);
        receptor = in.readParcelable(Usuario.class.getClassLoader(), Usuario.class);
        leida = in.readByte() != 0;
    }

    public static final Creator<Notificacion> CREATOR = new Creator<Notificacion>() {
        @Override
        public Notificacion createFromParcel(Parcel in) {
            return new Notificacion(in);
        }

        @Override
        public Notificacion[] newArray(int size) {
            return new Notificacion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idNotificacion);
        dest.writeString(titulo);
        dest.writeString(mensaje);
        dest.writeString(fechaCreacion);
        dest.writeParcelable((Parcelable) emisor, flags);
        dest.writeParcelable((Parcelable) receptor, flags);
        dest.writeByte((byte) (leida ? 1 : 0));
    }

    // Getters y setters

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }
}
