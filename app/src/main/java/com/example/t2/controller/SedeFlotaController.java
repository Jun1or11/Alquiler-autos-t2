package com.example.t2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;

public class SedeFlotaController {
    private DatabaseHelper dbHelper;

    public SedeFlotaController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public Cursor listarSedes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT idSede, nombreSede FROM Sede", null);
    }

    public Cursor listarCategorias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT idCategoria, descripcion FROM Categoria", null);
    }

    public Cursor listarFlotas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT f.idFlota, s.nombreSede FROM Flota f INNER JOIN Sede s ON f.idSede = s.idSede", null);
    }

    public long registrarSede(String nombreSede) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nombreSede", nombreSede);
        return db.insert("Sede", null, cv);
    }

    public long registrarCategoria(String descripcion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("descripcion", descripcion);
        return db.insert("Categoria", null, cv);
    }
}
