package com.example.t2.view;

import android.app.Activity;
import android.content.Intent;

import com.example.t2.MainActivity;

/** Regresa al panel de la sesión actual desde cualquier sección. */
public final class NavegacionInicio {
    private NavegacionInicio() {}

    public static void volver(Activity activity) {
        // El registro abierto desde el login debe regresar a la pantalla de acceso.
        if (activity instanceof RegistroUsuarioActivity && !activity.getIntent().hasExtra("ID_USUARIO")) {
            activity.finish();
            return;
        }

        Intent origen = activity.getIntent();
        Intent inicio = new Intent(activity, MainActivity.class);
        inicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        inicio.putExtra("ID_USUARIO", origen.getIntExtra("ID_USUARIO", -1));
        inicio.putExtra("ID_ROL", origen.getIntExtra("ID_ROL", 1));
        inicio.putExtra("NOMBRE_USUARIO", origen.getStringExtra("NOMBRE_USUARIO"));
        activity.startActivity(inicio);
        activity.finish();
    }
}
