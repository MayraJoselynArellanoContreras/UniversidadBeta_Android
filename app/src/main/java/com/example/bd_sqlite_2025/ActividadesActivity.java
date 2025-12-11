package com.example.bd_sqlite_2025;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActividadesActivity extends AppCompatActivity {

    private Toolbar toolbarActividades;
    private Button btnEventosIngresar;
    private Button btnVoluntariosIngresar;
    private Button btnVolverActividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades); // Asegúrate que el XML se llama así

        // Inicializar vistas
        toolbarActividades = findViewById(R.id.toolbar_actividades);
        btnEventosIngresar = findViewById(R.id.btnEventosIngresar);
        btnVoluntariosIngresar = findViewById(R.id.btnVoluntariosIngresar);
        btnVolverActividades = findViewById(R.id.btnVolverActividades);

        // Configurar toolbar
        setSupportActionBar(toolbarActividades);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Cambiar a true si quieres botón de retroceso
            getSupportActionBar().setTitle("Eventos y Actividades");
        }

        // Configurar listeners
        btnEventosIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirEventos();
            }
        });

        btnVoluntariosIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirVoluntarios();
            }
        });

        btnVolverActividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverMenuPrincipal();
            }
        });
    }

    private void abrirEventos() {

        Toast.makeText(this, "Abriendo gestión de Eventos", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, EventosActivity.class);
        startActivity(intent);
    }

    private void abrirVoluntarios() {
        // Aquí puedes abrir la actividad de Voluntarios
        Toast.makeText(this, "Abriendo gestión de Voluntarios", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, VoluntariosActivity.class);
        startActivity(intent);
    }

    private void volverMenuPrincipal() {
        // Regresar al menú principal
        Intent intent = new Intent(this, MenuPrincipalActivity.class);
        startActivity(intent);
        finish(); // Opcional: cierra esta actividad
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }



}