package com.example.t2.model;

public class ContratoAlquiler {
    private int idContrato;
    private String fechaInicio;
    private String fechaFin;
    private double montoTotal;
    private int idCliente;
    private int idRecepcionista;
    private int idVehiculo;
    private int idReserva;

    public ContratoAlquiler() {}

    public ContratoAlquiler(int idContrato, String fechaInicio, String fechaFin, double montoTotal, int idCliente, int idRecepcionista, int idVehiculo, int idReserva) {
        this.idContrato = idContrato;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.montoTotal = montoTotal;
        this.idCliente = idCliente;
        this.idRecepcionista = idRecepcionista;
        this.idVehiculo = idVehiculo;
        this.idReserva = idReserva;
    }

    public int getIdContrato() { return idContrato; }
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdRecepcionista() { return idRecepcionista; }
    public void setIdRecepcionista(int idRecepcionista) { this.idRecepcionista = idRecepcionista; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
}
