package com.example.edisatransporte.Cliente;

public class Producto {
    public String id;
    public String nombre;
    public String descripcion;
    public double precio;

    public Producto(){

    }

    public Producto(String id,String descripcion,String nombre,double precio){
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }
}
