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
import java.util.ArrayList;
import java.util.List;
import controlers.CorporacionDAO;
import db.UniversidadBeta;
import entities.Corporacion;
import ReciclerViews.CorporacionAdapter;

public class GestionCorporacionesActivity extends AppCompatActivity {

    // Views
    private TextView txtIdCorporacion;
    private EditText txtNombreCorporacion, txtDireccion, txtBuscar;
    private Button btnAgregar, btnEliminar, btnLimpiar, btnBuscar, btnVolver;
    private RecyclerView recyclerViewCorporaciones;

    // Base de datos y DAO
    private UniversidadBeta db;
    private CorporacionDAO corporacionDAO;

    // Datos
    private List<Corporacion> listaCorporaciones;
    private CorporacionAdapter adapter;
    private Corporacion corporacionSeleccionada = null;
    private int maxIdCorporacion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporaciones);

        // Inicializar base de datos
        db = UniversidadBeta.getAppDatabase(this);
        corporacionDAO = db.corporacionDAO();

        inicializarVistas();
        configurarRecyclerView();
        cargarDatosIniciales();
        configurarEventos();
    }

    private void inicializarVistas() {
        txtIdCorporacion = findViewById(R.id.txtIdCorporacion);
        txtNombreCorporacion = findViewById(R.id.txtNombreCorporacion);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtBuscar = findViewById(R.id.txtBuscar);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerViewCorporaciones = findViewById(R.id.recyclerViewCorporaciones);
    }

    private void configurarRecyclerView() {
        listaCorporaciones = new ArrayList<>();
        adapter = new CorporacionAdapter(listaCorporaciones, new CorporacionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Corporacion corporacion) {
                seleccionarCorporacion(corporacion);
            }

            @Override
            public void onItemLongClick(Corporacion corporacion) {
                mostrarDialogoOpciones(corporacion);
            }
        });

        recyclerViewCorporaciones.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCorporaciones.setAdapter(adapter);
    }

    private void cargarDatosIniciales() {
        new Thread(() -> {
            // Cargar corporaciones para obtener máximo ID
            List<Corporacion> corporaciones = corporacionDAO.mostrarTodos();
            maxIdCorporacion = 0;
            for (Corporacion c : corporaciones) {
                if (c.idCorporacion > maxIdCorporacion) {
                    maxIdCorporacion = c.idCorporacion;
                }
            }

            runOnUiThread(() -> {
                // Actualizar ID siguiente
                actualizarIdSiguiente();

                // Cargar lista de corporaciones
                cargarCorporaciones();
            });
        }).start();
    }

    private void actualizarIdSiguiente() {
        txtIdCorporacion.setText("ID: " + (maxIdCorporacion + 1));
    }

    private void cargarCorporaciones() {
        new Thread(() -> {
            List<Corporacion> corporaciones = corporacionDAO.mostrarTodos();
            runOnUiThread(() -> {
                listaCorporaciones.clear();
                listaCorporaciones.addAll(corporaciones);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void configurarEventos() {
        btnAgregar.setOnClickListener(v -> agregarCorporacion());
        btnEliminar.setOnClickListener(v -> eliminarCorporacion());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        btnBuscar.setOnClickListener(v -> buscarCorporacion());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void agregarCorporacion() {
        // Validar campos obligatorios
        String nombre = txtNombreCorporacion.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (direccion.isEmpty()) {
            Toast.makeText(this, "La dirección es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar nuevo ID
        int nuevoId = maxIdCorporacion + 1;

        // Crear nueva corporación
        // Nota: El campo 'telefono' no está en el formulario, así que lo dejamos como cadena vacía
        Corporacion nuevaCorporacion = new Corporacion(
                nuevoId,
                nombre,
                direccion,
                ""  // teléfono vacío por defecto
        );

        new Thread(() -> {
            corporacionDAO.agregarCorporacion(nuevaCorporacion);

            runOnUiThread(() -> {
                Toast.makeText(this, "Corporación agregada exitosamente", Toast.LENGTH_SHORT).show();

                // Actualizar máximo ID y limpiar formulario
                maxIdCorporacion = nuevoId;
                limpiarFormulario();

                // Recargar lista
                cargarCorporaciones();
            });
        }).start();
    }

    private void seleccionarCorporacion(Corporacion corporacion) {
        corporacionSeleccionada = corporacion;

        // Cargar datos en el formulario
        txtIdCorporacion.setText("ID: " + corporacion.idCorporacion);
        txtNombreCorporacion.setText(corporacion.nombre);
        txtDireccion.setText(corporacion.direccion);
        // Nota: teléfono no se muestra en el formulario

        // Habilitar botón de eliminación
        btnEliminar.setEnabled(true);
        btnAgregar.setEnabled(false);
    }

    private void eliminarCorporacion() {
        if (corporacionSeleccionada == null) {
            Toast.makeText(this, "Seleccione una corporación para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si hay donadores asociados a esta corporación
        new Thread(() -> {
            // Aquí deberías verificar si hay donadores asociados antes de eliminar
            // Por ahora, solo eliminamos
            boolean puedeEliminar = true; // Cambiar esto según tu lógica de validación

            runOnUiThread(() -> {
                if (!puedeEliminar) {
                    Toast.makeText(this,
                            "No se puede eliminar. Hay donadores asociados a esta corporación.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(GestionCorporacionesActivity.this)
                        .setTitle("Confirmar eliminación")
                        .setMessage("¿Está seguro de eliminar la corporación?\n\n" +
                                corporacionSeleccionada.nombre + "\n" +
                                "ID: " + corporacionSeleccionada.idCorporacion)
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            new Thread(() -> {
                                corporacionDAO.eliminarCorporacion(corporacionSeleccionada);

                                runOnUiThread(() -> {
                                    Toast.makeText(GestionCorporacionesActivity.this,
                                            "Corporación eliminada exitosamente",
                                            Toast.LENGTH_SHORT).show();
                                    limpiarFormulario();
                                    cargarCorporaciones();
                                    corporacionSeleccionada = null;
                                });
                            }).start();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        }).start();
    }

    private void buscarCorporacion() {
        String busqueda = txtBuscar.getText().toString().trim();

        if (busqueda.isEmpty()) {
            cargarCorporaciones();
            return;
        }

        new Thread(() -> {
            List<Corporacion> resultados;

            // Buscar por ID si es numérico
            if (busqueda.matches("\\d+")) {
                try {
                    int id = Integer.parseInt(busqueda);
                    // Buscar directamente por ID
                    resultados = new ArrayList<>();
                    List<Corporacion> todas = corporacionDAO.mostrarTodos();
                    for (Corporacion c : todas) {
                        if (c.idCorporacion == id) {
                            resultados.add(c);
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    resultados = corporacionDAO.buscarPorCoincidencia(busqueda);
                }
            } else {
                // Buscar por nombre
                resultados = corporacionDAO.buscarPorCoincidencia(busqueda);
            }

            List<Corporacion> finalResultados = resultados;
            runOnUiThread(() -> {
                listaCorporaciones.clear();
                listaCorporaciones.addAll(finalResultados);
                adapter.notifyDataSetChanged();

                if (finalResultados.isEmpty()) {
                    Toast.makeText(this, "No se encontraron corporaciones", Toast.LENGTH_SHORT).show();
                } else if (finalResultados.size() == 1) {
                    // Si hay un solo resultado, seleccionarlo automáticamente
                    seleccionarCorporacion(finalResultados.get(0));
                }
            });
        }).start();
    }

    private void mostrarDialogoOpciones(Corporacion corporacion) {
        String[] opciones = {"Seleccionar", "Eliminar", "Ver detalles", "Ver donadores"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones: " + corporacion.nombre)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Seleccionar
                            seleccionarCorporacion(corporacion);
                            break;
                        case 1: // Eliminar
                            corporacionSeleccionada = corporacion;
                            eliminarCorporacion();
                            break;
                        case 2: // Ver detalles
                            mostrarDetallesCorporacion(corporacion);
                            break;
                        case 3: // Ver donadores
                            verDonadoresCorporacion(corporacion);
                            break;
                    }
                })
                .show();
    }

    private void mostrarDetallesCorporacion(Corporacion corporacion) {
        StringBuilder detalles = new StringBuilder();
        detalles.append("ID: ").append(corporacion.idCorporacion).append("\n");
        detalles.append("Nombre: ").append(corporacion.nombre).append("\n");
        detalles.append("Dirección: ").append(corporacion.direccion).append("\n");
        if (corporacion.telefono != null && !corporacion.telefono.isEmpty()) {
            detalles.append("Teléfono: ").append(corporacion.telefono).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Detalles de la Corporación")
                .setMessage(detalles.toString())
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void verDonadoresCorporacion(Corporacion corporacion) {
        // Aquí podrías abrir una Activity para ver los donadores de esta corporación
        Toast.makeText(this,
                "Mostrando donadores de: " + corporacion.nombre,
                Toast.LENGTH_SHORT).show();

        // Ejemplo: Podrías abrir la Activity de donadores con filtro por corporación
        // Intent intent = new Intent(this, GestionDonadoresActivity.class);
        // intent.putExtra("idCorporacion", corporacion.idCorporacion);
        // startActivity(intent);
    }

    private void limpiarFormulario() {
        txtNombreCorporacion.setText("");
        txtDireccion.setText("");
        txtBuscar.setText("");

        // Actualizar ID siguiente
        actualizarIdSiguiente();

        // Resetear selección
        corporacionSeleccionada = null;
        btnEliminar.setEnabled(false);
        btnAgregar.setEnabled(true);

        // Recargar todas las corporaciones
        cargarCorporaciones();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}