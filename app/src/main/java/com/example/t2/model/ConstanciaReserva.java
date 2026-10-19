package com.example.t2.model;

public class ConstanciaReserva {
    private int idConstancia;
    private int idReserva;
    private String codigoConstancia;
    private String fechaEmision;

    public ConstanciaReserva() {}

    public ConstanciaReserva(int idConstancia, int idReserva, String codigoConstancia, String fechaEmision) {
        this.idConstancia = idConstancia;
        this.idReserva = idReserva;
        this.codigoConstancia = codigoConstancia;
        this.fechaEmision = fechaEmision;
    }

    public int getIdConstancia() { return idConstancia; }
    public void setIdConstancia(int idConstancia) { this.idConstancia = idConstancia; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public String getCodigoConstancia() { return codigoConstancia; }
    public void setCodigoConstancia(String codigoConstancia) { this.codigoConstancia = codigoConstancia; }

    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }
}
