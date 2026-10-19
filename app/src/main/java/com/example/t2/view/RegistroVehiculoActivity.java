package com.example.t2.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t2.R;
import com.example.t2.controller.VehiculoController;
import com.example.t2.model.Vehiculo;

public class RegistroVehiculoActivity extends AppCompatActivity {
    private TextView tvTituloRegistro;
    private EditText etPlaca;
    private EditText etMarca;
    private EditText etModelo;
    private TextView tvLabelCategoria;
    private Spinner spCategoriaVehiculo;
    private TextView tvLabelFlota;
    private Spinner spFlotaVehiculo;
    private Button btnGuardarVehiculo;
    private TextView tvResultadoRegistro;
    private TextView tvCatalogoCliente;
    private LinearLayout contenedorCatalogoCliente;

    private VehiculoController vehiculoController;
    private int idRolSesion;
    private int idUsuarioSesion;

    private final String[] categorias = {
            "1 - Económico",
            "2 - SUV",
            "3 - Camioneta 4x4",
            "4 - Lujo"
    };

    private final String[] flotas = {
            "1 - Flota Lima (Sede Central)",
            "2 - Flota Trujillo (Sede Norte)",
            "3 - Flota Arequipa (Sede Sur)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_vehiculo);

        idRolSesion = getIntent().getIntExtra("ID_ROL", 1);
        idUsuarioSesion = getIntent().getIntExtra("ID_USUARIO", -1);

        tvTituloRegistro = findViewById(R.id.tvTituloRegistro);
        etPlaca = findViewById(R.id.etPlaca);
        etMarca = findViewById(R.id.etMarca);
        etModelo = findViewById(R.id.etModelo);
        spCategoriaVehiculo = findViewById(R.id.spCategoriaVehiculo);
        spFlotaVehiculo = findViewById(R.id.spFlotaVehiculo);
        btnGuardarVehiculo = findViewById(R.id.btnGuardarVehiculo);
        tvResultadoRegistro = findViewById(R.id.tvResultadoRegistro);
        tvCatalogoCliente = findViewById(R.id.tvCatalogoCliente);
        contenedorCatalogoCliente = findViewById(R.id.contenedorCatalogoCliente);

        // Referencias a etiquetas de título de spinners
        tvLabelCategoria = findViewById(R.id.tvLabelCategoria);
        tvLabelFlota = findViewById(R.id.tvLabelFlota);

        vehiculoController = new VehiculoController(this);
        findViewById(R.id.btnVolverInicio).setOnClickListener(v -> NavegacionInicio.volver(this));

        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spCategoriaVehiculo.setAdapter(adapterCat);

        ArrayAdapter<String> adapterFlota = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, flotas);
        spFlotaVehiculo.setAdapter(adapterFlota);

        // Modo Vista por Rol (Cliente vs Personal)
        if (idRolSesion == 1) { // ROL CLIENTE: Modo solo lectura de Catálogo
            tvTituloRegistro.setText("Catálogo de Vehículos");
            etPlaca.setVisibility(View.GONE);
            etMarca.setVisibility(View.GONE);
            etModelo.setVisibility(View.GONE);
            if (tvLabelCategoria != null) tvLabelCategoria.setVisibility(View.GONE);
            spCategoriaVehiculo.setVisibility(View.GONE);
            if (tvLabelFlota != null) tvLabelFlota.setVisibility(View.GONE);
            spFlotaVehiculo.setVisibility(View.GONE);
            btnGuardarVehiculo.setVisibility(View.GONE);
            tvResultadoRegistro.setVisibility(View.GONE);
            tvCatalogoCliente.setVisibility(View.VISIBLE);
            contenedorCatalogoCliente.setVisibility(View.VISIBLE);
        } else { // ROL PERSONAL: Modo Registro y Gestión
            tvTituloRegistro.setText("Registro de Vehículo");
            btnGuardarVehiculo.setOnClickListener(v -> guardarDatos());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idRolSesion == 1) {
            mostrarCatalogoCliente();
        } else {
            mostrarListaVehiculos();
        }
    }

    private void mostrarCatalogoCliente() {
        contenedorCatalogoCliente.removeAllViews();
        int espacio = (int) (getResources().getDisplayMetrics().density * 12 + 0.5f);
        boolean hayVehiculos = false;
        try (Cursor c = vehiculoController.listarTodosVehiculos()) {
            while (c.moveToNext()) {
                hayVehiculos = true;
                int id = c.getInt(c.getColumnIndexOrThrow("idVehiculo"));
                String placa = c.getString(c.getColumnIndexOrThrow("placa"));
                String marca = c.getString(c.getColumnIndexOrThrow("marca"));
                String modelo = c.getString(c.getColumnIndexOrThrow("modelo"));
                String estado = c.getString(c.getColumnIndexOrThrow("estado"));
                String categoria = c.getString(c.getColumnIndexOrThrow("categoria"));
                String sede = c.getString(c.getColumnIndexOrThrow("sede"));

                LinearLayout tarjeta = new LinearLayout(this);
                tarjeta.setOrientation(LinearLayout.VERTICAL);
                tarjeta.setBackgroundResource(R.drawable.bg_app_card);
                tarjeta.setPadding(espacio, espacio, espacio, espacio);
                LinearLayout.LayoutParams paramsTarjeta = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsTarjeta.bottomMargin = espacio;
                contenedorCatalogoCliente.addView(tarjeta, paramsTarjeta);

                TextView detalle = new TextView(this);
                detalle.setText(placa + " • " + marca + " " + modelo + "\n" +
                        "Categoría: " + categoria + " | Sede: " + sede + "\nEstado: " + estado);
                detalle.setTextSize(15);
                detalle.setTextColor(getColor(R.color.app_text));
                detalle.setPadding(0, 0, 0, espacio / 2);
                tarjeta.addView(detalle);

                if ("Disponible".equals(estado)) {
                    Button reservar = new Button(this);
                    reservar.setText("Reservar " + placa);
                    reservar.setAllCaps(false);
                    reservar.setBackgroundResource(R.drawable.bg_app_primary);
                    reservar.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.app_primary)));
                    reservar.setTextColor(getColor(R.color.app_on_primary));
                    reservar.setOnClickListener(v -> {
                        if (idUsuarioSesion <= 0) {
                            Toast.makeText(this, "Inicia sesión para reservar", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(this, ReservaActivity.class);
                        intent.putExtra("ID_USUARIO", idUsuarioSesion);
                        intent.putExtra("ID_ROL", idRolSesion);
                        intent.putExtra("NOMBRE_USUARIO", getIntent().getStringExtra("NOMBRE_USUARIO"));
                        intent.putExtra("ID_VEHICULO", id);
                        startActivity(intent);
                    });
                    tarjeta.addView(reservar);
                }
            }
        }
        if (!hayVehiculos) {
            TextView vacio = new TextView(this);
            vacio.setText("Todavía no hay vehículos en el catálogo.");
            vacio.setTextColor(getColor(R.color.app_text));
            vacio.setBackgroundResource(R.drawable.bg_app_card);
            vacio.setPadding(espacio, espacio, espacio, espacio);
            contenedorCatalogoCliente.addView(vacio);
        }
    }

    private void guardarDatos() {
        String placa = etPlaca.getText().toString().trim();
        String marca = etMarca.getText().toString().trim();
        String modelo = etModelo.getText().toString().trim();

        if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int idCategoria = spCategoriaVehiculo.getSelectedItemPosition() + 1;
        int idFlota = spFlotaVehiculo.getSelectedItemPosition() + 1;

        Vehiculo nuevoVehiculo = new Vehiculo(0, placa, marca, modelo, "Disponible", idFlota, idCategoria);
        long resultado = vehiculoController.registrarVehiculo(nuevoVehiculo);

        if (resultado != -1) {
            Toast.makeText(this, "Vehículo registrado exitosamente", Toast.LENGTH_SHORT).show();
            etPlaca.setText("");
            etMarca.setText("");
            etModelo.setText("");
            mostrarListaVehiculos();
        } else {
            Toast.makeText(this, "Error: la placa ya se encuentra registrada", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarListaVehiculos() {
        Cursor c = vehiculoController.listarTodosVehiculos();
        StringBuilder sb = new StringBuilder("VEHÍCULOS REGISTRADOS\n\n");

        if (c != null && c.moveToFirst()) {
            do {
                String placa = c.getString(c.getColumnIndexOrThrow("placa"));
                String marca = c.getString(c.getColumnIndexOrThrow("marca"));
                String modelo = c.getString(c.getColumnIndexOrThrow("modelo"));
                String estado = c.getString(c.getColumnIndexOrThrow("estado"));
                String cat = c.getString(c.getColumnIndexOrThrow("categoria"));
                String sede = c.getString(c.getColumnIndexOrThrow("sede"));

                sb.append("Placa: ").append(placa)
                        .append("\n  Marca/Modelo: ").append(marca).append(" ").append(modelo)
                        .append("\n  Categoría: ").append(cat)
                        .append(" | Sede: ").append(sede)
                        .append("\n  Estado: ").append(estado)
                        .append("\n----------------------------------------\n");
            } while (c.moveToNext());
            c.close();
            tvResultadoRegistro.setText(sb.toString());
        } else {
            tvResultadoRegistro.setText("No hay vehículos registrados en la base de datos.");
        }
    }
}
