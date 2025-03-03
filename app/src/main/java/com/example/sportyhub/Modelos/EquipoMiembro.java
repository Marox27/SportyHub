package com.example.sportyhub.Modelos;

public class EquipoMiembro {

    private int idMiembro;
    private Equipo equipo;
    private Usuario usuario;
    private Rol rol;

    public enum Rol {
        MIEMBRO, ADMIN
    }

    // Constructor
    public EquipoMiembro(Equipo equipo, Usuario usuario, Rol rol) {
        this.equipo = equipo;
        this.usuario = usuario;
        this.rol = rol;
    }

    // Getters y Setters
    public int getIdMiembro() {
        return idMiembro;
    }

    public void setIdMiembro(int idMiembro) {
        this.idMiembro = idMiembro;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

}
