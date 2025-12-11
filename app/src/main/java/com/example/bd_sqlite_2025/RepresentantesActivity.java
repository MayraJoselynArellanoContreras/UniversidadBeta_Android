package com.example.bd_sqlite_2025;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import controlers.RepresentanteClaseDAO;
import db.UniversidadBeta;
import entities.RepresentanteClase;
import ReciclerViews.RepresentanteAdapter;

public class RepresentantesActivity extends AppCompatActivity {

    // Views
    private EditText txtIdRepresentante, txtNombre, txtTelefono, txtEmail;
    private Spinner comboGeneracion;
    private Button btnAgregar, btnEditar, btnEliminar, btnBuscar, btnLimpiar, btnVolver;
    private RecyclerView recyclerViewRepresentantes;

    // Base de datos y DAO
    private UniversidadBeta db;
    private RepresentanteClaseDAO representanteDAO;

    // Datos
    private List<RepresentanteClase> listaRepresentantes;
    private RepresentanteAdapter adapter;
    private RepresentanteClase representanteSeleccionado = null;
    private int maxIdRepresentante = 0;

    // Arrays para spinner
    private List<Integer> generaciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representantes);

        // Inicializar base de datos
        db = UniversidadBeta.getAppDatabase(this);
        representanteDAO = db.representanteClaseDAO();

        inicializarVistas();
        configurarSpinner();
        configurarRecyclerView();
        cargarDatosIniciales();
        configurarEventos();
    }

    private void inicializarVistas() {
        txtIdRepresentante = findViewById(R.id.txtIdRepresentante);
        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtEmail = findViewById(R.id.txtEmail);

        comboGeneracion = findViewById(R.id.comboGeneracion);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerViewRepresentantes = findViewById(R.id.recyclerViewRepresentantes);
    }

    private void configurarSpinner() {
        // Inicializar lista de generaciones
        generaciones.add(0); // "Seleccione generación..."

        // Agregar años desde 1990 hasta el actual
        int añoActual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = añoActual; i >= 1990; i--) {
            generaciones.add(i);
        }

        // Crear adapter para spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getGeneracionStrings());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboGeneracion.setAdapter(adapter);
    }

    private List<String> getGeneracionStrings() {
        List<String> strings = new ArrayList<>();
        for (Integer año : generaciones) {
            if (año == 0) {
                strings.add("Seleccione generación...");
            } else {
                strings.add("Generación " + año);
            }
        }
        return strings;
    }

    private void configurarRecyclerView() {
        listaRepresentantes = new ArrayList<>();
        adapter = new RepresentanteAdapter(listaRepresentantes, new RepresentanteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RepresentanteClase representante) {
                seleccionarRepresentante(representante);
            }

            @Override
            public void onItemLongClick(RepresentanteClase representante) {
                mostrarDialogoOpciones(representante);
            }
        });

        recyclerViewRepresentantes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRepresentantes.setAdapter(adapter);
    }

    private void cargarDatosIniciales() {
        new Thread(() -> {
            // Cargar representantes para obtener máximo ID
            List<RepresentanteClase> representantes = representanteDAO.mostrarTodos();
            maxIdRepresentante = 0;
            for (RepresentanteClase r : representantes) {
                if (r.idRepresentante > maxIdRepresentante) {
                    maxIdRepresentante = r.idRepresentante;
                }
            }

            runOnUiThread(() -> {
                // Actualizar ID siguiente
                txtIdRepresentante.setText(String.valueOf(maxIdRepresentante + 1));

                // Cargar lista de representantes
                cargarRepresentantes();
            });
        }).start();
    }

    private void cargarRepresentantes() {
        new Thread(() -> {
            List<RepresentanteClase> representantes = representanteDAO.mostrarTodos();
            runOnUiThread(() -> {
                listaRepresentantes.clear();
                listaRepresentantes.addAll(representantes);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void configurarEventos() {
        btnAgregar.setOnClickListener(v -> agregarRepresentante());
        btnEditar.setOnClickListener(v -> editarRepresentante());
        btnEliminar.setOnClickListener(v -> eliminarRepresentante());
        btnBuscar.setOnClickListener(v -> buscarRepresentante());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void agregarRepresentante() {
        if (!validarCamposObligatorios()) {
            return;
        }

        // Obtener datos del formulario
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();

        int generacionIndex = comboGeneracion.getSelectedItemPosition();
        if (generacionIndex == 0) {
            Toast.makeText(this, "Seleccione una generación", Toast.LENGTH_SHORT).show();
            return;
        }
        int generacion = generaciones.get(generacionIndex);

        // Crear nuevo representante
        RepresentanteClase nuevoRepresentante = new RepresentanteClase(
                maxIdRepresentante + 1,
                nombre,
                email.isEmpty() ? null : email,
                telefono.isEmpty() ? null : telefono
        );

        new Thread(() -> {
            representanteDAO.agregarRepresentante(nuevoRepresentante);

            runOnUiThread(() -> {
                Toast.makeText(this, "Representante agregado exitosamente", Toast.LENGTH_SHORT).show();

                // Actualizar máximo ID y limpiar formulario
                maxIdRepresentante++;
                limpiarFormulario();

                // Recargar lista
                cargarRepresentantes();
            });
        }).start();
    }

    private boolean validarCamposObligatorios() {
        // Validar nombre
        if (txtNombre.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar teléfono
        String telefono = txtTelefono.getText().toString().trim();
        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar email si está presente
        String email = txtEmail.getText().toString().trim();
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void seleccionarRepresentante(RepresentanteClase representante) {
        representanteSeleccionado = representante;

        // Cargar datos en el formulario
        txtIdRepresentante.setText(String.valueOf(representante.idRepresentante));
        txtNombre.setText(representante.nombre);
        txtTelefono.setText(representante.telefono != null ? representante.telefono : "");
        txtEmail.setText(representante.email != null ? representante.email : "");

        // Seleccionar generación en spinner (nota: tu entidad no tiene campo generación)
        // comboGeneracion.setSelection(obtenerPosicionGeneracion(representante.clase));

        // Habilitar botones de edición y eliminación
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnAgregar.setEnabled(false);
    }

    private void editarRepresentante() {
        if (representanteSeleccionado == null) {
            Toast.makeText(this, "Seleccione un representante para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCamposObligatorios()) {
            return;
        }

        // Obtener datos actualizados
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();

        // Crear representante actualizado
        RepresentanteClase representanteActualizado = new RepresentanteClase(
                representanteSeleccionado.idRepresentante,
                nombre,
                email.isEmpty() ? null : email,
                telefono.isEmpty() ? null : telefono
        );

        new Thread(() -> {
            representanteDAO.actualizarRepresentante(representanteActualizado);

            runOnUiThread(() -> {
                Toast.makeText(this, "Representante actualizado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarRepresentantes();
                representanteSeleccionado = null;
            });
        }).start();
    }

    private void eliminarRepresentante() {
        if (representanteSeleccionado == null) {
            Toast.makeText(this, "Seleccione un representante para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar al representante?\n\n" +
                        representanteSeleccionado.nombre + "\n" +
                        "ID: " + representanteSeleccionado.idRepresentante)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    new Thread(() -> {
                        representanteDAO.eliminarRepresentante(representanteSeleccionado);

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Representante eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarFormulario();
                            cargarRepresentantes();
                            representanteSeleccionado = null;
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void buscarRepresentante() {
        String idStr = txtIdRepresentante.getText().toString().trim();

        if (idStr.isEmpty()) {
            // Buscar por ID ingresado manualmente
            new AlertDialog.Builder(this)
                    .setTitle("Buscar Representante")
                    .setMessage("Ingrese ID del representante:")
                    .setView(new EditText(this))
                    .setPositiveButton("Buscar", (dialog, which) -> {
                        EditText input = ((AlertDialog) dialog).findViewById(android.R.id.input);
                        if (input != null) {
                            buscarRepresentantePorID(input.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            // Buscar por ID actual en el formulario
            buscarRepresentantePorID(idStr);
        }
    }

    private void buscarRepresentantePorID(String idStr) {
        try {
            int id = Integer.parseInt(idStr);

            new Thread(() -> {
                List<RepresentanteClase> resultados = representanteDAO.mostrarTodos();
                RepresentanteClase encontrado = resultados.stream().filter(r -> r.idRepresentante == id).findFirst().orElse(null);

                runOnUiThread(() -> {
                    if (encontrado != null) {
                        seleccionarRepresentante(encontrado);
                        Toast.makeText(this, "Representante encontrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No se encontró representante con ID: " + id,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID inválido. Debe ser un número", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoOpciones(RepresentanteClase representante) {
        String[] opciones = {"Editar", "Eliminar", "Ver detalles"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones: " + representante.nombre)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            seleccionarRepresentante(representante);
                            break;
                        case 1: // Eliminar
                            representanteSeleccionado = representante;
                            eliminarRepresentante();
                            break;
                        case 2: // Ver detalles
                            mostrarDetallesRepresentante(representante);
                            break;
                    }
                })
                .show();
    }

    private void mostrarDetallesRepresentante(RepresentanteClase representante) {
        StringBuilder detalles = new StringBuilder();
        detalles.append("ID: ").append(representante.idRepresentante).append("\n");
        detalles.append("Nombre: ").append(representante.nombre).append("\n");
        if (representante.telefono != null) {
            detalles.append("Teléfono: ").append(representante.telefono).append("\n");
        }
        if (representante.email != null) {
            detalles.append("Email: ").append(representante.email).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Detalles del Representante")
                .setMessage(detalles.toString())
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void limpiarFormulario() {
        txtIdRepresentante.setText(String.valueOf(maxIdRepresentante + 1));
        txtNombre.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        comboGeneracion.setSelection(0);

        representanteSeleccionado = null;
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnAgregar.setEnabled(true);

        // Deseleccionar RecyclerView
        adapter.clearSelection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}