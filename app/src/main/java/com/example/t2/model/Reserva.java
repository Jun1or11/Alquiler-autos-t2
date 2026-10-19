package com.example.t2.model;

public class Reserva {
    private int idReserva;
    private String fechaReserva;
    private double montoAdelanto;
    private String estadoReserva;
    private int idCliente;
    private int idVehiculo;

    public Reserva() {}

    public Reserva(int idReserva, String fechaReserva, double montoAdelanto, String estadoReserva, int idCliente, int idVehiculo) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.montoAdelanto = montoAdelanto;
        this.estadoReserva = estadoReserva;
        this.idCliente = idCliente;
        this.idVehiculo = idVehiculo;
    }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public String getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(String fechaReserva) { this.fechaReserva = fechaReserva; }

    public double getMontoAdelanto() { return montoAdelanto; }
    public void setMontoAdelanto(double montoAdelanto) { this.montoAdelanto = montoAdelanto; }

    public String getEstadoReserva() { return estadoReserva; }
    public void setEstadoReserva(String estadoReserva) { this.estadoReserva = estadoReserva; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
}
