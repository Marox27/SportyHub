package com.example.sportyhub.Modelos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Usuario implements Parcelable {
    @SerializedName("id")
    private Long idUsuario;

    @SerializedName("name")
    private String nombre;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("email")
    private String email;

    @SerializedName("telefono")
    private String telefono;

    @SerializedName("password")
    private String password;

    @SerializedName("fecha_nacimiento")
    private String fecha_nacimiento;

    @SerializedName("ciudad")
    private String ciudad;

    @SerializedName("pfp")
    private String pfp;

    @SerializedName("admin")
    private boolean admin;

    @SerializedName("activo")
    private boolean activo;

    @SerializedName("baneado")
    private boolean baneado;

    public Usuario() {}

    public Usuario(String nombre, String apellidos, String nickname, String email, String password, String fecha_nacimiento, String ciudad, String pfp) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.fecha_nacimiento = fecha_nacimiento;
        this.ciudad = ciudad;
        this.pfp = pfp;
    }

    protected Usuario(Parcel in) {
        if (in.readByte() == 0) {
            idUsuario = null;
        } else {
            idUsuario = in.readLong();
        }
        nombre = in.readString();
        apellidos = in.readString();
        nickname = in.readString();
        email = in.readString();
        telefono = in.readString();
        password = in.readString();
        fecha_nacimiento = in.readString();
        ciudad = in.readString();
        pfp = in.readString();
        admin = in.readByte() != 0;
        activo = in.readByte() != 0;
        baneado = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (idUsuario == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(idUsuario);
        }
        dest.writeString(nombre);
        dest.writeString(apellidos);
        dest.writeString(nickname);
        dest.writeString(email);
        dest.writeString(telefono);
        dest.writeString(password);
        dest.writeString(fecha_nacimiento);
        dest.writeString(ciudad);
        dest.writeString(pfp);
        dest.writeByte((byte) (admin ? 1 : 0));
        dest.writeByte((byte) (activo ? 1 : 0));
        dest.writeByte((byte) (baneado ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    // Getters y setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean isAdmin) {
        this.admin = isAdmin;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isBaneado() {
        return baneado;
    }

    public void setBaneado(boolean baneado) {
        this.baneado = baneado;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", password='" + password + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", fecha nacimiento='" + fecha_nacimiento + '\'' +
                ", pfp='" + pfp + '\'' +
                ", isAdmin=" + admin +
                ", activo=" + activo +
                ", baneado=" + baneado +
                '}';
    }
}
