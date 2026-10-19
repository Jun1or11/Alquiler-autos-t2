package com.example.t2.view;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t2.R;
import com.example.t2.controller.AlquilerController;
import com.example.t2.controller.DevolucionController;
import com.example.t2.controller.UsuarioController;

import java.util.Calendar;
import java.util.Locale;

public class DevolucionActivity extends AppCompatActivity {
    private Spinner spInspectorDevolucion;
    private Spinner spContratoDevolucion;
    private TextView tvVehiculoDevolucion;
    private EditText etFechaDevolucion;
    private EditText etObservacionesInspeccion;
    private EditText etMotivoPenalidad;
    private EditText etMontoPenalidad;
    private TextView tvResultadoDevolucion;

    private DevolucionController devolucionController;
    private AlquilerController alquilerController;
    private UsuarioController usuarioController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devolucion);

        spInspectorDevolucion = findViewById(R.id.spInspectorDevolucion);
        spContratoDevolucion = findViewById(R.id.spContratoDevolucion);
        tvVehiculoDevolucion = findViewById(R.id.tvVehiculoDevolucion);
        etFechaDevolucion = findViewById(R.id.etFechaDevolucion);
        etObservacionesInspeccion = findViewById(R.id.etObservacionesInspeccion);
        etMotivoPenalidad = findViewById(R.id.etMotivoPenalidad);
        etMontoPenalidad = findViewById(R.id.etMontoPenalidad);
        Button btnFinalizarDevolucion = findViewById(R.id.btnFinalizarDevolucion);
        tvResultadoDevolucion = findViewById(R.id.tvResultadoDevolucion);

        devolucionController = new DevolucionController(this);
        alquilerController = new AlquilerController(this);
        usuarioController = new UsuarioController(this);
        findViewById(R.id.btnVolverInicio).setOnClickListener(v -> NavegacionInicio.volver(this));

        spContratoDevolucion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int contrato = OpcionesFormulario.idSeleccionado(spContratoDevolucion);
                tvVehiculoDevolucion.setText("El vehículo se completa al elegir el contrato.");
                if (contrato > 0) {
                    try (Cursor c = alquilerController.obtenerVehiculoContrato(contrato)) {
                        if (c.moveToFirst()) tvVehiculoDevolucion.setText("Vehículo: " + c.getString(1));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etFechaDevolucion.setOnClickListener(v -> mostrarDatePicker(etFechaDevolucion));

        btnFinalizarDevolucion.setOnClickListener(v -> finalizarDevolucion());
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpcionesFormulario.cargar(this, spInspectorDevolucion,
                usuarioController.listarOpcionesPorRol(3), "Selecciona un inspector");
        OpcionesFormulario.cargar(this, spContratoDevolucion,
                alquilerController.listarOpcionesContratosEnAlquiler(), "Selecciona un contrato en alquiler");
        tvResultadoDevolucion.setText(construirHistorial());
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
        StringBuilder sb = new StringBuilder("INSPECCIONES RECIENTES\n\n");
        try (Cursor c = devolucionController.listarInspeccionesRecientes()) {
            if (!c.moveToFirst()) {
                sb.append("Todavía no hay inspecciones registradas.\n");
            } else {
                do {
                    sb.append("N° ").append(c.getInt(c.getColumnIndexOrThrow("idInspeccion")))
                            .append(" • ").append(c.getString(c.getColumnIndexOrThrow("placa")))
                            .append(" • ").append(c.getString(c.getColumnIndexOrThrow("fecha")))
                            .append("\nInspector: ").append(c.getString(c.getColumnIndexOrThrow("inspector")))
                            .append("\n").append(c.getString(c.getColumnIndexOrThrow("observaciones")))
                            .append("\n\n");
                } while (c.moveToNext());
            }
        }
        sb.append("\nPENALIDADES RECIENTES\n\n");
        try (Cursor c = devolucionController.listarPenalidadesRecientes()) {
            if (!c.moveToFirst()) {
                sb.append("Sin penalidades registradas.");
            } else {
                do {
                    sb.append(c.getString(c.getColumnIndexOrThrow("placa")))
                            .append(" • ").append(c.getString(c.getColumnIndexOrThrow("motivo")))
                            .append(" • S/ ").append(String.format(Locale.US, "%.2f", c.getDouble(c.getColumnIndexOrThrow("monto"))))
                            .append("\n");
                } while (c.moveToNext());
            }
        }
        return sb.toString();
    }

    private void finalizarDevolucion() {
        int idInspector = OpcionesFormulario.idSeleccionado(spInspectorDevolucion);
        int idContrato = OpcionesFormulario.idSeleccionado(spContratoDevolucion);
        String fecha = etFechaDevolucion.getText().toString().trim();
        String obs = etObservacionesInspeccion.getText().toString().trim();
        String motivoPenalidad = etMotivoPenalidad.getText().toString().trim();
        String sPenalidad = etMontoPenalidad.getText().toString().trim();

        if (idInspector <= 0 || idContrato <= 0 || fecha.isEmpty() || obs.isEmpty()) {
            Toast.makeText(this, "Complete los datos principales de inspección", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int idVehiculo;
            try (Cursor c = alquilerController.obtenerVehiculoContrato(idContrato)) {
                if (!c.moveToFirst()) {
                    Toast.makeText(this, "El contrato ya no está en alquiler", Toast.LENGTH_SHORT).show();
                    return;
                }
                idVehiculo = c.getInt(0);
            }
            double penalidad = sPenalidad.isEmpty() ? 0.0 : Double.parseDouble(sPenalidad);
            if (penalidad < 0 || Double.isNaN(penalidad) || Double.isInfinite(penalidad)) {
                Toast.makeText(this, "Ingresa una penalidad válida", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean exito = devolucionController.registrarDevolucion(
                    idVehiculo, idContrato, fecha, obs, motivoPenalidad, penalidad, idInspector);

            if (exito) {
                Toast.makeText(this, "Vehículo devuelto e inspeccionado", Toast.LENGTH_SHORT).show();
                tvResultadoDevolucion.setText("--- HOJA DE INSPECCIÓN REGISTRADA ---\n" +
                        tvVehiculoDevolucion.getText() + " • Disponible\n" +
                        "Inspector: " + spInspectorDevolucion.getSelectedItem() + "\n" +
                        "Observaciones: " + obs + "\n" +
                        (penalidad > 0 ? "Penalidad Aplicada: S/ " + penalidad + " (" + motivoPenalidad + ")" : "Sin penalidad registrada.") +
                        "\n\n" + construirHistorial());

                OpcionesFormulario.cargar(this, spContratoDevolucion,
                        alquilerController.listarOpcionesContratosEnAlquiler(), "Selecciona un contrato en alquiler");
                etFechaDevolucion.setText("");
                etObservacionesInspeccion.setText("");
                etMotivoPenalidad.setText("");
                etMontoPenalidad.setText("");
            } else {
                Toast.makeText(this, "Error al registrar la devolución", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error de formato numérico", Toast.LENGTH_SHORT).show();
        }
    }
}
