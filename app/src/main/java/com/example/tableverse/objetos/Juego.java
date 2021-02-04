package com.example.tableverse.objetos;

import java.io.Serializable;

public class Juego implements Serializable {
    //meterle una descripcion
    private String id, nombre, categoria, url_juego;
    private double precio;
    private int stock;
    private boolean disponible;

    //Constructor para Firebase
    public Juego() {
        this.id = "";
        this.nombre = "";
        this.categoria = "";
        this.precio = 0.0;
        this.stock = 0;
        this.disponible = false;
        this.url_juego = "";
    }

    public Juego(String nombre, String categoria, double precio, int stock) {
        this.id = "";
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.disponible = true;
        this.url_juego = "";
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getUrl_juego() {
        return url_juego;
    }

    public void setUrl_juego(String url_juego) {
        this.url_juego = url_juego;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }


}
