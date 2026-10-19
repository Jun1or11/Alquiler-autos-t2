package com.example.t2.model;

public class ComprobanteReserva {
    private int idComprobanteReserva;
    private int idReserva;
    private double montoAdelanto;
    private String fechaEmision;

    public ComprobanteReserva() {}

    public ComprobanteReserva(int idComprobanteReserva, int idReserva, double montoAdelanto, String fechaEmision) {
        this.idComprobanteReserva = idComprobanteReserva;
        this.idReserva = idReserva;
        this.montoAdelanto = montoAdelanto;
        this.fechaEmision = fechaEmision;
    }

    public int getIdComprobanteReserva() { return idComprobanteReserva; }
    public void setIdComprobanteReserva(int idComprobanteReserva) { this.idComprobanteReserva = idComprobanteReserva; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public double getMontoAdelanto() { return montoAdelanto; }
    public void setMontoAdelanto(double montoAdelanto) { this.montoAdelanto = montoAdelanto; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }
}
