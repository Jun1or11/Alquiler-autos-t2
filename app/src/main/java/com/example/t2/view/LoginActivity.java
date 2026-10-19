package com.example.t2.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t2.MainActivity;
import com.example.t2.R;
import com.example.t2.controller.UsuarioController;
import com.example.t2.model.Usuario;

public class LoginActivity extends AppCompatActivity {
    private EditText etCorreoLogin;
    private EditText etClaveLogin;
    private UsuarioController usuarioController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreoLogin = findViewById(R.id.etCorreoLogin);
        etClaveLogin = findViewById(R.id.etClaveLogin);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        Button btnIrRegistroUsuarioLogin = findViewById(R.id.btnIrRegistroUsuarioLogin);

        usuarioController = new UsuarioController(this);

        btnIniciarSesion.setOnClickListener(v -> autenticar());
        btnIrRegistroUsuarioLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroUsuarioActivity.class);
            startActivity(intent);
        });
    }

    private void autenticar() {
        String correo = etCorreoLogin.getText().toString().trim();
        String clave = etClaveLogin.getText().toString().trim();

        if (correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = usuarioController.autenticarUsuario(correo, clave);

        if (usuario != null) {
            Toast.makeText(this, "Bienvenido " + usuario.getNombre(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("ID_USUARIO", usuario.getIdUsuario());
            intent.putExtra("NOMBRE_USUARIO", usuario.getNombre());
            intent.putExtra("ID_ROL", usuario.getIdRol());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_LONG).show();
        }
    }
}
