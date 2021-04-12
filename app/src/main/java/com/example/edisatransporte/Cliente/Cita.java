package com.example.edisatransporte.Cliente;

public class Cita {
    public String id;
    public String apellidos;
    public String nombre;
    public String telefono;
    public String correo;
    public String hora;
    public String fechan;
    public String estudios;
    public String direccion;
    public String costos;
    public String total;
    public String status;


    public Cita(String id, String apellidos, String nombre, String telefono, String correo, String hora, String fechan, String estudios, String direccion, String costos,String total,String status) {
        this.id = id;
        this.apellidos = apellidos;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.hora = hora;
        this.fechan = fechan;
        this.estudios = estudios;
        this.direccion = direccion;
        this.costos = costos;
        this.total = total;
        this.status = status;

    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFechan() {
        return fechan;
    }

    public void setFechan(String fechan) {
        this.fechan = fechan;
    }

    public String getEstudios() {
        return estudios;
    }

    public void setEstudios(String estudios) {
        this.estudios = estudios;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCostos() {
        return costos;
    }

    public void setCostos(String costos) {
        this.costos = costos;
    }

}
