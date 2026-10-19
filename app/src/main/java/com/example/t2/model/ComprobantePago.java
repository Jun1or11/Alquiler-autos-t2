package com.example.t2.model;

public class ComprobantePago {
    private int idComprobante;
    private int idContrato;
    private double monto;
    private String fechaPago;

    public ComprobantePago() {}

    public ComprobantePago(int idComprobante, int idContrato, double monto, String fechaPago) {
        this.idComprobante = idComprobante;
        this.idContrato = idContrato;
        this.monto = monto;
        this.fechaPago = fechaPago;
    }

    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }

    public int getIdContrato() { return idContrato; }
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getFechaPago() { return fechaPago; }
    public void setFechaPago(String fechaPago) { this.fechaPago = fechaPago; }
}
