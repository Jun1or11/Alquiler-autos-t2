package com.example.t2.model;

public class PolizaSeguro {
    private int idPoliza;
    private String numeroPoliza;
    private String cobertura; // Pérdida o robo, Accidente, Siniestro
    private double montoCobertura;
    private String fechaVencimiento;
    private int idVehiculo;

    public PolizaSeguro() {}

    public PolizaSeguro(int idPoliza, String numeroPoliza, String cobertura, double montoCobertura, String fechaVencimiento, int idVehiculo) {
        this.idPoliza = idPoliza;
        this.numeroPoliza = numeroPoliza;
        this.cobertura = cobertura;
        this.montoCobertura = montoCobertura;
        this.fechaVencimiento = fechaVencimiento;
        this.idVehiculo = idVehiculo;
    }

    public int getIdPoliza() { return idPoliza; }
    public void setIdPoliza(int idPoliza) { this.idPoliza = idPoliza; }

    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }

    public String getCobertura() { return cobertura; }
    public void setCobertura(String cobertura) { this.cobertura = cobertura; }

    public double getMontoCobertura() { return montoCobertura; }
    public void setMontoCobertura(double montoCobertura) { this.montoCobertura = montoCobertura; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
}
