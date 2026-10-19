package com.example.t2.model;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String clave;
    private int idRol;

    public Usuario() {}

    public Usuario(int idUsuario, String nombre, String correo, int idRol) {
        this(idUsuario, nombre, correo, "123456", idRol);
    }

    public Usuario(int idUsuario, String nombre, String correo, String clave, int idRol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
        this.idRol = idRol;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
}
