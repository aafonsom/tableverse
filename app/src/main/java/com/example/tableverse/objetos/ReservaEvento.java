package com.example.tableverse.objetos;

import java.io.Serializable;

public class ReservaEvento implements Serializable {
    private String id, id_evento, id_cliente, url_cliente, nombre;

    public ReservaEvento() {
        this.id = "";
        this.id_evento = "";
        this.id_cliente = "";
        this.nombre = "";
        this.url_cliente = "";
    }

    public ReservaEvento(String id_evento, String id_cliente) {
        this.id = "";
        this.id_evento = id_evento;
        this.id_cliente = id_cliente;
        this.nombre = "";
        this.url_cliente = "";
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_evento() {
        return id_evento;
    }

    public void setId_evento(String id_evento) {
        this.id_evento = id_evento;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getUrl_cliente() {
        return url_cliente;
    }

    public void setUrl_cliente(String url_cliente) {
        this.url_cliente = url_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
