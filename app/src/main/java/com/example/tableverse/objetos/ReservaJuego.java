package com.example.tableverse.objetos;

import java.io.Serializable;

public class ReservaJuego implements Serializable {
    private String id, id_juego, id_cliente, url_juego, nombre_juego;
    private boolean preparado;

    public ReservaJuego() {
        this.id = "";
        this.id_juego = "";
        this.id_cliente = "";
        this.nombre_juego = "";
        this.preparado = false;
        this.url_juego = "";
    }

    public ReservaJuego(String id_juego, String id_cliente, String nombre_juego) {
        this.id = "";
        this.id_juego = id_juego;
        this.id_cliente = id_cliente;
        this.nombre_juego = nombre_juego;
        this.preparado = false;
        this.url_juego = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_juego() {
        return id_juego;
    }

    public void setId_juego(String id_juego) {
        this.id_juego = id_juego;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getUrl_juego() {
        return url_juego;
    }

    public void setUrl_juego(String url_juego) {
        this.url_juego = url_juego;
    }

    public String getNombre_juego() {
        return nombre_juego;
    }

    public void setNombre_juego(String nombre_juego) {
        this.nombre_juego = nombre_juego;
    }

    public boolean isPreparado() {
        return preparado;
    }

    public void setPreparado(boolean preparado) {
        this.preparado = preparado;
    }
}
