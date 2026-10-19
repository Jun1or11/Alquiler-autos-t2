package com.example.t2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;
import com.example.t2.model.PolizaSeguro;

public class PolizaSeguroController {
    private DatabaseHelper dbHelper;

    public PolizaSeguroController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long registrarPoliza(PolizaSeguro poliza) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("numeroPoliza", poliza.getNumeroPoliza());
        valores.put("cobertura", poliza.getCobertura());
        valores.put("montoCobertura", poliza.getMontoCobertura());
        valores.put("fechaVencimiento", poliza.getFechaVencimiento());
        valores.put("idVehiculo", poliza.getIdVehiculo());

        return db.insert("PolizaSeguro", null, valores);
    }

    public Cursor listarPolizasPorVehiculo(int idVehiculo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT idPoliza, numeroPoliza, cobertura, montoCobertura, fechaVencimiento " +
                "FROM PolizaSeguro WHERE idVehiculo = ?", new String[]{String.valueOf(idVehiculo)});
    }
}
