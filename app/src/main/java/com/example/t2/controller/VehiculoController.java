package com.example.t2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;
import com.example.t2.model.Vehiculo;

public class VehiculoController {
    private final DatabaseHelper dbHelper;

    public VehiculoController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long registrarVehiculo(Vehiculo vehiculo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("placa", vehiculo.getPlaca());
        valores.put("marca", vehiculo.getMarca());
        valores.put("modelo", vehiculo.getModelo());
        valores.put("estado", vehiculo.getEstado());
        valores.put("idFlota", vehiculo.getIdFlota() > 0 ? vehiculo.getIdFlota() : 1);
        valores.put("idCategoria", vehiculo.getIdCategoria() > 0 ? vehiculo.getIdCategoria() : 1);

        return db.insert("Vehiculo", null, valores);
    }

    public Cursor listarTodosVehiculos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT v.idVehiculo, v.placa, v.marca, v.modelo, v.estado, " +
                "COALESCE(c.descripcion, 'Sin Categoría') AS categoria, " +
                "COALESCE(s.nombreSede, 'Sede Central') AS sede " +
                "FROM Vehiculo v " +
                "LEFT JOIN Categoria c ON v.idCategoria = c.idCategoria " +
                "LEFT JOIN Flota f ON v.idFlota = f.idFlota " +
                "LEFT JOIN Sede s ON f.idSede = s.idSede", null);
    }

    public Cursor listarOpcionesVehiculos(String estado) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String filtro = estado == null ? "" : " WHERE v.estado = ? ";
        return db.rawQuery("SELECT v.idVehiculo AS id, " +
                "v.placa || ' - ' || v.marca || ' ' || v.modelo || ' (' || v.estado || ')' AS descripcion " +
                "FROM Vehiculo v" + filtro + " ORDER BY v.placa",
                estado == null ? null : new String[]{estado});
    }
}
