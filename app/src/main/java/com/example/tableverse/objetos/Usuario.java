package com.example.tableverse.objetos;

import java.io.Serializable;

public class Usuario implements Serializable {
    String id, nombre, correo, contraseña, url_imagen, tipo;

    public Usuario() {
        this.id = "";
        this.nombre = "";
        this.correo = "";
        this.contraseña = "";
        this.tipo = "";
        this.url_imagen = "";
    }

    public Usuario(String nombre, String correo, String contraseña) {
        this.id = "";
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.tipo = "normal";
        this.url_imagen = "";
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }
}
