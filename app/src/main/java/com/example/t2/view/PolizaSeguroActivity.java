package com.example.t2.view;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t2.R;
import com.example.t2.controller.PolizaSeguroController;
import com.example.t2.controller.VehiculoController;
import com.example.t2.model.PolizaSeguro;

import java.util.Calendar;
import java.util.Locale;

public class PolizaSeguroActivity extends AppCompatActivity {
    private TextView tvTituloPoliza;
    private Spinner spVehiculoPoliza;
    private EditText etNumeroPoliza;
    private TextView tvLabelCobertura;
    private Spinner spCoberturaPoliza;
    private EditText etMontoCobertura;
    private EditText etFechaVencimientoPoliza;
    private Button btnGuardarPoliza;
    private TextView tvResultadoPoliza;

    private PolizaSeguroController polizaSeguroController;
    private VehiculoController vehiculoController;

    private int idRolSesion;

    private final String[] tiposCobertura = {
            "Pérdida o Robo",
            "Accidente",
            "Siniestro Todo Riesgo"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poliza_seguro);

        idRolSesion = getIntent().getIntExtra("ID_ROL", 1);

        tvTituloPoliza = findViewById(R.id.tvTituloPoliza);
        spVehiculoPoliza = findViewById(R.id.spVehiculoPoliza);
        etNumeroPoliza = findViewById(R.id.etNumeroPoliza);
        tvLabelCobertura = findViewById(R.id.tvLabelCobertura);
        spCoberturaPoliza = findViewById(R.id.spCoberturaPoliza);
        etMontoCobertura = findViewById(R.id.etMontoCobertura);
        etFechaVencimientoPoliza = findViewById(R.id.etFechaVencimientoPoliza);
        btnGuardarPoliza = findViewById(R.id.btnGuardarPoliza);
        tvResultadoPoliza = findViewById(R.id.tvResultadoPoliza);

        polizaSeguroController = new PolizaSeguroController(this);
        vehiculoController = new VehiculoController(this);
        findViewById(R.id.btnVolverInicio).setOnClickListener(v -> NavegacionInicio.volver(this));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposCobertura);
        spCoberturaPoliza.setAdapter(adapter);

        if (idRolSesion == 1) { // ROL CLIENTE: Consulta de coberturas
            tvTituloPoliza.setText("Pólizas de Seguro de Vehículos");
            spVehiculoPoliza.setVisibility(View.GONE);
            etNumeroPoliza.setVisibility(View.GONE);
            if (tvLabelCobertura != null) tvLabelCobertura.setVisibility(View.GONE);
            spCoberturaPoliza.setVisibility(View.GONE);
            etMontoCobertura.setVisibility(View.GONE);
            etFechaVencimientoPoliza.setVisibility(View.GONE);
            btnGuardarPoliza.setVisibility(View.GONE);
        } else { // ROL PERSONAL: Registro
            tvTituloPoliza.setText("Registro de Póliza de Seguro");
            etFechaVencimientoPoliza.setOnClickListener(v -> mostrarDatePicker(etFechaVencimientoPoliza));
            btnGuardarPoliza.setOnClickListener(v -> guardarPoliza());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idRolSesion != 1) {
            OpcionesFormulario.cargar(this, spVehiculoPoliza,
                    vehiculoController.listarOpcionesVehiculos(null), "Selecciona un vehículo");
        }
        tvResultadoPoliza.setText(construirPolizas());
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

    private String construirPolizas() {
        StringBuilder sb = new StringBuilder("PÓLIZAS Y COBERTURAS POR VEHÍCULO\n\n");
        Cursor cVeh = vehiculoController.listarTodosVehiculos();
        if (cVeh != null && cVeh.moveToFirst()) {
            do {
                int idVeh = cVeh.getInt(cVeh.getColumnIndexOrThrow("idVehiculo"));
                String placa = cVeh.getString(cVeh.getColumnIndexOrThrow("placa"));
                String marca = cVeh.getString(cVeh.getColumnIndexOrThrow("marca"));
                String modelo = cVeh.getString(cVeh.getColumnIndexOrThrow("modelo"));

                sb.append(placa).append(" (").append(marca).append(" ").append(modelo).append(")\n");

                Cursor cPol = polizaSeguroController.listarPolizasPorVehiculo(idVeh);
                if (cPol != null && cPol.moveToFirst()) {
                    do {
                        String numPol = cPol.getString(cPol.getColumnIndexOrThrow("numeroPoliza"));
                        String cob = cPol.getString(cPol.getColumnIndexOrThrow("cobertura"));
                        double monto = cPol.getDouble(cPol.getColumnIndexOrThrow("montoCobertura"));
                        String venc = cPol.getString(cPol.getColumnIndexOrThrow("fechaVencimiento"));

                        sb.append("  • N° Póliza: ").append(numPol)
                                .append(" | Cobertura: ").append(cob)
                                .append("\n    Monto: S/ ").append(String.format(Locale.US, "%.2f", monto))
                                .append(" | Vence: ").append(venc).append("\n");
                    } while (cPol.moveToNext());
                    cPol.close();
                } else {
                    sb.append("  • Cobertura básica / Sin póliza específica registrada.\n");
                }
                sb.append("----------------------------------------\n");
            } while (cVeh.moveToNext());
            cVeh.close();
            return sb.toString();
        } else {
            return "Sin vehículos ni pólizas registradas.";
        }
    }

    private void guardarPoliza() {
        int idVehiculo = OpcionesFormulario.idSeleccionado(spVehiculoPoliza);
        String numPoliza = etNumeroPoliza.getText().toString().trim();
        String cobertura = spCoberturaPoliza.getSelectedItem().toString();
        String sMonto = etMontoCobertura.getText().toString().trim();
        String fechaVenc = etFechaVencimientoPoliza.getText().toString().trim();

        if (idVehiculo <= 0 || numPoliza.isEmpty() || sMonto.isEmpty() || fechaVenc.isEmpty()) {
            Toast.makeText(this, "Complete todos los datos de la póliza", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double monto = Double.parseDouble(sMonto);
            if (monto < 0 || Double.isNaN(monto) || Double.isInfinite(monto)) {
                Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show();
                return;
            }

            PolizaSeguro poliza = new PolizaSeguro(0, numPoliza, cobertura, monto, fechaVenc, idVehiculo);
            long idPoliza = polizaSeguroController.registrarPoliza(poliza);

            if (idPoliza != -1) {
                Toast.makeText(this, "Póliza de seguro registrada", Toast.LENGTH_SHORT).show();
                tvResultadoPoliza.setText("--- PÓLIZA DE SEGURO REGISTRADA ---\n" +
                        "N° Póliza ID: " + idPoliza + " (" + numPoliza + ")\n" +
                        "Vehículo: " + spVehiculoPoliza.getSelectedItem() + "\n" +
                        "Cobertura: " + cobertura + "\n" +
                        "Monto Cobertura: S/ " + String.format(Locale.US, "%.2f", monto) + "\n" +
                        "Vencimiento: " + fechaVenc + "\n\n" + construirPolizas());

                spVehiculoPoliza.setSelection(0);
                etNumeroPoliza.setText("");
                etMontoCobertura.setText("");
                etFechaVencimientoPoliza.setText("");
            } else {
                Toast.makeText(this, "Error al registrar la póliza", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique los valores numéricos", Toast.LENGTH_SHORT).show();
        }
    }
}
