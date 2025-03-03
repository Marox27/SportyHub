package com.example.sportyhub.Modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Participante implements Serializable {
    private String nickname;
    private int id;
    private String pfp;

    private Usuario usuario;


    public Participante(Usuario usuario, int id) {
        this.usuario = usuario;
        this.id = id;
    }

    public Participante(String nickname, int id, String pfp) {
        this.nickname = nickname;
        this.id = id;
        this.pfp = pfp;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
