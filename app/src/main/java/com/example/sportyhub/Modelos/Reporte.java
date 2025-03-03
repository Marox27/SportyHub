package com.example.sportyhub.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Reporte implements Parcelable {
    private int id;
    private TipoDeReporte tipoDeReporte;
    private String motivo, descripcion, fechaCreacion;
    private Usuario usuarioReportante, usuarioReportado;
    private Actividad actividadReportada;
    private Equipo equipoReportado;
    private boolean revisado = false;

    public Reporte() {
    }

    // Reporte para una actividad
    public Reporte(String motivo, String descripcion, Usuario usuarioReportante,
                   Actividad actividadReportada) {
        this.tipoDeReporte = TipoDeReporte.ACTIVIDAD;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.usuarioReportante = usuarioReportante;
        this.actividadReportada = actividadReportada;
    }

    // Reporte para un usuario
    public Reporte(String motivo, String descripcion,
                   Usuario usuarioReportante, Usuario usuarioReportado) {
        this.tipoDeReporte = TipoDeReporte.USUARIO;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.usuarioReportante = usuarioReportante;
        this.usuarioReportado = usuarioReportado;
    }

    // Reporte para un equipo
    public Reporte(String motivo, String descripcion,
                   Usuario usuarioReportante, Equipo equipoReportado) {
        this.tipoDeReporte = TipoDeReporte.EQUIPO;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.usuarioReportante = usuarioReportante;
        this.equipoReportado = equipoReportado;
    }

    protected Reporte(Parcel in) {
        id = in.readInt();
        tipoDeReporte = TipoDeReporte.valueOf(in.readString());
        motivo = in.readString();
        descripcion = in.readString();
        fechaCreacion = in.readString();
        usuarioReportante = in.readParcelable(Usuario.class.getClassLoader(), Usuario.class);
        usuarioReportado = in.readParcelable(Usuario.class.getClassLoader(), Usuario.class);
        actividadReportada = in.readParcelable(Actividad.class.getClassLoader(), Actividad.class);
        equipoReportado = in.readParcelable(Equipo.class.getClassLoader(), Equipo.class);
        revisado = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(tipoDeReporte.name());
        parcel.writeString(motivo);
        parcel.writeString(descripcion);
        parcel.writeString(fechaCreacion);
        parcel.writeParcelable(usuarioReportante, flags);
        parcel.writeParcelable(usuarioReportado, flags);
        parcel.writeParcelable(actividadReportada, flags);
        parcel.writeParcelable(equipoReportado, flags);
        parcel.writeByte((byte) (revisado ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Reporte> CREATOR = new Creator<Reporte>() {
        @Override
        public Reporte createFromParcel(Parcel in) {
            return new Reporte(in);
        }

        @Override
        public Reporte[] newArray(int size) {
            return new Reporte[size];
        }
    };

    public enum TipoDeReporte {
        ACTIVIDAD, USUARIO, EQUIPO
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoDeReporte getTipoDeReporte() {
        return tipoDeReporte;
    }

    public void setTipoDeReporte(TipoDeReporte tipoDeReporte) {
        this.tipoDeReporte = tipoDeReporte;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Usuario getUsuarioReportante() {
        return usuarioReportante;
    }

    public void setUsuarioReportante(Usuario usuarioReportante) {
        this.usuarioReportante = usuarioReportante;
    }

    public Usuario getUsuarioReportado() {
        return usuarioReportado;
    }

    public void setUsuarioReportado(Usuario usuarioReportado) {
        this.usuarioReportado = usuarioReportado;
    }

    public Actividad getActividadReportada() {
        return actividadReportada;
    }

    public void setActividadReportada(Actividad actividadReportada) {
        this.actividadReportada = actividadReportada;
    }

    public Equipo getEquipoReportado() {
        return equipoReportado;
    }

    public void setEquipoReportado(Equipo equipoReportado) {
        this.equipoReportado = equipoReportado;
    }

    public boolean isRevisado() {
        return revisado;
    }

    public void setRevisado(boolean revisado) {
        this.revisado = revisado;
    }
}
