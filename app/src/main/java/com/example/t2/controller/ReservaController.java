package com.example.t2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;
import com.example.t2.model.Reserva;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReservaController {
    private final DatabaseHelper dbHelper;

    public ReservaController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long registrarReserva(Reserva reserva) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // La disponibilidad se comprueba y se actualiza dentro de la misma transacción.
            ContentValues vVeh = new ContentValues();
            vVeh.put("estado", "Reservado");
            int actualizados = db.update("Vehiculo", vVeh,
                    "idVehiculo = ? AND estado = 'Disponible'",
                    new String[]{String.valueOf(reserva.getIdVehiculo())});
            if (actualizados != 1) {
                return -1;
            }

            ContentValues valores = new ContentValues();
            valores.put("fechaReserva", reserva.getFechaReserva());
            valores.put("montoAdelanto", reserva.getMontoAdelanto());
            valores.put("estadoReserva", "Confirmada");
            valores.put("idCliente", reserva.getIdCliente());
            valores.put("idVehiculo", reserva.getIdVehiculo());

            long idReserva = db.insert("Reserva", null, valores);
            if (idReserva == -1) {
                return -1;
            }

            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            ContentValues vComp = new ContentValues();
            vComp.put("idReserva", idReserva);
            vComp.put("montoAdelanto", reserva.getMontoAdelanto());
            vComp.put("fechaEmision", fechaHoy);
            if (db.insert("ComprobanteReserva", null, vComp) == -1) {
                return -1;
            }

            ContentValues vConst = new ContentValues();
            vConst.put("idReserva", idReserva);
            vConst.put("codigoConstancia", "CONST-RES-" + idReserva + "-" + (System.currentTimeMillis() % 10000));
            vConst.put("fechaEmision", fechaHoy);
            if (db.insert("ConstanciaReserva", null, vConst) == -1) {
                return -1;
            }

            db.setTransactionSuccessful();
            return idReserva;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor listarReservasActivas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT r.idReserva, r.fechaReserva, r.montoAdelanto, r.estadoReserva, u.nombre AS cliente, v.placa, v.marca, v.modelo " +
                "FROM Reserva r " +
                "INNER JOIN Usuario u ON r.idCliente = u.idUsuario " +
                "INNER JOIN Vehiculo v ON r.idVehiculo = v.idVehiculo " +
                "ORDER BY r.idReserva DESC", null);
    }

    public Cursor listarReservasPorCliente(int idCliente) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT r.idReserva, r.fechaReserva, r.montoAdelanto, r.estadoReserva, " +
                "v.placa, v.marca, v.modelo, cr.codigoConstancia " +
                "FROM Reserva r " +
                "INNER JOIN Vehiculo v ON r.idVehiculo = v.idVehiculo " +
                "LEFT JOIN ConstanciaReserva cr ON r.idReserva = cr.idReserva " +
                "WHERE r.idCliente = ? " +
                "ORDER BY r.idReserva DESC", new String[]{String.valueOf(idCliente)});
    }
}
