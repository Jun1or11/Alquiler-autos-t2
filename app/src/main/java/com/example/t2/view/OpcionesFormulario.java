package com.example.t2.view;

import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/** Opciones visibles para el usuario que conservan el ID real de SQLite. */
public final class OpcionesFormulario {
    private OpcionesFormulario() {}

    public static final class Opcion {
        public final int id;
        public final String descripcion;

        Opcion(int id, String descripcion) {
            this.id = id;
            this.descripcion = descripcion;
        }

        @Override
        public String toString() {
            return descripcion;
        }
    }

    public static void cargar(Context context, Spinner spinner, Cursor cursor, String indicacion) {
        ArrayList<Opcion> opciones = new ArrayList<>();
        opciones.add(new Opcion(-1, indicacion));
        try (Cursor c = cursor) {
            while (c.moveToNext()) {
                opciones.add(new Opcion(c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("descripcion"))));
            }
        }
        ArrayAdapter<Opcion> adaptador = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, opciones);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adaptador);
    }

    public static int idSeleccionado(Spinner spinner) {
        Opcion opcion = (Opcion) spinner.getSelectedItem();
        return opcion == null ? -1 : opcion.id;
    }

    public static boolean seleccionarId(Spinner spinner, int id) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (((Opcion) spinner.getItemAtPosition(i)).id == id) {
                spinner.setSelection(i);
                return true;
            }
        }
        return false;
    }
}
