package com.example.sportyhub.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Pago implements Parcelable {
    private String id;
    private Usuario usuario; // Referencia al usuario
    private Actividad actividad; // Referencia a la actividad
    private double cantidad;
    private String observaciones;
    private String fechaPago;
    private boolean reembolsado;
    private boolean liberado;

    public Pago(){}

    public Pago(String id, Usuario usuario, Actividad actividad, double cantidad, String observaciones, String fechaPago, boolean reembolsado, boolean liberado) {
        this.id = id;
        this.usuario = usuario;
        this.actividad = actividad;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.fechaPago = fechaPago;
        this.reembolsado = reembolsado;
        this.liberado = liberado;
    }

    public Pago(String id, Usuario usuario, Actividad actividad, double cantidad, String observaciones) {
        this.id = id;
        this.usuario = usuario;
        this.actividad = actividad;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
    }

    protected Pago(Parcel in) {
        id = in.readString();
        usuario = in.readParcelable(Usuario.class.getClassLoader(), Usuario.class);
        actividad = in.readParcelable(Actividad.class.getClassLoader(), Actividad.class);
        cantidad = in.readDouble();
        observaciones = in.readString();
        fechaPago = in.readString();
        reembolsado = in.readByte() != 0;
        liberado = in.readByte() != 0;
    }

    public static final Creator<Pago> CREATOR = new Creator<Pago>() {
        @Override
        public Pago createFromParcel(Parcel in) {
            return new Pago(in);
        }

        @Override
        public Pago[] newArray(int size) {
            return new Pago[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable((Parcelable) usuario, flags);
        dest.writeParcelable((Parcelable) actividad, flags);
        dest.writeDouble(cantidad);
        dest.writeString(observaciones);
        dest.writeString(fechaPago);
        dest.writeByte((byte) (reembolsado ? 1 : 0));
        dest.writeByte((byte) (liberado ? 1 : 0));
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Actividad getActividad() { return actividad; }
    public void setActividad(Actividad actividad) { this.actividad = actividad; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }

    public boolean isReembolsado() { return reembolsado; }
    public void setReembolsado(boolean reembolsado) { this.reembolsado = reembolsado; }

    public boolean isLiberado() { return liberado; }
    public void setLiberado(boolean liberado) { this.liberado = liberado; }
}

