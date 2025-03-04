package com.example.sportyhub.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Actividad implements Parcelable {
    private String titulo, descripcion, lugar, fecha, hora, fecha_publicacion, fechaLimiteCancelacion;
    @SerializedName("idactividad")
    private int idActividad;
    private int duracion, num_participantes, participantesNecesarios, creador, deporte;
    private double precio, latitud, longitud;
    private boolean activo;
    private Set<Etiqueta> etiquetas = new HashSet<>();

    public Actividad() {}

    public Actividad(String titulo, String lugar, String fecha, String hora,
                     int idActividad, int duracion, int num_participantes, int participantesNecesarios,
                     int creador, int deporte, double precio, double latitud, double longitud, Set<Etiqueta> etiquetas) {
        this.titulo = titulo;
        this.lugar = lugar;
        this.fecha = fecha;
        this.hora = hora;
        this.idActividad = idActividad;
        this.duracion = duracion;
        this.num_participantes = num_participantes;
        this.participantesNecesarios = participantesNecesarios;
        this.creador = creador;
        this.deporte = deporte;
        this.precio = precio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.etiquetas = etiquetas;
    }

    public Actividad(String titulo, String descripcion, String lugar, String fecha, String hora, String fechaLimiteCancelacion,
                     int duracion, int participantesNecesarios, int creador, int deporte,
                     double precio, double latitud, double longitud, Set<Etiqueta> etiquetas) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fecha = fecha;
        this.hora = hora;
        this.fechaLimiteCancelacion = fechaLimiteCancelacion;
        this.duracion = duracion;
        this.participantesNecesarios = participantesNecesarios;
        this.creador = creador;
        this.deporte = deporte;
        this.precio = precio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.etiquetas = etiquetas;
    }

    // Métodos de getter y setter aquí (sin cambios)

    // Parcelable implementation

    protected Actividad(Parcel in) {
        titulo = in.readString();
        descripcion = in.readString();
        lugar = in.readString();
        fecha = in.readString();
        hora = in.readString();
        fecha_publicacion = in.readString();
        fechaLimiteCancelacion = in.readString();
        idActividad = in.readInt();
        duracion = in.readInt();
        num_participantes = in.readInt();
        participantesNecesarios = in.readInt();
        creador = in.readInt();
        deporte = in.readInt();
        precio = in.readDouble();
        latitud = in.readDouble();
        longitud = in.readDouble();
        activo = in.readBoolean();
        etiquetas = new HashSet<>();

        // Leer el Set de etiquetas de manera actualizada
        etiquetas = new HashSet<>(in.createTypedArrayList(Etiqueta.CREATOR));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeString(lugar);
        dest.writeString(fecha);
        dest.writeString(hora);
        dest.writeString(fecha_publicacion);
        dest.writeString(fechaLimiteCancelacion);
        dest.writeInt(idActividad);
        dest.writeInt(duracion);
        dest.writeInt(num_participantes);
        dest.writeInt(participantesNecesarios);
        dest.writeInt(creador);
        dest.writeInt(deporte);
        dest.writeDouble(precio);
        dest.writeDouble(latitud);
        dest.writeDouble(longitud);
        dest.writeBoolean(activo);
        dest.writeTypedList(new ArrayList<>(etiquetas));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Actividad> CREATOR = new Creator<Actividad>() {
        @Override
        public Actividad createFromParcel(Parcel in) {
            return new Actividad(in);
        }

        @Override
        public Actividad[] newArray(int size) {
            return new Actividad[size];
        }
    };


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getParticipantesNecesarios() {
        return participantesNecesarios;
    }

    public void setParticipantesNecesarios(int participantesNecesarios) {
        this.participantesNecesarios = participantesNecesarios;
    }

    public int getNum_participantes() {
        return num_participantes;
    }

    public void setNum_participantes(int num_participantes) {
        this.num_participantes = num_participantes;
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
                return "Fútbol 11";
            case 3:
                return "Fútbol Sala";
            case 4:
                return "Tenis";
            case 5:
                return "Padel";
            case 6:
                return "Baloncesto";
            case 7:
                return "Beisbol";
        }
        return null;
    }

    public void setDeporte(int deporte) {
        this.deporte = deporte;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFecha_publicacion() {
        return fecha_publicacion;
    }

    public void setFecha_publicacion(String fecha_publicacion) {
        this.fecha_publicacion = fecha_publicacion;
    }

    public String getFechaLimiteCancelacion() {
        return fechaLimiteCancelacion;
    }

    public void setFechaLimiteCancelacion(String fechaLimiteCancelacion) {
        this.fechaLimiteCancelacion = fechaLimiteCancelacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(Set<Etiqueta> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public Set<String>getEtiquetasString(){
        Set<String> nombreEtiquetas = new HashSet<>();
        for (Etiqueta etiqueta: etiquetas) {
            nombreEtiquetas.add(etiqueta.getNombre());
        }
        return nombreEtiquetas;
    }
}
