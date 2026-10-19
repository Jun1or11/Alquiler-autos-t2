package com.example.t2.model;

public class Vehiculo {
    private int idVehiculo;
    private String placa;
    private String marca;
    private String modelo;
    private String estado;
    private int idFlota;
    private int idCategoria;

    public Vehiculo() {
    }

    public Vehiculo(int idVehiculo, String placa, String marca, String modelo, String estado) {
        this(idVehiculo, placa, marca, modelo, estado, 1, 1);
    }

    public Vehiculo(int idVehiculo, String placa, String marca, String modelo, String estado, int idFlota, int idCategoria) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.estado = estado;
        this.idFlota = idFlota;
        this.idCategoria = idCategoria;
    }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdFlota() { return idFlota; }
    public void setIdFlota(int idFlota) { this.idFlota = idFlota; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
}
