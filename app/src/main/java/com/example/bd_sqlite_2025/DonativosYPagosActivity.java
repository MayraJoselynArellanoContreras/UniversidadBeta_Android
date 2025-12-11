package com.example.bd_sqlite_2025;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DonativosYPagosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donativos_pagos);

        Button btnGarantias = findViewById(R.id.btnGarantias);
        Button btnDonativos = findViewById(R.id.btnDonativos);
        Button btnVolver = findViewById(R.id.btnVolver);

        btnGarantias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonativosYPagosActivity.this, GarantiasActivity.class));
            }
        });

        btnDonativos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonativosYPagosActivity.this, DonativoActivity.class));
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonativosYPagosActivity.this,
                        MenuPrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


}