package com.example.t2.model;

public class Flota {
    private int idFlota;
    private int idSede;

    public Flota() {}

    public Flota(int idFlota, int idSede) {
        this.idFlota = idFlota;
        this.idSede = idSede;
    }

    public int getIdFlota() { return idFlota; }
    public void setIdFlota(int idFlota) { this.idFlota = idFlota; }

    public int getIdSede() { return idSede; }
    public void setIdSede(int idSede) { this.idSede = idSede; }
}
