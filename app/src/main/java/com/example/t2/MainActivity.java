package com.example.t2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t2.view.AlquilerActivity;
import com.example.t2.view.DevolucionActivity;
import com.example.t2.view.LoginActivity;
import com.example.t2.view.MantenimientoActivity;
import com.example.t2.view.PolizaSeguroActivity;
import com.example.t2.view.RegistroUsuarioActivity;
import com.example.t2.view.RegistroVehiculoActivity;
import com.example.t2.view.ReportesActivity;
import com.example.t2.view.ReservaActivity;

public class MainActivity extends AppCompatActivity {
    private int idUsuarioSesion;
    private int idRolSesion;
    private String nombreUsuarioSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        TextView tvBienvenidoUsuario = findViewById(R.id.tvBienvenidoUsuario);
        Button btnIrRegistroVehiculo = findViewById(R.id.btnIrRegistroVehiculo);
        Button btnIrRegistroUsuario = findViewById(R.id.btnIrRegistroUsuario);
        Button btnIrReserva = findViewById(R.id.btnIrReserva);
        Button btnIrAlquiler = findViewById(R.id.btnIrAlquiler);
        Button btnIrMantenimiento = findViewById(R.id.btnIrMantenimiento);
        Button btnIrDevolucion = findViewById(R.id.btnIrDevolucion);
        Button btnIrPoliza = findViewById(R.id.btnIrPoliza);
        Button btnIrReportes = findViewById(R.id.btnIrReportes);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        idUsuarioSesion = getIntent().getIntExtra("ID_USUARIO", 1);
        idRolSesion = getIntent().getIntExtra("ID_ROL", 2);
        String extraNombre = getIntent().getStringExtra("NOMBRE_USUARIO");
        String nombreUsuario = (extraNombre != null && !extraNombre.isEmpty()) ? extraNombre : "Usuario Administrador";
        nombreUsuarioSesion = nombreUsuario;

        String rolTexto;
        switch (idRolSesion) {
            case 1:
                rolTexto = "Cliente";
                break;
            case 2:
                rolTexto = "Recepcionista de Ventas / Admin";
                break;
            case 3:
                rolTexto = "Inspector de Flota";
                break;
            case 4:
                rolTexto = "Recepcionista de Devolución";
                break;
            default:
                rolTexto = "Personal";
                break;
        }

        tvBienvenidoUsuario.setText("Sesión Activa: " + nombreUsuario + " (" + rolTexto + ")");

        // Configuración de accesos dinámicos por Rol
        if (idRolSesion == 1) { // ROL CLIENTE
            btnIrRegistroUsuario.setVisibility(View.GONE);
            btnIrAlquiler.setVisibility(View.GONE);
            btnIrMantenimiento.setVisibility(View.GONE);
            btnIrDevolucion.setVisibility(View.GONE);
            btnIrReportes.setVisibility(View.GONE);
            btnIrRegistroVehiculo.setText("Explorar Vehículos y Reservar");
            btnIrReserva.setText("Ver Mis Reservas");
            btnIrPoliza.setVisibility(View.GONE);
        } else { // ROL ADMINISTRACIÓN / PERSONAL
            btnIrRegistroUsuario.setVisibility(View.VISIBLE);
            btnIrAlquiler.setVisibility(View.VISIBLE);
            btnIrMantenimiento.setVisibility(View.VISIBLE);
            btnIrDevolucion.setVisibility(View.VISIBLE);
            btnIrReportes.setVisibility(View.VISIBLE);
            btnIrPoliza.setVisibility(View.VISIBLE);
            btnIrRegistroVehiculo.setText("Vehículos: Registrar y Ver Flota");
            btnIrRegistroUsuario.setText("Usuarios y Roles");
            btnIrReserva.setText("Reservas");
            btnIrAlquiler.setText("Alquileres");
            btnIrMantenimiento.setText("Mantenimiento");
            btnIrDevolucion.setText("Devoluciones e Inspecciones");
            btnIrPoliza.setText("Seguros");
            btnIrReportes.setText("Reportes");
        }

        btnIrRegistroVehiculo.setOnClickListener(v -> abrirPantalla(RegistroVehiculoActivity.class));
        btnIrRegistroUsuario.setOnClickListener(v -> abrirPantalla(RegistroUsuarioActivity.class));
        btnIrReserva.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReservaActivity.class);
            intent.putExtra("ID_USUARIO", idUsuarioSesion);
            intent.putExtra("ID_ROL", idRolSesion);
            intent.putExtra("NOMBRE_USUARIO", nombreUsuarioSesion);
            if (idRolSesion == 1) {
                intent.putExtra("SOLO_MIS_RESERVAS", true);
            }
            startActivity(intent);
        });
        btnIrAlquiler.setOnClickListener(v -> abrirPantalla(AlquilerActivity.class));
        btnIrMantenimiento.setOnClickListener(v -> abrirPantalla(MantenimientoActivity.class));
        btnIrDevolucion.setOnClickListener(v -> abrirPantalla(DevolucionActivity.class));
        btnIrPoliza.setOnClickListener(v -> abrirPantalla(PolizaSeguroActivity.class));
        btnIrReportes.setOnClickListener(v -> abrirPantalla(ReportesActivity.class));

        btnCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void abrirPantalla(Class<?> destino) {
        Intent intent = new Intent(MainActivity.this, destino);
        intent.putExtra("ID_USUARIO", idUsuarioSesion);
        intent.putExtra("ID_ROL", idRolSesion);
        intent.putExtra("NOMBRE_USUARIO", nombreUsuarioSesion);
        startActivity(intent);
    }
}
