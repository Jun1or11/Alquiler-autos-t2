package com.example.t2.model;

public class HojaInspeccion {
    private int idInspeccion;
    private String fecha;
    private String observaciones;
    private int idInspector;
    private int idVehiculo;

    public HojaInspeccion() {}

    public HojaInspeccion(int idInspeccion, String fecha, String observaciones, int idInspector, int idVehiculo) {
        this.idInspeccion = idInspeccion;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.idInspector = idInspector;
        this.idVehiculo = idVehiculo;
    }

    public int getIdInspeccion() { return idInspeccion; }
    public void setIdInspeccion(int idInspeccion) { this.idInspeccion = idInspeccion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getIdInspector() { return idInspector; }
    public void setIdInspector(int idInspector) { this.idInspector = idInspector; }

    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }
}
