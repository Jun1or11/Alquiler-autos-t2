package com.example.t2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;

public class AlquilerController {
    private final DatabaseHelper dbHelper;

    public AlquilerController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public Cursor listarVehiculosDisponibles() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT idVehiculo, placa, marca, modelo FROM Vehiculo WHERE estado = 'Disponible' OR estado = 'Reservado'", null);
    }

    public Cursor listarOpcionesVehiculosAlquiler() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT idVehiculo AS id, placa || ' - ' || marca || ' ' || modelo || " +
                "' (' || estado || ')' AS descripcion FROM Vehiculo " +
                "WHERE estado IN ('Disponible', 'Reservado') ORDER BY placa", null);
    }

    public Cursor listarOpcionesReservas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT r.idReserva AS id, 'Reserva N° ' || r.idReserva || " +
                "' - ' || u.nombre || ' / ' || v.placa AS descripcion " +
                "FROM Reserva r INNER JOIN Usuario u ON u.idUsuario = r.idCliente " +
                "INNER JOIN Vehiculo v ON v.idVehiculo = r.idVehiculo " +
                "WHERE r.estadoReserva = 'Confirmada' AND v.estado = 'Reservado' " +
                "ORDER BY r.idReserva DESC", null);
    }

    public Cursor obtenerDatosReserva(int idReserva) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT idCliente, idVehiculo FROM Reserva WHERE idReserva = ? " +
                "AND estadoReserva = 'Confirmada'", new String[]{String.valueOf(idReserva)});
    }

    public Cursor listarOpcionesContratosEnAlquiler() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT c.idContrato AS id, 'Contrato N° ' || c.idContrato || " +
                "' - ' || v.placa || ' / ' || u.nombre AS descripcion " +
                "FROM ContratoAlquiler c INNER JOIN Vehiculo v ON v.idVehiculo = c.idVehiculo " +
                "INNER JOIN Usuario u ON u.idUsuario = c.idCliente " +
                "WHERE v.estado = 'Alquilado' AND c.idContrato = " +
                "(SELECT MAX(c2.idContrato) FROM ContratoAlquiler c2 WHERE c2.idVehiculo = v.idVehiculo) " +
                "ORDER BY c.idContrato DESC", null);
    }

    public Cursor obtenerVehiculoContrato(int idContrato) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT v.idVehiculo, v.placa || ' - ' || v.marca || ' ' || v.modelo AS descripcion " +
                        "FROM ContratoAlquiler c JOIN Vehiculo v ON v.idVehiculo = c.idVehiculo " +
                        "WHERE c.idContrato = ? AND v.estado = 'Alquilado'",
                new String[]{String.valueOf(idContrato)});
    }

    public Cursor listarContratosPorCliente(int idCliente) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT c.idContrato, c.fechaInicio, c.fechaFin, c.montoTotal, " +
                "v.placa, v.marca, v.modelo, cp.idComprobante " +
                "FROM ContratoAlquiler c " +
                "INNER JOIN Vehiculo v ON c.idVehiculo = v.idVehiculo " +
                "LEFT JOIN ComprobantePago cp ON c.idContrato = cp.idContrato " +
                "WHERE c.idCliente = ? " +
                "ORDER BY c.idContrato DESC", new String[]{String.valueOf(idCliente)});
    }

    public long registrarContratoYComprobante(String fechaInicio, String fechaFin, double montoTotal,
                                              int idCliente, int idRecepcionista, int idVehiculo) {
        return registrarContratoYComprobante(fechaInicio, fechaFin, montoTotal, idCliente, idRecepcionista, idVehiculo, 0);
    }

    public long registrarContratoYComprobante(String fechaInicio, String fechaFin, double montoTotal,
                                               int idCliente, int idRecepcionista, int idVehiculo, int idReserva) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            if (idReserva > 0) {
                try (Cursor c = db.rawQuery("SELECT 1 FROM Reserva WHERE idReserva = ? AND idCliente = ? " +
                        "AND idVehiculo = ? AND estadoReserva = 'Confirmada'", new String[]{
                        String.valueOf(idReserva), String.valueOf(idCliente), String.valueOf(idVehiculo)})) {
                    if (!c.moveToFirst()) return -1;
                }
            }
            ContentValues valoresVehiculo = new ContentValues();
            valoresVehiculo.put("estado", "Alquilado");
            String estadoEsperado = idReserva > 0 ? "Reservado" : "Disponible";
            if (db.update("Vehiculo", valoresVehiculo, "idVehiculo = ? AND estado = ?",
                    new String[]{String.valueOf(idVehiculo), estadoEsperado}) != 1) return -1;

            // Insertar Contrato de Alquiler
            ContentValues valoresContrato = new ContentValues();
            valoresContrato.put("fechaInicio", fechaInicio);
            valoresContrato.put("fechaFin", fechaFin);
            valoresContrato.put("montoTotal", montoTotal);
            valoresContrato.put("idCliente", idCliente);
            valoresContrato.put("idRecepcionista", idRecepcionista);
            valoresContrato.put("idVehiculo", idVehiculo);
            if (idReserva > 0) {
                valoresContrato.put("idReserva", idReserva);
            }

            long idContrato = db.insert("ContratoAlquiler", null, valoresContrato);

            if (idContrato != -1) {
                // Insertar Comprobante de Pago asociado
                ContentValues valoresPago = new ContentValues();
                valoresPago.put("idContrato", idContrato);
                valoresPago.put("monto", montoTotal);
                valoresPago.put("fechaPago", fechaInicio);
                if (db.insert("ComprobantePago", null, valoresPago) == -1) return -1;

                // Si fue generado desde una reserva, actualizar estado de la reserva
                if (idReserva > 0) {
                    ContentValues valoresReserva = new ContentValues();
                    valoresReserva.put("estadoReserva", "Atendida");
                    if (db.update("Reserva", valoresReserva, "idReserva = ? AND estadoReserva = 'Confirmada'",
                            new String[]{String.valueOf(idReserva)}) != 1) return -1;
                }

                db.setTransactionSuccessful();
            }
            return idContrato;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor listarContratosRecientes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT c.idContrato, c.fechaInicio, c.fechaFin, c.montoTotal, " +
                "cliente.nombre AS cliente, recep.nombre AS recepcionista, v.placa " +
                "FROM ContratoAlquiler c " +
                "JOIN Usuario cliente ON cliente.idUsuario = c.idCliente " +
                "LEFT JOIN Usuario recep ON recep.idUsuario = c.idRecepcionista " +
                "JOIN Vehiculo v ON v.idVehiculo = c.idVehiculo " +
                "ORDER BY c.idContrato DESC LIMIT 20", null);
    }
}
