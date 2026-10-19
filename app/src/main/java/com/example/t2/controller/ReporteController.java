package com.example.t2.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;

public class ReporteController {
    private final DatabaseHelper dbHelper;

    public ReporteController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public Cursor obtenerFechasConContratos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT MIN(date(fechaInicio)) AS fechaMin, " +
                "MAX(date(fechaInicio)) AS fechaMax FROM ContratoAlquiler", null);
    }

    // Reporte por rango de fechas (montos recaudados y unidades)
    public Cursor obtenerRecaudacionPorRango(String fechaInicio, String fechaFin) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT COUNT(idContrato) AS totalUnidades, " +
                "COALESCE(SUM(montoTotal), 0) AS totalRecaudado " +
                "FROM ContratoAlquiler " +
                "WHERE date(fechaInicio) >= date(?) AND date(fechaInicio) <= date(?)";
        return db.rawQuery(sql, new String[]{fechaInicio, fechaFin});
    }

    // Cliente top (mayor recurrencia)
    public Cursor obtenerClienteTop() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT u.nombre, COUNT(c.idContrato) AS totalContratos, COALESCE(SUM(c.montoTotal), 0) AS totalGasto " +
                "FROM Usuario u " +
                "INNER JOIN ContratoAlquiler c ON u.idUsuario = c.idCliente " +
                "GROUP BY u.idUsuario, u.nombre " +
                "ORDER BY totalContratos DESC, totalGasto DESC LIMIT 1";
        return db.rawQuery(sql, null);
    }

    // Top 10 recepcionistas de ventas
    public Cursor obtenerTop10Recepcionistas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT u.nombre, COUNT(c.idContrato) AS totalVentas, COALESCE(SUM(c.montoTotal), 0) AS montoTotalVentas " +
                "FROM Usuario u " +
                "INNER JOIN ContratoAlquiler c ON u.idUsuario = c.idRecepcionista " +
                "GROUP BY u.idUsuario, u.nombre " +
                "ORDER BY totalVentas DESC LIMIT 10";
        return db.rawQuery(sql, null);
    }

    // Vehículos más usados (alquilados)
    public Cursor obtenerVehiculosMasUsados() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT v.placa, v.marca, v.modelo, COUNT(c.idContrato) AS vecesAlquilado " +
                "FROM Vehiculo v " +
                "INNER JOIN ContratoAlquiler c ON v.idVehiculo = c.idVehiculo " +
                "GROUP BY v.idVehiculo " +
                "ORDER BY vecesAlquilado DESC";
        return db.rawQuery(sql, null);
    }

    // Vehículos con más ordenes de mantenimiento (Preventivo vs Correctivo)
    public Cursor obtenerVehiculosMasMantenimiento() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT v.placa, v.marca, v.modelo, " +
                "COUNT(m.idOrden) AS totalMantenimientos, " +
                "SUM(CASE WHEN m.tipo = 'Preventivo' THEN 1 ELSE 0 END) AS preventivos, " +
                "SUM(CASE WHEN m.tipo = 'Correctivo' THEN 1 ELSE 0 END) AS correctivos " +
                "FROM Vehiculo v " +
                "INNER JOIN OrdenMantenimiento m ON v.idVehiculo = m.idVehiculo " +
                "GROUP BY v.idVehiculo " +
                "ORDER BY totalMantenimientos DESC";
        return db.rawQuery(sql, null);
    }
}
