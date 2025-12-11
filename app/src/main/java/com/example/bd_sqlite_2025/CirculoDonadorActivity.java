package com.example.bd_sqlite_2025;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import ReciclerViews.CirculoDonadorAdapter;
import controlers.CirculoDonadorDAO;
import db.UniversidadBeta;
import entities.CirculoDonador;
import ReciclerViews.CirculoDonadorAdapter;

public class CirculoDonadorActivity extends AppCompatActivity {

    private EditText etIdCirculo, etNombreCirculo, etMontoMinimo, etMontoMaximo;
    private Button btnRegistrar, btnEditar, btnEliminar, btnBuscar, btnLimpiar, btnVolver;
    private RecyclerView recyclerViewCirculos;

    private UniversidadBeta db;
    private CirculoDonadorDAO circuloDonadorDAO;
    private List<CirculoDonador> listaCirculos;
    private CirculoDonadorAdapter adapter;
    private CirculoDonador circuloSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circulo_donador);

        // Inicializar base de datos
        db = UniversidadBeta.getAppDatabase(this);
        circuloDonadorDAO = db.circuloDonadorDAO();

        inicializarVistas();
        configurarRecyclerView();
        cargarCirculos();
        configurarEventos();
    }

    private void inicializarVistas() {
        etIdCirculo = findViewById(R.id.etIdCirculo);
        etNombreCirculo = findViewById(R.id.etNombreCirculo);
        etMontoMinimo = findViewById(R.id.etMontoMinimo);
        etMontoMaximo = findViewById(R.id.etMontoMaximo);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerViewCirculos = findViewById(R.id.recyclerViewCirculos);
    }

    private void configurarRecyclerView() {
        listaCirculos = new ArrayList<>();
        adapter = new CirculoDonadorAdapter(listaCirculos, new CirculoDonadorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CirculoDonador circulo) {
                seleccionarCirculo(circulo);
            }

            @Override
            public void onItemLongClick(CirculoDonador circulo) {
                mostrarDialogoOpciones(circulo);
            }
        });

        recyclerViewCirculos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCirculos.setAdapter(adapter);
    }

    private void configurarEventos() {
        btnRegistrar.setOnClickListener(v -> registrarCirculo());
        btnEditar.setOnClickListener(v -> editarCirculo());
        btnEliminar.setOnClickListener(v -> eliminarCirculo());
        btnBuscar.setOnClickListener(v -> buscarCirculo());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void cargarCirculos() {
        new Thread(() -> {
            List<CirculoDonador> circulos = circuloDonadorDAO.mostrarTodos();
            runOnUiThread(() -> {
                listaCirculos.clear();
                listaCirculos.addAll(circulos);
            });
        }).start();
    }

    private void registrarCirculo() {
        String nombre = etNombreCirculo.getText().toString().trim();
        String montoMinimoStr = etMontoMinimo.getText().toString().trim();
        String montoMaximoStr = etMontoMaximo.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        double montoMinimo = 0;
        try {
            if (!montoMinimoStr.isEmpty()) {
                montoMinimo = Double.parseDouble(montoMinimoStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto mínimo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcion = construirDescripcion(montoMinimoStr, montoMaximoStr);

        new Thread(() -> {
            // Buscar el máximo ID actual para generar el siguiente
            int maxId = 0;
            if (!listaCirculos.isEmpty()) {
                for (CirculoDonador c : listaCirculos) {
                    if (c.idCirculo > maxId) {
                        maxId = c.idCirculo;
                    }
                }
            }

            CirculoDonador nuevoCirculo = new CirculoDonador(
                    maxId + 1,
                    nombre,
                    descripcion
            );

            circuloDonadorDAO.agregarCirculo(nuevoCirculo);

            runOnUiThread(() -> {
                Toast.makeText(this, "Círculo registrado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarCirculos();
            });
        }).start();
    }

    private String construirDescripcion(String montoMinimo, String montoMaximo) {
        StringBuilder descripcion = new StringBuilder();
        if (!montoMinimo.isEmpty()) {
            descripcion.append("Monto mínimo: $").append(montoMinimo);
        }
        if (!montoMaximo.isEmpty()) {
            if (descripcion.length() > 0) descripcion.append(" | ");
            descripcion.append("Monto máximo: $").append(montoMaximo);
        }
        return descripcion.toString();
    }

    private void editarCirculo() {
        if (circuloSeleccionado == null) {
            Toast.makeText(this, "Seleccione un círculo para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = etNombreCirculo.getText().toString().trim();
        String montoMinimoStr = etMontoMinimo.getText().toString().trim();
        String montoMaximoStr = etMontoMaximo.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcion = construirDescripcion(montoMinimoStr, montoMaximoStr);

        CirculoDonador circuloActualizado = new CirculoDonador(
                circuloSeleccionado.idCirculo,
                nombre,
                descripcion
        );

        new Thread(() -> {
            circuloDonadorDAO.actualizarCirculo(circuloActualizado);
            runOnUiThread(() -> {
                Toast.makeText(this, "Círculo actualizado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarCirculos();
                circuloSeleccionado = null;
                btnEditar.setEnabled(false);
                btnEliminar.setEnabled(false);
            });
        }).start();
    }

    private void eliminarCirculo() {
        if (circuloSeleccionado == null) {
            Toast.makeText(this, "Seleccione un círculo para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar el círculo '" + circuloSeleccionado.nombre + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    new Thread(() -> {
                        circuloDonadorDAO.eliminarCirculo(circuloSeleccionado);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Círculo eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarFormulario();
                            cargarCirculos();
                            circuloSeleccionado = null;
                            btnEditar.setEnabled(false);
                            btnEliminar.setEnabled(false);
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void buscarCirculo() {
        String filtro = etNombreCirculo.getText().toString().trim();

        if (filtro.isEmpty()) {
            cargarCirculos();
            return;
        }

        new Thread(() -> {
            List<CirculoDonador> resultados = circuloDonadorDAO.buscarPorCoincidencia(filtro);
            runOnUiThread(() -> {
                listaCirculos.clear();
                listaCirculos.addAll(resultados);

                if (resultados.isEmpty()) {
                    Toast.makeText(this, "No se encontraron círculos", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void limpiarFormulario() {
        etIdCirculo.setText("");
        etNombreCirculo.setText("");
        etMontoMinimo.setText("");
        etMontoMaximo.setText("");
        circuloSeleccionado = null;
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        cargarCirculos();
    }

    private void seleccionarCirculo(CirculoDonador circulo) {
        circuloSeleccionado = circulo;

        etIdCirculo.setText(String.valueOf(circulo.idCirculo));
        etNombreCirculo.setText(circulo.nombre);

        // Parsear descripción para montos
        if (circulo.descripcion != null && !circulo.descripcion.isEmpty()) {
            String[] partes = circulo.descripcion.split("\\|");
            for (String parte : partes) {
                if (parte.contains("Monto mínimo:")) {
                    String monto = parte.replace("Monto mínimo: $", "").trim();
                    etMontoMinimo.setText(monto);
                } else if (parte.contains("Monto máximo:")) {
                    String monto = parte.replace("Monto máximo: $", "").trim();
                    etMontoMaximo.setText(monto);
                }
            }
        }

        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void mostrarDialogoOpciones(CirculoDonador circulo) {
        String[] opciones = {"Editar", "Eliminar", "Ver detalles"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones: " + circulo.nombre)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            seleccionarCirculo(circulo);
                            break;
                        case 1: // Eliminar
                            circuloSeleccionado = circulo;
                            eliminarCirculo();
                            break;
                        case 2: // Ver detalles
                            mostrarDetallesCirculo(circulo);
                            break;
                    }
                })
                .show();
    }

    private void mostrarDetallesCirculo(CirculoDonador circulo) {
        String detalles = "ID: " + circulo.idCirculo + "\n" +
                "Nombre: " + circulo.nombre + "\n" +
                "Descripción: " + (circulo.descripcion != null ? circulo.descripcion : "Sin descripción");

        new AlertDialog.Builder(this)
                .setTitle("Detalles del Círculo")
                .setMessage(detalles)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
