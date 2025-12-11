package com.example.bd_sqlite_2025;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import controlers.AnioFiscalDAO;
import db.UniversidadBeta;
import entities.AnioFiscal;
import ReciclerViews.AnioFiscalAdapter;

public class ConfigFiscalActivity extends AppCompatActivity {

    private EditText txtID, txtNombre, txtFechaInicio, txtFechaFin;
    private TextView txtEstado;
    private Button btnActivar, btnEditar, btnEliminar, btnAgregar, btnVolver;
    private RecyclerView recyclerViewAnios;

    private UniversidadBeta db;
    private AnioFiscalDAO anioFiscalDAO;
    private List<AnioFiscal> listaAnios;
    private AnioFiscalAdapter adapter;
    private AnioFiscal anioSeleccionado = null;
    private int maxId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_fiscal);


        db = UniversidadBeta.getAppDatabase(this);
        anioFiscalDAO = db.anioFiscalDAO();

        inicializarVistas();
        configurarRecyclerView();
        cargarAniosFiscales();
        configurarEventos();
    }

    private void inicializarVistas() {
        txtID = findViewById(R.id.txtID);
        txtNombre = findViewById(R.id.txtNombre);
        txtFechaInicio = findViewById(R.id.txtFechaInicio);
        txtFechaFin = findViewById(R.id.txtFechaFin);
        txtEstado = findViewById(R.id.txtEstado);

        btnActivar = findViewById(R.id.btnActivar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerViewAnios = findViewById(R.id.recyclerViewAnios);
    }

    private void configurarRecyclerView() {
        listaAnios = new ArrayList<>();
        adapter = new AnioFiscalAdapter(listaAnios, new AnioFiscalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AnioFiscal anioFiscal) {
                seleccionarAnioFiscal(anioFiscal);
            }

            @Override
            public void onItemLongClick(AnioFiscal anioFiscal) {
                mostrarDialogoOpciones(anioFiscal);
            }
        });

        recyclerViewAnios.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAnios.setAdapter(adapter);
    }

    private void configurarEventos() {
        btnAgregar.setOnClickListener(v -> agregarAnioFiscal());
        btnEditar.setOnClickListener(v -> editarAnioFiscal());
        btnEliminar.setOnClickListener(v -> eliminarAnioFiscal());
        btnActivar.setOnClickListener(v -> activarAnioFiscal());
        btnVolver.setOnClickListener(v -> finish());

        // Establecer fecha actual como sugerencia
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        txtFechaInicio.setHint("Ej: " + fechaHoy);
        txtFechaFin.setHint("Ej: " + fechaHoy);
    }

    private void cargarAniosFiscales() {
        new Thread(() -> {
            List<AnioFiscal> anios = anioFiscalDAO.mostrarTodos();
            runOnUiThread(() -> {
                listaAnios.clear();
                listaAnios.addAll(anios);
                adapter.notifyDataSetChanged();

                // Encontrar el máximo ID
                maxId = 0;
                for (AnioFiscal anio : anios) {
                    if (anio.idAnioFiscal > maxId) {
                        maxId = anio.idAnioFiscal;
                    }
                }

                txtID.setText(String.valueOf(maxId + 1));

                actualizarEstadoActivo();
            });
        }).start();
    }

    private void actualizarEstadoActivo() {
        boolean hayActivo = false;
        for (AnioFiscal anio : listaAnios) {
            if (anio.activo != null && anio.activo) {
                hayActivo = true;
                break;
            }
        }

        if (hayActivo) {
            txtEstado.setText("HAY AÑO ACTIVO");
            txtEstado.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            txtEstado.setText("SIN AÑO ACTIVO");
            txtEstado.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void agregarAnioFiscal() {
        String nombre = txtNombre.getText().toString().trim();
        String fechaInicio = txtFechaInicio.getText().toString().trim();
        String fechaFin = txtFechaFin.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaInicio.isEmpty()) {
            Toast.makeText(this, "La fecha de inicio es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaFin.isEmpty()) {
            Toast.makeText(this, "La fecha de fin es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!validarFormatoFecha(fechaInicio)) {
            Toast.makeText(this, "Formato fecha inicio inválido. Use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarFormatoFecha(fechaFin)) {
            Toast.makeText(this, "Formato fecha fin inválido. Use YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaFin.compareTo(fechaInicio) <= 0) {
            Toast.makeText(this, "La fecha fin debe ser posterior a la fecha inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        int nuevoId = maxId + 1;
        String fechaCreacion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());


        AnioFiscal nuevoAnio = new AnioFiscal(
                nuevoId,
                nombre,
                fechaInicio,
                fechaFin,
                false, // Inactivo por defecto
                fechaCreacion
        );

        new Thread(() -> {
            anioFiscalDAO.agregarAnioFiscal(nuevoAnio);
            runOnUiThread(() -> {
                Toast.makeText(this, "Año fiscal agregado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarAniosFiscales();
            });
        }).start();
    }

    private boolean validarFormatoFecha(String fecha) {
        // Validación simple de formato YYYY-MM-DD
        return fecha.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private void editarAnioFiscal() {
        if (anioSeleccionado == null) {
            Toast.makeText(this, "Seleccione un año fiscal para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = txtNombre.getText().toString().trim();
        String fechaInicio = txtFechaInicio.getText().toString().trim();
        String fechaFin = txtFechaFin.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty() || fechaInicio.isEmpty() || fechaFin.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarFormatoFecha(fechaInicio) || !validarFormatoFecha(fechaFin)) {
            Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fechaFin.compareTo(fechaInicio) <= 0) {
            Toast.makeText(this, "La fecha fin debe ser posterior a la fecha inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        AnioFiscal anioActualizado = new AnioFiscal(
                anioSeleccionado.idAnioFiscal,
                nombre,
                fechaInicio,
                fechaFin,
                anioSeleccionado.activo, // Mantener estado activo
                anioSeleccionado.fechaCreacion // Mantener fecha creación original
        );

        new Thread(() -> {
            anioFiscalDAO.actualizarAnioFiscal(anioActualizado);
            runOnUiThread(() -> {
                Toast.makeText(this, "Año fiscal actualizado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarAniosFiscales();
                anioSeleccionado = null;
                btnEditar.setEnabled(false);
                btnEliminar.setEnabled(false);
                btnActivar.setEnabled(false);
            });
        }).start();
    }

    private void eliminarAnioFiscal() {
        if (anioSeleccionado == null) {
            Toast.makeText(this, "Seleccione un año fiscal para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        // No permitir eliminar año activo
        if (anioSeleccionado.activo != null && anioSeleccionado.activo) {
            Toast.makeText(this, "No se puede eliminar un año fiscal activo", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar el año fiscal '" + anioSeleccionado.nombre + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    new Thread(() -> {
                        anioFiscalDAO.eliminarAnioFiscal(anioSeleccionado);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Año fiscal eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarFormulario();
                            cargarAniosFiscales();
                            anioSeleccionado = null;
                            btnEditar.setEnabled(false);
                            btnEliminar.setEnabled(false);
                            btnActivar.setEnabled(false);
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void activarAnioFiscal() {
        if (anioSeleccionado == null) {
            Toast.makeText(this, "Seleccione un año fiscal para activar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ya está activo
        if (anioSeleccionado.activo != null && anioSeleccionado.activo) {
            Toast.makeText(this, "Este año fiscal ya está activo", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Activar Año Fiscal")
                .setMessage("¿Activar el año fiscal '" + anioSeleccionado.nombre + "'?\n\nEsto desactivará cualquier otro año fiscal activo.")
                .setPositiveButton("Activar", (dialog, which) -> {
                    new Thread(() -> {
                        // 1. Desactivar todos los años fiscales
                        for (AnioFiscal anio : listaAnios) {
                            if (anio.activo != null && anio.activo) {
                                AnioFiscal anioDesactivado = new AnioFiscal(
                                        anio.idAnioFiscal,
                                        anio.nombre,
                                        anio.fechaInicio,
                                        anio.fechaFin,
                                        false,
                                        anio.fechaCreacion
                                );
                                anioFiscalDAO.actualizarAnioFiscal(anioDesactivado);
                            }
                        }

                        // 2. Activar el año seleccionado
                        AnioFiscal anioActivado = new AnioFiscal(
                                anioSeleccionado.idAnioFiscal,
                                anioSeleccionado.nombre,
                                anioSeleccionado.fechaInicio,
                                anioSeleccionado.fechaFin,
                                true,
                                anioSeleccionado.fechaCreacion
                        );
                        anioFiscalDAO.actualizarAnioFiscal(anioActivado);

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Año fiscal activado exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarFormulario();
                            cargarAniosFiscales();
                            anioSeleccionado = null;
                            btnActivar.setEnabled(false);
                            btnEditar.setEnabled(false);
                            btnEliminar.setEnabled(false);
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void seleccionarAnioFiscal(AnioFiscal anioFiscal) {
        anioSeleccionado = anioFiscal;

        txtID.setText(String.valueOf(anioFiscal.idAnioFiscal));
        txtNombre.setText(anioFiscal.nombre);
        txtFechaInicio.setText(anioFiscal.fechaInicio);
        txtFechaFin.setText(anioFiscal.fechaFin);

        // Habilitar/deshabilitar botones según estado
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnActivar.setEnabled(anioFiscal.activo == null || !anioFiscal.activo);

        // Actualizar texto del botón activar
        if (anioFiscal.activo != null && anioFiscal.activo) {
            btnActivar.setText("Desactivar");
        } else {
            btnActivar.setText("Activar");
        }
    }

    private void mostrarDialogoOpciones(AnioFiscal anioFiscal) {
        String[] opciones = {"Editar", "Eliminar", "Activar", "Ver detalles"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones: " + anioFiscal.nombre)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            seleccionarAnioFiscal(anioFiscal);
                            break;
                        case 1: // Eliminar
                            anioSeleccionado = anioFiscal;
                            eliminarAnioFiscal();
                            break;
                        case 2: // Activar
                            anioSeleccionado = anioFiscal;
                            activarAnioFiscal();
                            break;
                        case 3: // Ver detalles
                            mostrarDetallesAnioFiscal(anioFiscal);
                            break;
                    }
                })
                .show();
    }

    private void mostrarDetallesAnioFiscal(AnioFiscal anioFiscal) {
        String estado = (anioFiscal.activo != null && anioFiscal.activo) ? "ACTIVO" : "INACTIVO";
        String colorEstado = (anioFiscal.activo != null && anioFiscal.activo) ? "🟢" : "🔴";

        String detalles = colorEstado + " Estado: " + estado + "\n" +
                "ID: " + anioFiscal.idAnioFiscal + "\n" +
                "Nombre: " + anioFiscal.nombre + "\n" +
                "Fecha Inicio: " + anioFiscal.fechaInicio + "\n" +
                "Fecha Fin: " + anioFiscal.fechaFin + "\n" +
                "Fecha Creación: " + anioFiscal.fechaCreacion;

        new AlertDialog.Builder(this)
                .setTitle("Detalles del Año Fiscal")
                .setMessage(detalles)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtID.setText(String.valueOf(maxId + 1));

        anioSeleccionado = null;
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnActivar.setEnabled(false);
        btnActivar.setText("Activar");

        // Actualizar estado general
        actualizarEstadoActivo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}