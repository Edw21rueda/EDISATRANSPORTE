package com.example.edisatransporte.Cliente;

public class Estudios {
    private long precio;
    private String descripcion;

    public Estudios(long precio, String descripcion) {
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public long getPrecio() {
        return precio;
    }

    public void setPrecio(long precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Estudios(){
    }
    public String toString(){
        return this. precio+"";
    }
}
