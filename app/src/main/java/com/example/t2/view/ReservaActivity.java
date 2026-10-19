package com.example.t2.view;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t2.R;
import com.example.t2.controller.ReservaController;
import com.example.t2.controller.UsuarioController;
import com.example.t2.controller.VehiculoController;
import com.example.t2.model.Reserva;

import java.util.Calendar;
import java.util.Locale;

public class ReservaActivity extends AppCompatActivity {
    private Spinner spClienteReserva;
    private Spinner spVehiculoReserva;
    private EditText etFechaReserva;
    private EditText etMontoAdelanto;
    private TextView tvResultadoReserva;
    private TextView tvVehiculoSeleccionado;
    private LinearLayout contenedorVehiculosReserva;

    private ReservaController reservaController;
    private VehiculoController vehiculoController;
    private UsuarioController usuarioController;

    private int idUsuarioSesion;
    private int idRolSesion;
    private int idVehiculoSeleccionado;
    private boolean soloMisReservas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        idUsuarioSesion = getIntent().getIntExtra("ID_USUARIO", -1);
        idRolSesion = getIntent().getIntExtra("ID_ROL", 1);
        idVehiculoSeleccionado = getIntent().getIntExtra("ID_VEHICULO", -1);
        soloMisReservas = idRolSesion == 1 && getIntent().getBooleanExtra("SOLO_MIS_RESERVAS", false);

        spClienteReserva = findViewById(R.id.spClienteReserva);
        spVehiculoReserva = findViewById(R.id.spVehiculoReserva);
        etFechaReserva = findViewById(R.id.etFechaReserva);
        etMontoAdelanto = findViewById(R.id.etMontoAdelanto);
        Button btnRegistrarReserva = findViewById(R.id.btnRegistrarReserva);
        tvResultadoReserva = findViewById(R.id.tvResultadoReserva);
        tvVehiculoSeleccionado = findViewById(R.id.tvVehiculoSeleccionado);
        contenedorVehiculosReserva = findViewById(R.id.contenedorVehiculosReserva);

        reservaController = new ReservaController(this);
        vehiculoController = new VehiculoController(this);
        usuarioController = new UsuarioController(this);
        findViewById(R.id.btnVolverInicio).setOnClickListener(v -> NavegacionInicio.volver(this));

        if (idRolSesion == 1) { // ROL CLIENTE
            if (idUsuarioSesion <= 0) {
                Toast.makeText(this, "Inicia sesión para consultar tus reservas", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            spClienteReserva.setVisibility(View.GONE);
            spVehiculoReserva.setVisibility(View.GONE);
            if (soloMisReservas) {
                ((TextView) findViewById(R.id.tvTituloReserva)).setText("Mis Reservas");
                etFechaReserva.setVisibility(View.GONE);
                etMontoAdelanto.setVisibility(View.GONE);
                btnRegistrarReserva.setVisibility(View.GONE);
            } else {
                ((TextView) findViewById(R.id.tvTituloReserva)).setText("Registrar Reserva");
                tvVehiculoSeleccionado.setVisibility(View.VISIBLE);
                contenedorVehiculosReserva.setVisibility(View.VISIBLE);
            }
        }

        etFechaReserva.setOnClickListener(v -> mostrarDatePicker(etFechaReserva));

        btnRegistrarReserva.setOnClickListener(v -> procesarReserva());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idRolSesion == 1 && idUsuarioSesion > 0) {
            if (soloMisReservas) {
                mostrarGuiaConsultas(null);
            } else {
                mostrarVehiculosParaReserva();
            }
        } else if (idRolSesion != 1) {
            OpcionesFormulario.cargar(this, spClienteReserva,
                    usuarioController.listarOpcionesPorRol(1), "Selecciona un cliente");
            OpcionesFormulario.cargar(this, spVehiculoReserva,
                    vehiculoController.listarOpcionesVehiculos("Disponible"), "Selecciona un vehículo disponible");
            mostrarGuiaConsultas(null);
        }
    }

    private void mostrarVehiculosParaReserva() {
        contenedorVehiculosReserva.removeAllViews();
        boolean hayDisponibles = false;
        boolean seleccionadoDisponible = false;
        String descripcionSeleccionada = null;

        try (Cursor c = vehiculoController.listarTodosVehiculos()) {
            while (c.moveToNext()) {
                if (!"Disponible".equals(c.getString(c.getColumnIndexOrThrow("estado")))) {
                    continue;
                }
                hayDisponibles = true;
                int id = c.getInt(c.getColumnIndexOrThrow("idVehiculo"));
                String descripcion = c.getString(c.getColumnIndexOrThrow("placa")) + " • " +
                        c.getString(c.getColumnIndexOrThrow("marca")) + " " +
                        c.getString(c.getColumnIndexOrThrow("modelo")) + " (" +
                        c.getString(c.getColumnIndexOrThrow("categoria")) + ", " +
                        c.getString(c.getColumnIndexOrThrow("sede")) + ")";

                if (id == idVehiculoSeleccionado) {
                    seleccionadoDisponible = true;
                    descripcionSeleccionada = descripcion;
                }
                Button opcion = new Button(this);
                opcion.setText(id == idVehiculoSeleccionado ? "Seleccionado: " + descripcion : "Elegir " + descripcion);
                opcion.setAllCaps(false);
                opcion.setBackgroundResource(R.drawable.bg_app_secondary);
                opcion.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.app_soft)));
                opcion.setTextColor(getColor(R.color.app_secondary_text));
                opcion.setEnabled(id != idVehiculoSeleccionado);
                opcion.setOnClickListener(v -> {
                    idVehiculoSeleccionado = id;
                    mostrarVehiculosParaReserva();
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = (int) (getResources().getDisplayMetrics().density * 8 + 0.5f);
                contenedorVehiculosReserva.addView(opcion, params);
            }
        }

        if (!seleccionadoDisponible) {
            idVehiculoSeleccionado = -1;
        }
        tvVehiculoSeleccionado.setText(seleccionadoDisponible
                ? "Vehículo para tu reserva: " + descripcionSeleccionada
                : "Elige un vehículo disponible para tu reserva:");
        if (!hayDisponibles) {
            TextView vacio = new TextView(this);
            vacio.setText("No hay vehículos disponibles en este momento.");
            vacio.setTextColor(getColor(R.color.app_text));
            contenedorVehiculosReserva.addView(vacio);
        }
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

    private void mostrarGuiaConsultas(String mensaje) {
        StringBuilder sb = new StringBuilder();
        if (mensaje != null) sb.append(mensaje).append("\n\n");

        if (idRolSesion == 1) { // CLIENTE VERÁ SÓLO SUS RESERVAS
            sb.append("--- MIS RESERVAS REGISTRADAS ---\n");
            Cursor cRes = reservaController.listarReservasPorCliente(idUsuarioSesion);
            if (cRes != null && cRes.moveToFirst()) {
                do {
                    int id = cRes.getInt(cRes.getColumnIndexOrThrow("idReserva"));
                    String fecha = cRes.getString(cRes.getColumnIndexOrThrow("fechaReserva"));
                    double adelanto = cRes.getDouble(cRes.getColumnIndexOrThrow("montoAdelanto"));
                    String estado = cRes.getString(cRes.getColumnIndexOrThrow("estadoReserva"));
                    String placa = cRes.getString(cRes.getColumnIndexOrThrow("placa"));
                    String marca = cRes.getString(cRes.getColumnIndexOrThrow("marca"));
                    String codConst = cRes.getString(cRes.getColumnIndexOrThrow("codigoConstancia"));

                    sb.append("Reserva N° ").append(id)
                            .append(" | Fecha: ").append(fecha)
                            .append("\n  Vehículo: ").append(placa).append(" (").append(marca).append(")")
                            .append("\n  Adelanto: S/ ").append(String.format(Locale.US, "%.2f", adelanto))
                            .append(" | Estado: ").append(estado)
                            .append("\n  Constancia: ").append(codConst != null ? codConst : "N/A")
                            .append("\n----------------------------------------\n");
                } while (cRes.moveToNext());
                cRes.close();
            } else {
                sb.append("Usted no tiene reservas registradas.\n\n");
            }
        } else { // ADMIN O PERSONAL VERÁ TODAS LAS RESERVAS
            sb.append("RESERVAS REGISTRADAS\n\n");
            Cursor cResAll = reservaController.listarReservasActivas();
            if (cResAll != null && cResAll.moveToFirst()) {
                do {
                    int id = cResAll.getInt(cResAll.getColumnIndexOrThrow("idReserva"));
                    String cliente = cResAll.getString(cResAll.getColumnIndexOrThrow("cliente"));
                    String fecha = cResAll.getString(cResAll.getColumnIndexOrThrow("fechaReserva"));
                    String placa = cResAll.getString(cResAll.getColumnIndexOrThrow("placa"));
                    String estado = cResAll.getString(cResAll.getColumnIndexOrThrow("estadoReserva"));

                    sb.append("Reserva N° ").append(id)
                            .append(" | Cliente: ").append(cliente)
                            .append("\n  Fecha: ").append(fecha)
                            .append(" | Placa: ").append(placa)
                            .append(" | Estado: ").append(estado)
                            .append("\n----------------------------------------\n");
                } while (cResAll.moveToNext());
                cResAll.close();
            } else {
                sb.append("No hay reservas registradas en el sistema.\n");
            }
        }

        tvResultadoReserva.setText(sb.toString());
    }

    private void procesarReserva() {
        if (soloMisReservas) {
            return;
        }
        int idCliente = idRolSesion == 1 ? idUsuarioSesion : OpcionesFormulario.idSeleccionado(spClienteReserva);
        int idVehiculo = idRolSesion == 1 ? idVehiculoSeleccionado : OpcionesFormulario.idSeleccionado(spVehiculoReserva);
        String fecha = etFechaReserva.getText().toString().trim();
        String sMonto = etMontoAdelanto.getText().toString().trim();

        if (idCliente <= 0 || idVehiculo <= 0 || fecha.isEmpty() || sMonto.isEmpty()) {
            Toast.makeText(this, idRolSesion == 1
                    ? "Selecciona un vehículo, fecha y adelanto"
                    : "Complete todos los campos para generar la reserva", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double montoAdelanto = Double.parseDouble(sMonto);
            if (montoAdelanto < 0 || Double.isNaN(montoAdelanto) || Double.isInfinite(montoAdelanto)) {
                Toast.makeText(this, "Ingrese un adelanto válido", Toast.LENGTH_SHORT).show();
                return;
            }

            Reserva reserva = new Reserva(0, fecha, montoAdelanto, "Confirmada", idCliente, idVehiculo);
            long idReserva = reservaController.registrarReserva(reserva);

            if (idReserva != -1) {
                Toast.makeText(this, "Reserva realizada exitosamente", Toast.LENGTH_SHORT).show();
                String vehiculo = idRolSesion == 1
                        ? tvVehiculoSeleccionado.getText().toString().replace("Vehículo para tu reserva: ", "")
                        : spVehiculoReserva.getSelectedItem().toString();
                String cliente = idRolSesion == 1 ? "Tu cuenta" : spClienteReserva.getSelectedItem().toString();
                String confirmacion = "RESERVA CONFIRMADA\n" +
                        "N° Reserva: " + idReserva + "\n" +
                        "Vehículo: " + vehiculo + " (Reservado)\n" +
                        "Cliente: " + cliente + "\n" +
                        "Monto Adelanto: S/ " + String.format(Locale.US, "%.2f", montoAdelanto) + "\n" +
                        "Comprobante y constancia emitidos automáticamente.";

                if (idRolSesion != 1) {
                    OpcionesFormulario.cargar(this, spVehiculoReserva,
                            vehiculoController.listarOpcionesVehiculos("Disponible"), "Selecciona un vehículo disponible");
                }
                etFechaReserva.setText("");
                etMontoAdelanto.setText("");
                if (idRolSesion == 1) {
                    idVehiculoSeleccionado = -1;
                    mostrarVehiculosParaReserva();
                    tvResultadoReserva.setText(confirmacion);
                } else {
                    mostrarGuiaConsultas(confirmacion);
                }
            } else {
                Toast.makeText(this, "No se pudo reservar: el vehículo ya no está disponible o hubo un error", Toast.LENGTH_LONG).show();
                if (idRolSesion == 1) {
                    mostrarVehiculosParaReserva();
                }
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Verifique los datos numéricos", Toast.LENGTH_SHORT).show();
        }
    }
}
