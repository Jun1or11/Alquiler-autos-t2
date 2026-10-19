package com.example.t2.view;

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
import com.example.t2.controller.UsuarioController;
import com.example.t2.model.Usuario;

public class RegistroUsuarioActivity extends AppCompatActivity {
    private EditText etNombreUsuario;
    private EditText etCorreoUsuario;
    private EditText etClaveUsuario;
    private EditText etConfirmarClaveUsuario;
    private Spinner spRolUsuario;
    private TextView tvResultadoUsuario;

    private UsuarioController usuarioController;
    private boolean desdeLogin;

    private final String[] roles = {
            "1 - Cliente",
            "2 - Recepcionista de venta",
            "3 - Inspector de flota",
            "4 - Recepcionista de devolución"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        desdeLogin = !getIntent().hasExtra("ID_USUARIO");

        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etCorreoUsuario = findViewById(R.id.etCorreoUsuario);
        etClaveUsuario = findViewById(R.id.etClaveUsuario);
        etConfirmarClaveUsuario = findViewById(R.id.etConfirmarClaveUsuario);
        spRolUsuario = findViewById(R.id.spRolUsuario);
        Button btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);
        tvResultadoUsuario = findViewById(R.id.tvResultadoUsuario);

        usuarioController = new UsuarioController(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        spRolUsuario.setAdapter(adapter);

        if (desdeLogin) {
            ((TextView) findViewById(R.id.tvTituloUsuario)).setText("Crear cuenta de cliente");
            findViewById(R.id.tvLabelRolUsuario).setVisibility(View.GONE);
            spRolUsuario.setVisibility(View.GONE);
            btnGuardarUsuario.setText("Crear cuenta");
            ((Button) findViewById(R.id.btnVolverInicio)).setText("Volver al login");
        }

        btnGuardarUsuario.setOnClickListener(v -> guardarUsuario());
        findViewById(R.id.btnVolverInicio).setOnClickListener(v -> NavegacionInicio.volver(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (desdeLogin) {
            tvResultadoUsuario.setText("Completa tus datos y, al terminar, vuelve para iniciar sesión.");
        } else {
            mostrarListaUsuarios();
        }
    }

    private void guardarUsuario() {
        String nombre = etNombreUsuario.getText().toString().trim();
        String correo = etCorreoUsuario.getText().toString().trim();
        String clave = etClaveUsuario.getText().toString();
        String confirmarClave = etConfirmarClaveUsuario.getText().toString();

        if (nombre.isEmpty() || correo.isEmpty() || clave.isEmpty() || confirmarClave.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (clave.length() < 6) {
            etClaveUsuario.setError("Usa al menos 6 caracteres");
            return;
        }

        if (!clave.equals(confirmarClave)) {
            etConfirmarClaveUsuario.setError("Las contraseñas no coinciden");
            return;
        }

        // El registro público siempre es de cliente, sin depender del selector de rol.
        int idRol = desdeLogin ? 1 : spRolUsuario.getSelectedItemPosition() + 1;

        Usuario nuevo = new Usuario(0, nombre, correo, clave, idRol);
        long idInsertado = usuarioController.registrarUsuario(nuevo);

        if (idInsertado != -1) {
            Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
            etNombreUsuario.setText("");
            etCorreoUsuario.setText("");
            etClaveUsuario.setText("");
            etConfirmarClaveUsuario.setText("");
            if (desdeLogin) {
                tvResultadoUsuario.setText("Cuenta creada. Vuelve a la pantalla de acceso e inicia sesión.");
            } else {
                mostrarListaUsuarios();
            }
        } else {
            Toast.makeText(this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarListaUsuarios() {
        Cursor c = usuarioController.listarTodosUsuarios();
        StringBuilder sb = new StringBuilder("USUARIOS REGISTRADOS\n\n");
        if (c != null && c.moveToFirst()) {
            do {
                String nombre = c.getString(c.getColumnIndexOrThrow("nombre"));
                String correo = c.getString(c.getColumnIndexOrThrow("correo"));
                String rol = c.getString(c.getColumnIndexOrThrow("nombreRol"));

                sb.append(nombre)
                        .append("\n  Correo: ").append(correo)
                        .append(" | Rol: ").append(rol)
                        .append("\n----------------------------------------\n");
            } while (c.moveToNext());
            c.close();
            tvResultadoUsuario.setText(sb.toString());
        } else {
            tvResultadoUsuario.setText("No hay usuarios registrados en la base de datos.");
        }
    }
}
