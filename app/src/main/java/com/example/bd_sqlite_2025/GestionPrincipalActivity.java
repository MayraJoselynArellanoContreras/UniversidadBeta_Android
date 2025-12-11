package com.example.bd_sqlite_2025;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GestionPrincipalActivity extends AppCompatActivity {

    private Button btnDonadores;
    private Button btnCorporaciones;
    private Button btnRepresentantes;
    private Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_principal); // Asegúrate que el XML se llame así

        // Inicializar botones
        btnDonadores = findViewById(R.id.btnDonadores);
        btnCorporaciones = findViewById(R.id.btnCorporaciones);
        btnRepresentantes = findViewById(R.id.btnRepresentantes);
        btnVolver = findViewById(R.id.btnVolver);

        // Configurar listeners
        btnDonadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirDonadores();
            }
        });

        btnCorporaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCorporaciones();
            }
        });

        btnRepresentantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirRepresentantes();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverMenuPrincipal();
            }
        });
    }

    private void abrirDonadores() {
        // Aquí abrirás la actividad de Donadores
        Toast.makeText(this, "Abriendo gestión de Donadores", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, DonadoresActivity.class);
        startActivity(intent);
    }

    private void abrirCorporaciones() {

        Toast.makeText(this, "Abriendo gestión de Corporaciones", Toast.LENGTH_SHORT).show();

        // Intent intent = new Intent(this, CorporacionesActivity.class);
        // startActivity(intent);

    }

    private void abrirRepresentantes() {
        // Aquí abrirás la actividad de Representantes
        Toast.makeText(this, "Abriendo gestión de Representantes", Toast.LENGTH_SHORT).show();

        // Descomentar cuando tengas la actividad de Representantes
        // Intent intent = new Intent(this, RepresentantesActivity.class);
        // startActivity(intent);

        // Para probar:
        Toast.makeText(this, "Módulo Representantes - En desarrollo", Toast.LENGTH_LONG).show();
    }

    private void volverMenuPrincipal() {
        // Regresar al menú principal
        Intent intent = new Intent(this, MenuPrincipalActivity.class);
        startActivity(intent);
        finish(); // Cierra esta actividad para liberar memoria
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Opcional: Manejar botón de retroceso físico del dispositivo
    @Override
    public void onBackPressed() {
        volverMenuPrincipal();
    }



}