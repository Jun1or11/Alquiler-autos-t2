package com.example.t2.view;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t2.R;
import com.example.t2.controller.MantenimientoController;
import com.example.t2.controller.VehiculoController;

import java.util.Calendar;
import java.util.Locale;

public class MantenimientoActivity extends AppCompatActivity {
    private Spinner spVehiculoMantenimiento;
    private Spinner spTipoMantenimiento;
    private EditText etFechaMantenimiento;
    private EditText etCostoMantenimiento;
    private TextView tvResultadoMantenimiento;

    private MantenimientoController mantenimientoController;
    private VehiculoController vehiculoController;

    private final String[] tiposMantenimiento = {"Preventivo", "Correctivo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimiento);

        spVehiculoMantenimiento = findViewById(R.id.spVehiculoMantenimiento);
        spTipoMantenimiento = findViewById(R.id.spTipoMantenimiento);
        etFechaMantenimiento = findViewById(R.id.etFechaMantenimiento);
        etCostoMantenimiento = findViewById(R.id.etCostoMantenimiento);
        Button btnGuardarMantenimiento = findViewById(R.id.btnGuardarMantenimiento);
        tvResultadoMantenimiento = findViewById(R.id.tvResultadoMantenimiento);

        mantenimientoController = new MantenimientoController(this);
        vehiculoController = new VehiculoController(this);
        findViewById(R.id.btnVolverInicio).setOnClickListener(v -> NavegacionInicio.volver(this));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposMantenimiento);
        spTipoMantenimiento.setAdapter(adapter);

        etFechaMantenimiento.setOnClickListener(v -> mostrarDatePicker(etFechaMantenimiento));

        btnGuardarMantenimiento.setOnClickListener(v -> registrarOrden());
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpcionesFormulario.cargar(this, spVehiculoMantenimiento,
                vehiculoController.listarOpcionesVehiculos("Disponible"), "Selecciona un vehículo disponible");
        tvResultadoMantenimiento.setText(construirHistorial());
    }

    private void mostrarDatePicker(EditText target) {
        final Calendar calendar = Calendar.getInstance();
        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String fecha = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    target.setText(fecha);
                }, anio, mes, dia);
        datePickerDialog.show();
    }

    private String construirHistorial() {
        StringBuilder sb = new StringBuilder("ÓRDENES DE MANTENIMIENTO RECIENTES\n\n");
        try (Cursor c = mantenimientoController.listarOrdenesRecientes()) {
            if (!c.moveToFirst()) return sb.append("Todavía no hay órdenes registradas.").toString();
            do {
                sb.append("N° ").append(c.getInt(c.getColumnIndexOrThrow("idOrden")))
                        .append(" • ").append(c.getString(c.getColumnIndexOrThrow("placa")))
                        .append(" • ").append(c.getString(c.getColumnIndexOrThrow("tipo")))
                        .append("\nFecha: ").append(c.getString(c.getColumnIndexOrThrow("fecha")))
                        .append(" • S/ ").append(String.format(Locale.US, "%.2f", c.getDouble(c.getColumnIndexOrThrow("costo"))))
                        .append("\n\n");
            } while (c.moveToNext());
        }
        return sb.toString();
    }

    private void registrarOrden() {
        int idVehiculo = OpcionesFormulario.idSeleccionado(spVehiculoMantenimiento);
        String fecha = etFechaMantenimiento.getText().toString().trim();
        String sCosto = etCostoMantenimiento.getText().toString().trim();
        String tipo = spTipoMantenimiento.getSelectedItem().toString();

        if (idVehiculo <= 0 || fecha.isEmpty() || sCosto.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double costo = Double.parseDouble(sCosto);
            if (costo < 0 || Double.isNaN(costo) || Double.isInfinite(costo)) {
                Toast.makeText(this, "Ingresa un costo válido", Toast.LENGTH_SHORT).show();
                return;
            }

            long idOrden = mantenimientoController.registrarOrdenMantenimiento(idVehiculo, tipo, fecha, costo);

            if (idOrden != -1) {
                Toast.makeText(this, "Mantenimiento registrado con éxito", Toast.LENGTH_SHORT).show();
                tvResultadoMantenimiento.setText("Orden N°: " + idOrden + "\n" +
                        "Vehículo: " + spVehiculoMantenimiento.getSelectedItem() + "\n" +
                        "Tipo: " + tipo + "\n" +
                        "Costo: S/ " + String.format(Locale.US, "%.2f", costo) + "\n" +
                        "Fecha: " + fecha + "\n\n" + construirHistorial());

                OpcionesFormulario.cargar(this, spVehiculoMantenimiento,
                        vehiculoController.listarOpcionesVehiculos("Disponible"), "Selecciona un vehículo disponible");
                etFechaMantenimiento.setText("");
                etCostoMantenimiento.setText("");
            } else {
                Toast.makeText(this, "Error al registrar la orden", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique los valores numéricos", Toast.LENGTH_SHORT).show();
        }
    }
}
