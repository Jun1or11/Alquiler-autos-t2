package com.example.t2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.t2.database.DatabaseHelper;
import com.example.t2.model.Usuario;

public class UsuarioController {
    private final DatabaseHelper dbHelper;

    public UsuarioController(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public long registrarUsuario(Usuario usuario) {
        if (usuario.getClave() == null || usuario.getClave().isEmpty()) return -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", usuario.getNombre());
        valores.put("correo", usuario.getCorreo());
        valores.put("clave", usuario.getClave());
        valores.put("idRol", usuario.getIdRol());
        return db.insert("Usuario", null, valores);
    }

    public Usuario autenticarUsuario(String correo, String clave) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT idUsuario, nombre, correo, clave, idRol FROM Usuario WHERE correo = ? AND clave = ?",
                new String[]{correo, clave});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("idUsuario"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("correo"));
                String pass = cursor.getString(cursor.getColumnIndexOrThrow("clave"));
                int idRol = cursor.getInt(cursor.getColumnIndexOrThrow("idRol"));
                cursor.close();
                return new Usuario(id, nombre, email, pass, idRol);
            }
            cursor.close();
        }
        return null;
    }

    public Cursor listarUsuariosPorRol(int idRol) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT idUsuario, nombre, correo FROM Usuario WHERE idRol = ?", new String[]{String.valueOf(idRol)});
    }

    public Cursor listarOpcionesPorRol(int idRol) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT idUsuario AS id, nombre || ' (' || correo || ')' AS descripcion " +
                "FROM Usuario WHERE idRol = ? " +
                (idRol == 2 ? "AND correo <> 'admin@renta.com' " : "") +
                "ORDER BY nombre", new String[]{String.valueOf(idRol)});
    }

    public Cursor listarTodosUsuarios() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT u.idUsuario, u.nombre, u.correo, r.nombreRol " +
                "FROM Usuario u LEFT JOIN Rol r ON u.idRol = r.idRol " +
                "ORDER BY u.idUsuario ASC", null);
    }
}
