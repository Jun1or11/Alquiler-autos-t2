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
import com.example.t2.controller.UsuarioController;

import java.util.Calendar;
import java.util.Locale;

public class AlquilerActivity extends AppCompatActivity {
    private Spinner spCliente;
    private Spinner spRecepcionista;
    private Spinner spVehiculo;
    private Spinner spReserva;
    private EditText etFechaInicioAlquiler;
    private EditText etFechaFinAlquiler;
    private EditText etMontoTotal;
    private TextView tvResultadoAlquiler;

    private AlquilerController alquilerController;
    private UsuarioController usuarioController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alquiler);

        spCliente = findViewById(R.id.spClienteAlquiler);
        spRecepcionista = findViewById(R.id.spRecepcionistaAlquiler);
        spVehiculo = findViewById(R.id.spVehiculoAlquiler);
        spReserva = findViewById(R.id.spReservaAlquiler);
        etFechaInicioAlquiler = findViewById(R.id.etFechaInicioAlquiler);
        etFechaFinAlquiler = findViewById(R.id.etFechaFinAlquiler);
        etMontoTotal = findViewById(R.id.etMontoTotal);
        Button btnGenerarContrato = findViewById(R.id.btnGenerarContrato);
        tvResultadoAlquiler = findViewById(R.id.tvResultadoAlquiler);

        alquilerController = new AlquilerController(this);
        usuarioController = new UsuarioController(this);
        findViewById(R.id.btnVolverInicio).setOnClickListener(v -> NavegacionInicio.volver(this));

        spReserva.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int idReserva = OpcionesFormulario.idSeleccionado(spReserva);
                if (idReserva > 0) {
                    try (Cursor c = alquilerController.obtenerDatosReserva(idReserva)) {
                        if (c.moveToFirst()) {
                            OpcionesFormulario.seleccionarId(spCliente, c.getInt(0));
                            OpcionesFormulario.seleccionarId(spVehiculo, c.getInt(1));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etFechaInicioAlquiler.setOnClickListener(v -> mostrarDatePicker(etFechaInicioAlquiler));
        etFechaFinAlquiler.setOnClickListener(v -> mostrarDatePicker(etFechaFinAlquiler));

        btnGenerarContrato.setOnClickListener(v -> procesarAlquiler());
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpcionesFormulario.cargar(this, spCliente, usuarioController.listarOpcionesPorRol(1), "Selecciona un cliente");
        OpcionesFormulario.cargar(this, spRecepcionista, usuarioController.listarOpcionesPorRol(2), "Selecciona un recepcionista");
        OpcionesFormulario.cargar(this, spVehiculo, alquilerController.listarOpcionesVehiculosAlquiler(), "Selecciona un vehículo");
        OpcionesFormulario.cargar(this, spReserva, alquilerController.listarOpcionesReservas(), "Sin reserva previa");
        tvResultadoAlquiler.setText(construirHistorial());
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
        StringBuilder sb = new StringBuilder("CONTRATOS RECIENTES\n\n");
        try (Cursor c = alquilerController.listarContratosRecientes()) {
            if (!c.moveToFirst()) return sb.append("Todavía no hay contratos registrados.").toString();
            do {
                sb.append("N° ").append(c.getInt(c.getColumnIndexOrThrow("idContrato")))
                        .append(" • ").append(c.getString(c.getColumnIndexOrThrow("placa")))
                        .append("\nCliente: ").append(c.getString(c.getColumnIndexOrThrow("cliente")))
                        .append(" • Venta: ").append(c.getString(c.getColumnIndexOrThrow("recepcionista")))
                        .append("\nDel ").append(c.getString(c.getColumnIndexOrThrow("fechaInicio")))
                        .append(" al ").append(c.getString(c.getColumnIndexOrThrow("fechaFin")))
                        .append(" • S/ ").append(String.format(Locale.US, "%.2f", c.getDouble(c.getColumnIndexOrThrow("montoTotal"))))
                        .append("\n\n");
            } while (c.moveToNext());
        }
        return sb.toString();
    }

    private void procesarAlquiler() {
        int idCliente = OpcionesFormulario.idSeleccionado(spCliente);
        int idRecep = OpcionesFormulario.idSeleccionado(spRecepcionista);
        int idVehiculo = OpcionesFormulario.idSeleccionado(spVehiculo);
        int idReserva = Math.max(0, OpcionesFormulario.idSeleccionado(spReserva));
        String fInicio = etFechaInicioAlquiler.getText().toString().trim();
        String fFin = etFechaFinAlquiler.getText().toString().trim();
        String sMonto = etMontoTotal.getText().toString().trim();

        if (idCliente <= 0 || idRecep <= 0 || idVehiculo <= 0 ||
                fInicio.isEmpty() || fFin.isEmpty() || sMonto.isEmpty()) {
            Toast.makeText(this, "Debe completar todos los datos del contrato", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double monto = Double.parseDouble(sMonto);
            if (monto < 0 || Double.isNaN(monto) || Double.isInfinite(monto) || fFin.compareTo(fInicio) < 0) {
                Toast.makeText(this, "Revisa el monto y las fechas", Toast.LENGTH_SHORT).show();
                return;
            }
            if (idReserva > 0) {
                try (Cursor c = alquilerController.obtenerDatosReserva(idReserva)) {
                    if (!c.moveToFirst() || c.getInt(0) != idCliente || c.getInt(1) != idVehiculo) {
                        Toast.makeText(this, "La reserva debe coincidir con el cliente y el vehículo", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            long idContrato = alquilerController.registrarContratoYComprobante(
                    fInicio, fFin, monto, idCliente, idRecep, idVehiculo, idReserva);

            if (idContrato != -1) {
                Toast.makeText(this, "Contrato y comprobante generados", Toast.LENGTH_SHORT).show();
                tvResultadoAlquiler.setText("--- COMPROBANTE DE PAGO EMITIDO ---\n" +
                        "N° Contrato: " + idContrato + "\n" +
                        (idReserva > 0 ? "Reserva ID Asociada: " + idReserva + "\n" : "") +
                        "Vehículo: " + spVehiculo.getSelectedItem() + "\n" +
                        "Cliente: " + spCliente.getSelectedItem() + "\n" +
                        "Recepcionista: " + spRecepcionista.getSelectedItem() + "\n" +
                        "Monto Cancelado: S/ " + String.format(Locale.US, "%.2f", monto) + "\n" +
                        "Vigencia: " + fInicio + " al " + fFin + "\n\n" + construirHistorial());

                etFechaInicioAlquiler.setText("");
                etFechaFinAlquiler.setText("");
                etMontoTotal.setText("");
                OpcionesFormulario.cargar(this, spVehiculo, alquilerController.listarOpcionesVehiculosAlquiler(), "Selecciona un vehículo");
                OpcionesFormulario.cargar(this, spReserva, alquilerController.listarOpcionesReservas(), "Sin reserva previa");
            } else {
                Toast.makeText(this, "No se pudo registrar el alquiler", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Revise los campos numéricos", Toast.LENGTH_SHORT).show();
        }
    }
}
