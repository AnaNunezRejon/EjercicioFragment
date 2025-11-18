package com.example.ejerciciofragment.model;

public class Ticket {

    private int id;
    private String nombreUsuario;
    private String descripcion;
    private String resolucion;
    private Estado estado;

    public Ticket(int id, String nombreUsuario, String descripcion, String resolucion, Estado estado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.descripcion = descripcion;
        this.resolucion = resolucion;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getResolucion() {
        return resolucion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public void setId(int id) {
        this.id = id;
    }
}
