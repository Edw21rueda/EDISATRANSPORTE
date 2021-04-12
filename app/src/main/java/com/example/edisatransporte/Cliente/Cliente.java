package com.example.edisatransporte.Cliente;

public class Cliente {
    public String id;
    public String apellidos;
    public String nombre;
    public String telefono;
    public String correo;
    public String genero;
    public String fechan;

    public Cliente(String id, String apellidos, String nombre, String telefono, String correo, String genero, String fechan) {
        this.id = id;
        this.apellidos = apellidos;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.genero = genero;
        this.fechan = fechan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFechan() {
        return fechan;
    }

    public void setFechan(String fechan) {
        this.fechan = fechan;
    }
}
