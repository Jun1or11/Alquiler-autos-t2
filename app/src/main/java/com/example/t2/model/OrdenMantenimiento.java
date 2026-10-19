package com.example.t2.model;

public class OrdenMantenimiento {
    private int idOrden;
    private String tipo; // Preventivo o Correctivo
    private String fecha;
    private double costo;
    private int idVehiculo;

    public OrdenMantenimiento() {}

    public OrdenMantenimiento(int idOrden, String tipo, String fecha, double costo, int idVehiculo) {
        this.idOrden = idOrden;
        this.tipo = tipo;
        this.fecha = fecha;
        this.costo = costo;
        this.idVehiculo = idVehiculo;
    }

    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
}
