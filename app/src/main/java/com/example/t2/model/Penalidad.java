package com.example.t2.model;

public class Penalidad {
    private int idPenalidad;
    private String motivo;
    private double monto;
    private int idContrato;

    public Penalidad() {}

    public Penalidad(int idPenalidad, String motivo, double monto, int idContrato) {
        this.idPenalidad = idPenalidad;
        this.motivo = motivo;
        this.monto = monto;
        this.idContrato = idContrato;
    }

    public int getIdPenalidad() { return idPenalidad; }
    public void setIdPenalidad(int idPenalidad) { this.idPenalidad = idPenalidad; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public int getIdContrato() { return idContrato; }
    public void setIdContrato(int idContrato) { this.idContrato = idContrato; }
}
