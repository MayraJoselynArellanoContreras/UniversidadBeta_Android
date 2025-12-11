package com.example.bd_sqlite_2025;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityConfiguraciones extends AppCompatActivity {

    private Button btnCirculo;
    private Button btnConfigFiscal;

    private Button btnVolverConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuraciones);


        btnCirculo = findViewById(R.id.btnCirculo);
        btnConfigFiscal = findViewById(R.id.btnConfigFiscal);
        btnVolverConfig = findViewById(R.id.btnVolverConfig);


        btnCirculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCirculoDonador();
            }
        });

        btnConfigFiscal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirConfigFiscal();
            }
        });


        btnVolverConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volverMenuPrincipal();
            }
        });
    }

    private void abrirCirculoDonador() {
        // Aquí abrirás la actividad de Círculo Donador
        Toast.makeText(this, "Abriendo Círculo Donador", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(this, CirculoDonadorActivity.class);
        startActivity(intent);

    }

    private void abrirConfigFiscal() {
        // Aquí abrirás la actividad de Configuración Fiscal
        Toast.makeText(this, "Abriendo Configuración Fiscal", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(this, ConfigFiscalActivity.class);
        startActivity(intent);

    }



    private void volverMenuPrincipal() {
        // Regresar al menú principal
        Intent intent = new Intent(this, MenuPrincipalActivity.class);
        startActivity(intent);
        finish(); // Cierra esta actividad
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }






}