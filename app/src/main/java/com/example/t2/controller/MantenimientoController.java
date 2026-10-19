package com.example.t2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;

public class MantenimientoController {
    private DatabaseHelper dbHelper;

    public MantenimientoController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public Cursor listarOrdenesRecientes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT m.idOrden, v.placa, m.tipo, m.fecha, m.costo " +
                "FROM OrdenMantenimiento m JOIN Vehiculo v ON v.idVehiculo = m.idVehiculo " +
                "ORDER BY m.idOrden DESC LIMIT 20", null);
    }

    public long registrarOrdenMantenimiento(int idVehiculo, String tipo, String fecha, double costo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        long idOrden = -1;
        try {
            // Insertar orden de mantenimiento
            ContentValues valores = new ContentValues();
            valores.put("idVehiculo", idVehiculo);
            valores.put("tipo", tipo); // "Preventivo" o "Correctivo"
            valores.put("fecha", fecha);
            valores.put("costo", costo);

            idOrden = db.insert("OrdenMantenimiento", null, valores);

            if (idOrden != -1) {
                // Actualizar estado del vehículo a Mantenimiento
                ContentValues valoresVehiculo = new ContentValues();
                valoresVehiculo.put("estado", "Mantenimiento");
                db.update("Vehiculo", valoresVehiculo, "idVehiculo = ?", new String[]{String.valueOf(idVehiculo)});
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
        return idOrden;
    }
}
