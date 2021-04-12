package com.example.edisatransporte.Cliente;

public class locali {

    public Double latitud;
    public Double longitud;
    public Float velocidad;

    public locali(Double latitud, Double longitud, Float velocidad) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.velocidad = velocidad;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Float velocidad) {
        this.velocidad = velocidad;
    }

}