package com.example.bd_sqlite_2025;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityLogin extends AppCompatActivity {

    private EditText edtUsuario, edtContrasenia;
    private Button btnIniciar, btnSalir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsuario = findViewById(R.id.edtUsuario);
        edtContrasenia = findViewById(R.id.edtContrasenia);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnSalir = findViewById(R.id.btnSalir);

        btnIniciar.setOnClickListener(v -> iniciarSesion());
        btnSalir.setOnClickListener(v -> finishAffinity());
    }

    private void iniciarSesion() {
        String usuario = edtUsuario.getText().toString().trim();
        String password = edtContrasenia.getText().toString().trim();

        if (usuario.equals("Mayra") && password.equals("12345")) {

            Toast.makeText(this, "¡Bienvenida, Mayra!", Toast.LENGTH_LONG).show();

            Intent i = new Intent(this, MenuPrincipalActivity.class);
            startActivity(i);
            finish();

        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
            edtContrasenia.setText("");
            edtUsuario.requestFocus();
        }
    }
}
