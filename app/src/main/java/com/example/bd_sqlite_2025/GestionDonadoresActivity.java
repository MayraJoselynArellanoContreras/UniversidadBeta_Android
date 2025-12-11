package com.example.bd_sqlite_2025;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import controlers.DonadorDAO;
import controlers.CategoriaDonadorDAO;
import controlers.CorporacionDAO;
import db.UniversidadBeta;
import entities.Donador;
import entities.CategoriaDonador;
import entities.Corporacion;
import ReciclerViews.DonadorAdapter;

public class GestionDonadoresActivity extends AppCompatActivity {

    // Views
    private TextView txtIdDonador;
    private EditText txtNombre, txtDireccion, txtTelefono, txtCorreo, txtAnioGraduacion, txtConyuge, txtBuscar;
    private Spinner spinnerCategoria, spinnerCorporacion;
    private LinearLayout layoutAnioGraduacion;
    private Button btnGenerarId, btnAgregar, btnEditar, btnLimpiar, btnBuscar, btnVolver, btnEliminar;
    private RecyclerView recyclerViewDonadores;

    // Base de datos y DAOs
    private UniversidadBeta db;
    private DonadorDAO donadorDAO;
    private CategoriaDonadorDAO categoriaDAO;
    private CorporacionDAO corporacionDAO;

    // Datos
    private List<Donador> listaDonadores;
    private List<CategoriaDonador> listaCategorias;
    private List<Corporacion> listaCorporaciones;
    private DonadorAdapter adapter;
    private Donador donadorSeleccionado = null;
    private int maxIdDonador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donadores);

        // Inicializar base de datos
        db = UniversidadBeta.getAppDatabase(this);
        donadorDAO = db.donadorDAO();
        categoriaDAO = db.categoriaDonadorDAO();
        corporacionDAO = db.corporacionDAO();

        inicializarVistas();
        configurarSpinners();
        configurarRecyclerView();
        cargarDatosIniciales();
        configurarEventos();
    }

    private void inicializarVistas() {
        txtIdDonador = findViewById(R.id.txtIdDonador);
        txtNombre = findViewById(R.id.txtNombre);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtAnioGraduacion = findViewById(R.id.txtAnioGraduacion);
        txtConyuge = findViewById(R.id.txtConyuge);
        txtBuscar = findViewById(R.id.txtBuscar);

        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerCorporacion = findViewById(R.id.spinnerCorporacion);

        layoutAnioGraduacion = findViewById(R.id.layoutAnioGraduacion);

        btnGenerarId = findViewById(R.id.btnGenerarId);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnEditar = findViewById(R.id.btnEditar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnVolver = findViewById(R.id.btnVolver);
        btnEliminar = findViewById(R.id.btnEliminar);

        recyclerViewDonadores = findViewById(R.id.recyclerViewDonadores);
    }

    private void configurarSpinners() {
        // Configurar spinner de categorías
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Mostrar/ocultar año de graduación según la categoría seleccionada
                if (position > 0 && listaCategorias != null && position - 1 < listaCategorias.size()) {
                    CategoriaDonador categoria = listaCategorias.get(position - 1);
                    // Si la categoría es "Alumno" o similar, mostrar año de graduación
                    boolean mostrarAnio = categoria.nombre.toLowerCase().contains("alumno") ||
                            categoria.nombre.toLowerCase().contains("egresado");
                    layoutAnioGraduacion.setVisibility(mostrarAnio ? View.VISIBLE : View.GONE);
                } else {
                    layoutAnioGraduacion.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configurar spinner de corporaciones
        ArrayAdapter<String> corporacionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        corporacionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorporacion.setAdapter(corporacionAdapter);
    }

    private void configurarRecyclerView() {
        listaDonadores = new ArrayList<>();
        adapter = new DonadorAdapter(listaDonadores, new DonadorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Donador donador) {
                seleccionarDonador(donador);
            }

            @Override
            public void onItemLongClick(Donador donador) {
                mostrarDialogoOpciones(donador);
            }
        });

        recyclerViewDonadores.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDonadores.setAdapter(adapter);
    }

    private void cargarDatosIniciales() {
        new Thread(() -> {
            // Cargar categorías para spinner
            listaCategorias = categoriaDAO.mostrarTodos();
            List<String> nombresCategorias = new ArrayList<>();
            nombresCategorias.add("-- Seleccionar Categoría --");
            for (CategoriaDonador c : listaCategorias) {
                nombresCategorias.add(c.nombre);
            }

            // Cargar corporaciones para spinner
            listaCorporaciones = corporacionDAO.mostrarTodos();
            List<String> nombresCorporaciones = new ArrayList<>();
            nombresCorporaciones.add("-- Sin Corporación --");
            for (Corporacion c : listaCorporaciones) {
                nombresCorporaciones.add(c.nombre);
            }

            // Cargar donadores para obtener máximo ID
            List<Donador> donadores = donadorDAO.mostrarTodos();
            maxIdDonador = 0;
            for (Donador d : donadores) {
                if (d.idDonador > maxIdDonador) {
                    maxIdDonador = d.idDonador;
                }
            }

            runOnUiThread(() -> {
                // Actualizar spinners
                ArrayAdapter<String> catAdapter = (ArrayAdapter<String>) spinnerCategoria.getAdapter();
                catAdapter.clear();
                catAdapter.addAll(nombresCategorias);
                catAdapter.notifyDataSetChanged();

                ArrayAdapter<String> corpAdapter = (ArrayAdapter<String>) spinnerCorporacion.getAdapter();
                corpAdapter.clear();
                corpAdapter.addAll(nombresCorporaciones);
                corpAdapter.notifyDataSetChanged();

                // Actualizar ID inicial
                txtIdDonador.setText("Presiona Generar ID");

                // Cargar lista de donadores
                cargarDonadores();
            });
        }).start();
    }

    private void cargarDonadores() {
        new Thread(() -> {
            List<Donador> donadores = donadorDAO.mostrarTodos();
            runOnUiThread(() -> {
                listaDonadores.clear();
                listaDonadores.addAll(donadores);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void configurarEventos() {
        btnGenerarId.setOnClickListener(v -> generarNuevoId());
        btnAgregar.setOnClickListener(v -> agregarDonador());
        btnEditar.setOnClickListener(v -> editarDonador());
        btnEliminar.setOnClickListener(v -> eliminarDonador());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        btnBuscar.setOnClickListener(v -> buscarDonador());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void generarNuevoId() {
        // Generar ID temporal único (podría incluir timestamp)
        String idTemp = "TEMP_" + System.currentTimeMillis() % 10000;

        // Para ID permanente, usar el siguiente ID disponible
        int nuevoId = maxIdDonador + 1;
        txtIdDonador.setText("ID: " + nuevoId + " | Temp: " + idTemp);

        Toast.makeText(this, "ID generado. Complete el formulario y guarde.", Toast.LENGTH_SHORT).show();
    }

    private void agregarDonador() {
        // Validar campos obligatorios
        String nombre = txtNombre.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Nombre, dirección y teléfono son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerCategoria.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener ID de categoría seleccionada
        int categoriaPos = spinnerCategoria.getSelectedItemPosition() - 1;
        Integer idCategoria = (categoriaPos >= 0 && categoriaPos < listaCategorias.size()) ?
                listaCategorias.get(categoriaPos).idCategoria : null;

        // Obtener ID de corporación seleccionada
        Integer idCorporacion = null;
        int corporacionPos = spinnerCorporacion.getSelectedItemPosition() - 1;
        if (corporacionPos >= 0 && corporacionPos < listaCorporaciones.size()) {
            idCorporacion = listaCorporaciones.get(corporacionPos).idCorporacion;
        }

        // Obtener año de graduación si está visible y tiene valor
        Integer anioGraduacion = null;
        if (layoutAnioGraduacion.getVisibility() == View.VISIBLE) {
            String anioStr = txtAnioGraduacion.getText().toString().trim();
            if (!anioStr.isEmpty()) {
                try {
                    anioGraduacion = Integer.parseInt(anioStr);
                    if (anioGraduacion < 1900 || anioGraduacion > 2100) {
                        Toast.makeText(this, "Año de graduación inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Año de graduación debe ser un número", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        // Generar ID permanente
        int nuevoId = maxIdDonador + 1;

        // Extraer ID temporal del TextView si existe
        String idDonadorTemp = null;
        String idText = txtIdDonador.getText().toString();
        if (idText.contains("Temp:")) {
            String[] partes = idText.split("Temp:");
            if (partes.length > 1) {
                idDonadorTemp = partes[1].trim();
            }
        }

        // Crear nuevo donador
        Donador nuevoDonador = new Donador(
                nuevoId,
                nombre,
                direccion,
                telefono,
                txtCorreo.getText().toString().trim(),
                idCategoria,
                anioGraduacion,
                idCorporacion,
                txtConyuge.getText().toString().trim(),
                idDonadorTemp
        );

        new Thread(() -> {
            donadorDAO.insertar(nuevoDonador);

            runOnUiThread(() -> {
                Toast.makeText(this, "Donador agregado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarDonadores();

                // Actualizar máximo ID
                maxIdDonador = nuevoId;
            });
        }).start();
    }

    private void seleccionarDonador(Donador donador) {
        donadorSeleccionado = donador;

        // Cargar datos en el formulario
        txtIdDonador.setText("ID: " + donador.idDonador +
                (donador.idDonadorTemp != null ? " | Temp: " + donador.idDonadorTemp : ""));

        txtNombre.setText(donador.nombre);
        txtDireccion.setText(donador.direccion);
        txtTelefono.setText(donador.telefono);
        txtCorreo.setText(donador.email != null ? donador.email : "");
        txtConyuge.setText(donador.conyuge != null ? donador.conyuge : "");

        // Seleccionar categoría en spinner
        if (donador.idCategoria != null) {
            for (int i = 0; i < listaCategorias.size(); i++) {
                if (listaCategorias.get(i).idCategoria == donador.idCategoria) {
                    spinnerCategoria.setSelection(i + 1); // +1 por el primer elemento placeholder

                    // Mostrar/ocultar año de graduación según categoría
                    CategoriaDonador categoria = listaCategorias.get(i);
                    boolean mostrarAnio = categoria.nombre.toLowerCase().contains("alumno") ||
                            categoria.nombre.toLowerCase().contains("egresado");
                    layoutAnioGraduacion.setVisibility(mostrarAnio ? View.VISIBLE : View.GONE);

                    if (mostrarAnio && donador.anioGraduacion != null) {
                        txtAnioGraduacion.setText(String.valueOf(donador.anioGraduacion));
                    }
                    break;
                }
            }
        }

        // Seleccionar corporación en spinner
        if (donador.idCorporacion != null) {
            for (int i = 0; i < listaCorporaciones.size(); i++) {
                if (listaCorporaciones.get(i).idCorporacion == donador.idCorporacion) {
                    spinnerCorporacion.setSelection(i + 1); // +1 por el primer elemento placeholder
                    break;
                }
            }
        }

        // Habilitar botones de edición y eliminación
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnAgregar.setEnabled(false);
    }

    private void editarDonador() {
        if (donadorSeleccionado == null) {
            Toast.makeText(this, "Seleccione un donador para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar campos obligatorios
        String nombre = txtNombre.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();

        if (nombre.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Nombre, dirección y teléfono son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerCategoria.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener ID de categoría seleccionada
        int categoriaPos = spinnerCategoria.getSelectedItemPosition() - 1;
        Integer idCategoria = (categoriaPos >= 0 && categoriaPos < listaCategorias.size()) ?
                listaCategorias.get(categoriaPos).idCategoria : null;

        // Obtener ID de corporación seleccionada
        Integer idCorporacion = null;
        int corporacionPos = spinnerCorporacion.getSelectedItemPosition() - 1;
        if (corporacionPos >= 0 && corporacionPos < listaCorporaciones.size()) {
            idCorporacion = listaCorporaciones.get(corporacionPos).idCorporacion;
        }

        // Obtener año de graduación si está visible
        Integer anioGraduacion = null;
        if (layoutAnioGraduacion.getVisibility() == View.VISIBLE) {
            String anioStr = txtAnioGraduacion.getText().toString().trim();
            if (!anioStr.isEmpty()) {
                try {
                    anioGraduacion = Integer.parseInt(anioStr);
                } catch (NumberFormatException e) {
                    anioGraduacion = null;
                }
            }
        }

        // Extraer ID temporal si existe
        String idDonadorTemp = null;
        String idText = txtIdDonador.getText().toString();
        if (idText.contains("Temp:")) {
            String[] partes = idText.split("Temp:");
            if (partes.length > 1) {
                idDonadorTemp = partes[1].trim();
            }
        }

        // Actualizar donador
        Donador donadorActualizado = new Donador(
                donadorSeleccionado.idDonador,
                nombre,
                direccion,
                telefono,
                txtCorreo.getText().toString().trim(),
                idCategoria,
                anioGraduacion,
                idCorporacion,
                txtConyuge.getText().toString().trim(),
                idDonadorTemp != null ? idDonadorTemp : donadorSeleccionado.idDonadorTemp
        );

        new Thread(() -> {
            donadorDAO.actualizar(donadorActualizado);

            runOnUiThread(() -> {
                Toast.makeText(this, "Donador actualizado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarDonadores();
                donadorSeleccionado = null;
            });
        }).start();
    }

    private void eliminarDonador() {
        if (donadorSeleccionado == null) {
            Toast.makeText(this, "Seleccione un donador para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar al donador?\n\n" +
                        donadorSeleccionado.nombre + "\n" +
                        "ID: " + donadorSeleccionado.idDonador)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    new Thread(() -> {
                        donadorDAO.eliminar(donadorSeleccionado);

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Donador eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarFormulario();
                            cargarDonadores();
                            donadorSeleccionado = null;
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void buscarDonador() {
        String busqueda = txtBuscar.getText().toString().trim();

        if (busqueda.isEmpty()) {
            cargarDonadores();
            return;
        }

        new Thread(() -> {
            List<Donador> resultados;

            // Buscar por ID si es numérico
            if (busqueda.matches("\\d+")) {
                try {
                    int id = Integer.parseInt(busqueda);
                    Donador donador = donadorDAO.obtenerPorId(id);
                    resultados = new ArrayList<>();
                    if (donador != null) {
                        resultados.add(donador);
                    }
                } catch (NumberFormatException e) {
                    resultados = donadorDAO.buscarPorCoincidencia(busqueda);
                }
            } else {
                // Buscar por nombre, teléfono, etc.
                resultados = donadorDAO.buscarPorCoincidencia(busqueda);
            }

            List<Donador> finalResultados = resultados;
            runOnUiThread(() -> {
                listaDonadores.clear();
                listaDonadores.addAll(finalResultados);
                adapter.notifyDataSetChanged();

                if (finalResultados.isEmpty()) {
                    Toast.makeText(this, "No se encontraron donadores", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void mostrarDialogoOpciones(Donador donador) {
        String[] opciones = {"Editar", "Eliminar", "Ver detalles", "Ver donativos"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones: " + donador.nombre)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            seleccionarDonador(donador);
                            break;
                        case 1: // Eliminar
                            donadorSeleccionado = donador;
                            eliminarDonador();
                            break;
                        case 2: // Ver detalles
                            mostrarDetallesDonador(donador);
                            break;
                        case 3: // Ver donativos
                            verDonativosDonador(donador);
                            break;
                    }
                })
                .show();
    }

    private void mostrarDetallesDonador(Donador donador) {
        // Obtener nombres de categoría y corporación
        String nombreCategoria = "No asignada";
        String nombreCorporacion = "No asignada";

        if (donador.idCategoria != null) {
            for (CategoriaDonador c : listaCategorias) {
                if (c.idCategoria == donador.idCategoria) {
                    nombreCategoria = c.nombre;
                    break;
                }
            }
        }

        if (donador.idCorporacion != null) {
            for (Corporacion c : listaCorporaciones) {
                if (c.idCorporacion == donador.idCorporacion) {
                    nombreCorporacion = c.nombre;
                    break;
                }
            }
        }

        StringBuilder detalles = new StringBuilder();
        detalles.append("ID: ").append(donador.idDonador).append("\n");
        if (donador.idDonadorTemp != null) {
            detalles.append("ID Temporal: ").append(donador.idDonadorTemp).append("\n");
        }
        detalles.append("Nombre: ").append(donador.nombre).append("\n");
        detalles.append("Dirección: ").append(donador.direccion).append("\n");
        detalles.append("Teléfono: ").append(donador.telefono).append("\n");
        if (donador.email != null && !donador.email.isEmpty()) {
            detalles.append("Correo: ").append(donador.email).append("\n");
        }
        detalles.append("Categoría: ").append(nombreCategoria).append("\n");
        if (donador.anioGraduacion != null) {
            detalles.append("Año Graduación: ").append(donador.anioGraduacion).append("\n");
        }
        detalles.append("Corporación: ").append(nombreCorporacion).append("\n");
        if (donador.conyuge != null && !donador.conyuge.isEmpty()) {
            detalles.append("Cónyuge: ").append(donador.conyuge).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Detalles del Donador")
                .setMessage(detalles.toString())
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void verDonativosDonador(Donador donador) {
        // Aquí podrías abrir una Activity para ver los donativos de este donador
        Toast.makeText(this, "Funcionalidad de donativos en desarrollo", Toast.LENGTH_SHORT).show();
    }

    private void limpiarFormulario() {
        txtIdDonador.setText("Presiona Generar ID");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtAnioGraduacion.setText("");
        txtConyuge.setText("");
        txtBuscar.setText("");

        spinnerCategoria.setSelection(0);
        spinnerCorporacion.setSelection(0);
        layoutAnioGraduacion.setVisibility(View.GONE);

        donadorSeleccionado = null;
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnAgregar.setEnabled(true);

        // Recargar todos los donadores
        cargarDonadores();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}