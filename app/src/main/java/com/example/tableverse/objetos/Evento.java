package com.example.tableverse.objetos;

import java.io.Serializable;

public class Evento implements Serializable {
    private String id, nombre, fecha, urlImagen;
    private double precio;
    private int aforoMax, ocupado;

    public Evento() {
        this.id = "";
        this.nombre = "";
        this.fecha = "";
        this.precio = 0.0;
        this.aforoMax = 0;
        this.ocupado = 0;
        this.urlImagen = "";
    }

    public Evento(String nombre, String fecha, double precio, int ocupado) {
        this.id = "";
        this.nombre = nombre;
        this.fecha = fecha;
        this.precio = precio;
        this.aforoMax = 0;
        this.ocupado = ocupado;
        this.urlImagen = "";
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getAforoMax() {
        return aforoMax;
    }

    public void setAforoMax(int aforoMax) {
        this.aforoMax = aforoMax;
    }

    public int getOcupado() {
        return ocupado;
    }

    public void setOcupado(int ocupado) {
        this.ocupado = ocupado;
    }
}


