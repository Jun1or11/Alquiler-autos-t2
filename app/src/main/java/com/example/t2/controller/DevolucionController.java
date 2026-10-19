package com.example.t2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;

public class DevolucionController {
    private final DatabaseHelper dbHelper;

    public DevolucionController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public Cursor listarInspeccionesRecientes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT h.idInspeccion, h.fecha, h.observaciones, v.placa, " +
                "u.nombre AS inspector FROM HojaInspeccion h " +
                "JOIN Vehiculo v ON v.idVehiculo = h.idVehiculo " +
                "LEFT JOIN Usuario u ON u.idUsuario = h.idInspector " +
                "ORDER BY h.idInspeccion DESC LIMIT 20", null);
    }

    public Cursor listarPenalidadesRecientes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT p.motivo, p.monto, v.placa FROM Penalidad p " +
                "JOIN ContratoAlquiler c ON c.idContrato = p.idContrato " +
                "JOIN Vehiculo v ON v.idVehiculo = c.idVehiculo " +
                "ORDER BY p.idPenalidad DESC LIMIT 20", null);
    }

    public boolean registrarDevolucion(int idVehiculo, int idContrato, String fecha,
                                       String observaciones, String motivoPenalidad, double montoPenalidad) {
        return registrarDevolucion(idVehiculo, idContrato, fecha, observaciones, motivoPenalidad, montoPenalidad, 0);
    }

    public boolean registrarDevolucion(int idVehiculo, int idContrato, String fecha,
                                       String observaciones, String motivoPenalidad, double montoPenalidad, int idInspector) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            try (Cursor c = db.rawQuery("SELECT 1 FROM ContratoAlquiler c JOIN Vehiculo v " +
                    "ON v.idVehiculo = c.idVehiculo WHERE c.idContrato = ? AND c.idVehiculo = ? " +
                    "AND v.estado = 'Alquilado' AND c.idContrato = " +
                    "(SELECT MAX(c2.idContrato) FROM ContratoAlquiler c2 WHERE c2.idVehiculo = v.idVehiculo)",
                    new String[]{String.valueOf(idContrato), String.valueOf(idVehiculo)})) {
                if (!c.moveToFirst()) return false;
            }
            // Registrar Hoja de Inspección Física
            ContentValues valoresInspeccion = new ContentValues();
            valoresInspeccion.put("fecha", fecha);
            valoresInspeccion.put("observaciones", observaciones);
            valoresInspeccion.put("idVehiculo", idVehiculo);
            if (idInspector > 0) {
                valoresInspeccion.put("idInspector", idInspector);
            }
            long idInspeccion = db.insert("HojaInspeccion", null, valoresInspeccion);
            if (idInspeccion == -1) return false;

            // Si hay penalidad aplicable, registrarla
            if (montoPenalidad > 0 && !motivoPenalidad.isEmpty()) {
                ContentValues valoresPenalidad = new ContentValues();
                valoresPenalidad.put("motivo", motivoPenalidad);
                valoresPenalidad.put("monto", montoPenalidad);
                valoresPenalidad.put("idContrato", idContrato);
                if (db.insert("Penalidad", null, valoresPenalidad) == -1) return false;
            }

            // Regresar vehículo al estado Disponible
            ContentValues valoresVehiculo = new ContentValues();
            valoresVehiculo.put("estado", "Disponible");
            if (db.update("Vehiculo", valoresVehiculo, "idVehiculo = ? AND estado = 'Alquilado'",
                    new String[]{String.valueOf(idVehiculo)}) != 1) return false;

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }
}
