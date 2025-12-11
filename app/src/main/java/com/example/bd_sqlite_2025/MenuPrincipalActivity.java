package com.example.bd_sqlite_2025;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuPrincipalActivity extends AppCompatActivity {

    private Button btnGestion, btnEventos, btnGestionPagos, btnConfig, btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal); // Asegúrate que tu XML se llama así

        // Inicializar botones
        btnGestion = findViewById(R.id.botonGestion);
        btnEventos = findViewById(R.id.botonEventos);
        btnGestionPagos = findViewById(R.id.botonGestionPagos);
        btnConfig = findViewById(R.id.botonConfig);
        btnCerrarSesion = findViewById(R.id.botonCerrarSesion);

        // Configurar listeners
        btnGestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalActivity.this, GestionPrincipalActivity.class));
            }
        });

        btnEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalActivity.this, ActividadesActivity.class));
            }
        });

        btnGestionPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalActivity.this, DonativosYPagosActivity.class));
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalActivity.this, ActivityConfiguraciones.class));
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalActivity.this, ActivityLogin.class));
                finish();
            }
        });
    }
}
