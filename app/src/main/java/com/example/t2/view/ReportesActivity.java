package com.example.t2.view;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t2.R;
import com.example.t2.controller.ReporteController;

import java.util.Calendar;
import java.util.Locale;

public class ReportesActivity extends AppCompatActivity {
    private EditText etFechaInicio;
    private EditText etFechaFin;
    private TextView tvResultadoReporte;

    private ReporteController reporteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        Button btnConsultarRango = findViewById(R.id.btnConsultarRango);
        Button btnClienteTop = findViewById(R.id.btnClienteTop);
        Button btnTop10Recepcionistas = findViewById(R.id.btnTop10Recepcionistas);
        Button btnVehiculosUsoMantenimiento = findViewById(R.id.btnVehiculosUsoMantenimiento);
        tvResultadoReporte = findViewById(R.id.tvResultadoReporte);

        reporteController = new ReporteController(this);
        findViewById(R.id.btnVolverInicio).setOnClickListener(v -> NavegacionInicio.volver(this));

        try (Cursor fechas = reporteController.obtenerFechasConContratos()) {
            if (fechas.moveToFirst() && !fechas.isNull(0)) {
                etFechaInicio.setText(fechas.getString(0));
                etFechaFin.setText(fechas.getString(1));
            }
        }

        etFechaInicio.setOnClickListener(v -> mostrarDatePicker(etFechaInicio));
        etFechaFin.setOnClickListener(v -> mostrarDatePicker(etFechaFin));

        btnConsultarRango.setOnClickListener(v -> consultarRango());
        btnClienteTop.setOnClickListener(v -> consultarClienteTop());
        btnTop10Recepcionistas.setOnClickListener(v -> consultarTopRecepcionistas());
        btnVehiculosUsoMantenimiento.setOnClickListener(v -> consultarVehiculos());
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

    private void consultarRango() {
        String fInicio = etFechaInicio.getText().toString().trim();
        String fFin = etFechaFin.getText().toString().trim();

        if (fInicio.isEmpty() || fFin.isEmpty()) {
            Toast.makeText(this, "Ingrese ambas fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor c = reporteController.obtenerRecaudacionPorRango(fInicio, fFin);
        if (c != null && c.moveToFirst()) {
            int total = c.getInt(c.getColumnIndexOrThrow("totalUnidades"));
            double recaudado = c.getDouble(c.getColumnIndexOrThrow("totalRecaudado"));
            tvResultadoReporte.setText("Resultados del Rango (" + fInicio + " a " + fFin + "):\n" +
                    "• Unidades Alquiladas: " + total + "\n" +
                    "• Monto Recaudado Total: S/ " + String.format(Locale.US, "%.2f", recaudado));
            c.close();
        } else {
            tvResultadoReporte.setText("No se encontraron registros en el rango especificado.");
        }
    }

    private void consultarClienteTop() {
        Cursor c = reporteController.obtenerClienteTop();
        if (c != null && c.moveToFirst()) {
            String nombre = c.getString(c.getColumnIndexOrThrow("nombre"));
            int contratos = c.getInt(c.getColumnIndexOrThrow("totalContratos"));
            double gasto = c.getDouble(c.getColumnIndexOrThrow("totalGasto"));
            tvResultadoReporte.setText("--- CLIENTE MÁS TOP ---\n" +
                    "Nombre: " + nombre + "\n" +
                    "Total de Alquileres Realizados: " + contratos + "\n" +
                    "Monto Total Generado: S/ " + String.format(Locale.US, "%.2f", gasto));
            c.close();
        } else {
            tvResultadoReporte.setText("Sin alquileres registrados para determinar el cliente top.");
        }
    }

    private void consultarTopRecepcionistas() {
        Cursor c = reporteController.obtenerTop10Recepcionistas();
        StringBuilder sb = new StringBuilder("--- TOP 10 RECEPCIONISTAS DE VENTAS ---\n");
        if (c != null && c.moveToFirst()) {
            int posicion = 1;
            do {
                String nombre = c.getString(c.getColumnIndexOrThrow("nombre"));
                int ventas = c.getInt(c.getColumnIndexOrThrow("totalVentas"));
                double monto = c.getDouble(c.getColumnIndexOrThrow("montoTotalVentas"));
                sb.append(posicion).append(". ").append(nombre)
                        .append(" - ").append(ventas).append(" contratos (S/ ")
                        .append(String.format(Locale.US, "%.2f", monto)).append(")\n");
                posicion++;
            } while (c.moveToNext());
            tvResultadoReporte.setText(sb.toString());
            c.close();
        } else {
            tvResultadoReporte.setText("Sin datos de recepcionistas de ventas.");
        }
    }

    private void consultarVehiculos() {
        Cursor cUso = reporteController.obtenerVehiculosMasUsados();
        Cursor cMant = reporteController.obtenerVehiculosMasMantenimiento();
        StringBuilder sb = new StringBuilder();

        sb.append("--- VEHÍCULOS MÁS ALQUILADOS ---\n");
        if (cUso != null && cUso.moveToFirst()) {
            do {
                String placa = cUso.getString(cUso.getColumnIndexOrThrow("placa"));
                String marca = cUso.getString(cUso.getColumnIndexOrThrow("marca"));
                String modelo = cUso.getString(cUso.getColumnIndexOrThrow("modelo"));
                int veces = cUso.getInt(cUso.getColumnIndexOrThrow("vecesAlquilado"));
                sb.append("• Placa: ").append(placa).append(" (").append(marca).append(" ").append(modelo).append(") -> ").append(veces).append(" alquileres\n");
            } while (cUso.moveToNext());
            cUso.close();
        } else {
            sb.append("No hay registros de alquiler de vehículos.\n");
        }

        sb.append("\n--- VEHÍCULOS EN MANTENIMIENTO (PREVENTIVO / CORRECTIVO) ---\n");
        if (cMant != null && cMant.moveToFirst()) {
            do {
                String placa = cMant.getString(cMant.getColumnIndexOrThrow("placa"));
                String marca = cMant.getString(cMant.getColumnIndexOrThrow("marca"));
                String modelo = cMant.getString(cMant.getColumnIndexOrThrow("modelo"));
                int totalM = cMant.getInt(cMant.getColumnIndexOrThrow("totalMantenimientos"));
                int prev = cMant.getInt(cMant.getColumnIndexOrThrow("preventivos"));
                int corr = cMant.getInt(cMant.getColumnIndexOrThrow("correctivos"));
                sb.append("• Placa: ").append(placa).append(" (").append(marca).append(" ").append(modelo).append(")\n")
                        .append("  Total Mantenimientos: ").append(totalM)
                        .append(" (Preventivos: ").append(prev).append(", Correctivos: ").append(corr).append(")\n");
            } while (cMant.moveToNext());
            cMant.close();
        } else {
            sb.append("No hay registros de mantenimientos de vehículos.\n");
        }

        tvResultadoReporte.setText(sb.toString());
    }
}
