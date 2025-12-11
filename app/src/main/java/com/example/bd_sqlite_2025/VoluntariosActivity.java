package com.example.bd_sqlite_2025;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import db.UniversidadBeta;
import entities.Voluntario;
import controlers.VoluntarioDAO;

public class VoluntariosActivity extends AppCompatActivity {

    // 1. DECLARACIÓN DE VARIABLES
    private EditText edtNombre, edtTelefono, edtEmail, edtDireccion, edtFechaRegistro, edtObservaciones;
    private CheckBox chkEstudiante, chkActivo;
    private Button btnAgregar, btnEditar, btnEliminar, btnBuscar, btnLimpiar, btnVolver;
    private RecyclerView recyclerVoluntarios;

    // Variables para datos
    private int voluntarioSeleccionadoId = -1;
    private List<Voluntario> listaVoluntarios = new ArrayList<>();
    private VoluntariosAdapter voluntariosAdapter;

    // Base de datos
    private UniversidadBeta db;
    private VoluntarioDAO voluntarioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voluntarios);

        // 1. Base de datos
        db = UniversidadBeta.getAppDatabase(getApplicationContext());
        voluntarioDAO = db.voluntarioDAO();

        // 2. Inicializar TODAS las vistas
        inicializarVistas();

        // 3. Configurar RecyclerView
        configurarRecyclerView();

        // 4. Cargar datos y configurar
        cargarVoluntariosDesdeBD();
        configurarFechaActual();
        configurarListeners();

        // 5. Limpiar campos inicialmente
        limpiarCampos();
    }

    private void inicializarVistas() {
        // TODOS LOS findViewById PRIMERO
        edtNombre = findViewById(R.id.edtNombre);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtEmail = findViewById(R.id.edtEmail);
        edtDireccion = findViewById(R.id.edtDireccion);
        edtFechaRegistro = findViewById(R.id.edtFechaRegistro);
        edtObservaciones = findViewById(R.id.edtObservaciones);
        chkEstudiante = findViewById(R.id.chkEstudiante);
        chkActivo = findViewById(R.id.chkActivo);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerVoluntarios = findViewById(R.id.recyclerVoluntarios);
        recyclerVoluntarios.setLayoutManager(new LinearLayoutManager(this));
    }

    private void configurarRecyclerView() {
        voluntariosAdapter = new VoluntariosAdapter(listaVoluntarios);
        recyclerVoluntarios.setAdapter(voluntariosAdapter);
    }

    private void configurarFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = sdf.format(new Date());
        edtFechaRegistro.setText(fechaActual);
    }

    private void configurarListeners() {
        btnAgregar.setOnClickListener(v -> agregarVoluntario());
        btnEditar.setOnClickListener(v -> editarVoluntario());
        btnEliminar.setOnClickListener(v -> eliminarVoluntario());
        btnBuscar.setOnClickListener(v -> buscarVoluntario());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
        btnVolver.setOnClickListener(v -> finish());

        // Click en campo de fecha
        edtFechaRegistro.setOnClickListener(v -> {
            // Aquí podrías implementar un DatePickerDialog
            Toast.makeText(this, "Formato: YYYY-MM-DD", Toast.LENGTH_SHORT).show();
        });
    }

    private void cargarVoluntariosDesdeBD() {
        new Thread(() -> {
            List<Voluntario> voluntarios = voluntarioDAO.mostrarTodos();
            runOnUiThread(() -> {
                listaVoluntarios.clear();
                listaVoluntarios.addAll(voluntarios);
                voluntariosAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private boolean validarCampos() {
        String nombre = edtNombre.getText().toString().trim();
        String telefono = edtTelefono.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String direccion = edtDireccion.getText().toString().trim();
        String fecha = edtFechaRegistro.getText().toString().trim();

        // Validar nombre
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar teléfono
        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        String soloNumeros = telefono.replaceAll("[^0-9]", "");
        if (soloNumeros.length() < 8) {
            Toast.makeText(this, "Teléfono debe tener al menos 8 dígitos", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar email (opcional pero si se ingresa debe ser válido)
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Formato de email inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar dirección
        if (direccion.isEmpty()) {
            Toast.makeText(this, "La dirección es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar fecha
        if (fecha.isEmpty()) {
            Toast.makeText(this, "La fecha de registro es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar formato de fecha YYYY-MM-DD
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(this, "Formato de fecha inválido (YYYY-MM-DD)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void agregarVoluntario() {
        if (!validarCampos()) return;

        String nombre = edtNombre.getText().toString().trim();
        String telefono = edtTelefono.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String direccion = edtDireccion.getText().toString().trim();
        String fechaRegistro = edtFechaRegistro.getText().toString().trim();
        boolean estudiante = chkEstudiante.isChecked();
        boolean activo = chkActivo.isChecked();
        String observaciones = edtObservaciones.getText().toString().trim();

        // Limpiar teléfono (solo números)
        String telefonoLimpio = telefono.replaceAll("[^0-9]", "");

        new Thread(() -> {
            Voluntario nuevoVoluntario = new Voluntario(
                    0,
                    nombre,
                    email.isEmpty() ? null : email,
                    telefonoLimpio,
                    direccion,
                    fechaRegistro,
                    estudiante,
                    activo,
                    observaciones.isEmpty() ? null : observaciones
            );

            voluntarioDAO.agregarVoluntario(nuevoVoluntario);

            runOnUiThread(() -> {
                Toast.makeText(this, "Voluntario agregado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                cargarVoluntariosDesdeBD();
            });
        }).start();
    }

    private void editarVoluntario() {
        if (voluntarioSeleccionadoId == -1) {
            Toast.makeText(this, "Seleccione un voluntario primero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCampos()) return;

        String nombre = edtNombre.getText().toString().trim();
        String telefono = edtTelefono.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String direccion = edtDireccion.getText().toString().trim();
        String fechaRegistro = edtFechaRegistro.getText().toString().trim();
        boolean estudiante = chkEstudiante.isChecked();
        boolean activo = chkActivo.isChecked();
        String observaciones = edtObservaciones.getText().toString().trim();

        // Limpiar teléfono (solo números)
        String telefonoLimpio = telefono.replaceAll("[^0-9]", "");

        new Thread(() -> {
            Voluntario voluntario = voluntarioDAO.buscarPorId(voluntarioSeleccionadoId);
            if (voluntario != null) {
                voluntario.nombre = nombre;
                voluntario.email = email.isEmpty() ? null : email;
                voluntario.telefono = telefonoLimpio;
                voluntario.direccion = direccion;
                voluntario.fechaRegistro = fechaRegistro;
                voluntario.estudiante = estudiante;
                voluntario.activo = activo;
                voluntario.observaciones = observaciones.isEmpty() ? null : observaciones;

                voluntarioDAO.actualizarVoluntario(voluntario);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Voluntario actualizado exitosamente", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    cargarVoluntariosDesdeBD();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Voluntario no encontrado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                });
            }
        }).start();
    }

    private void eliminarVoluntario() {
        if (voluntarioSeleccionadoId == -1) {
            Toast.makeText(this, "Seleccione un voluntario primero", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Voluntario")
                .setMessage("¿Está seguro de eliminar este voluntario?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    new Thread(() -> {
                        voluntarioDAO.eliminarVoluntarioPorId(voluntarioSeleccionadoId);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Voluntario eliminado", Toast.LENGTH_SHORT).show();
                            limpiarCampos();
                            cargarVoluntariosDesdeBD();
                        });
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void buscarVoluntario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buscar Voluntario");

        EditText input = new EditText(this);
        input.setHint("Nombre o teléfono");
        builder.setView(input);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String criterio = input.getText().toString().trim();
            if (!criterio.isEmpty()) {
                new Thread(() -> {
                    List<Voluntario> resultados = voluntarioDAO.buscarPorCoincidencia(criterio);
                    runOnUiThread(() -> {
                        if (resultados.isEmpty()) {
                            Toast.makeText(this, "No se encontraron voluntarios", Toast.LENGTH_SHORT).show();
                            // Volver a mostrar todos
                            cargarVoluntariosDesdeBD();
                        } else {
                            listaVoluntarios.clear();
                            listaVoluntarios.addAll(resultados);
                            voluntariosAdapter.notifyDataSetChanged();
                            Toast.makeText(this, resultados.size() + " voluntarios encontrados", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void limpiarCampos() {
        if (edtNombre != null) edtNombre.setText("");
        if (edtTelefono != null) edtTelefono.setText("");
        if (edtEmail != null) edtEmail.setText("");
        if (edtDireccion != null) edtDireccion.setText("");
        if (edtObservaciones != null) edtObservaciones.setText("");

        if (chkEstudiante != null) chkEstudiante.setChecked(false);
        if (chkActivo != null) chkActivo.setChecked(true);

        // Restaurar fecha actual
        configurarFechaActual();

        voluntarioSeleccionadoId = -1;
        if (btnAgregar != null) btnAgregar.setEnabled(true);
        if (btnEditar != null) btnEditar.setEnabled(false);
        if (btnEliminar != null) btnEliminar.setEnabled(false);
    }

    private void cargarVoluntarioEnFormulario(Voluntario voluntario) {
        voluntarioSeleccionadoId = voluntario.idVoluntario;

        edtNombre.setText(voluntario.nombre);
        edtTelefono.setText(voluntario.telefono != null ? voluntario.telefono : "");
        edtEmail.setText(voluntario.email != null ? voluntario.email : "");
        edtDireccion.setText(voluntario.direccion != null ? voluntario.direccion : "");
        edtFechaRegistro.setText(voluntario.fechaRegistro != null ? voluntario.fechaRegistro : "");
        edtObservaciones.setText(voluntario.observaciones != null ? voluntario.observaciones : "");

        chkEstudiante.setChecked(voluntario.estudiante);
        chkActivo.setChecked(voluntario.activo);

        btnAgregar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    class VoluntariosAdapter extends RecyclerView.Adapter<VoluntariosAdapter.VoluntarioViewHolder> {
        private List<Voluntario> voluntarios;

        VoluntariosAdapter(List<Voluntario> voluntarios) {
            this.voluntarios = voluntarios;
        }

        void actualizarLista(List<Voluntario> nuevosVoluntarios) {
            this.voluntarios = nuevosVoluntarios;
            notifyDataSetChanged();
        }

        @Override
        public VoluntarioViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_voluntarios, parent, false);
            return new VoluntarioViewHolder(view);
        }

        @Override
        public void onBindViewHolder(VoluntarioViewHolder holder, int position) {
            Voluntario voluntario = voluntarios.get(position);

            holder.txtNombre.setText(voluntario.nombre);
            holder.txtTelefono.setText(voluntario.telefono != null ? voluntario.telefono : "Sin teléfono");

            // Estado
            String estado = voluntario.activo ? "Activo" : "Inactivo";
            int colorEstado = voluntario.activo ? 0xFF4CAF50 : 0xFFF44336; // Verde/Rojo
            holder.txtEstado.setText(estado);
            holder.txtEstado.setTextColor(colorEstado);

            // Estudiante
            String tipo = voluntario.estudiante ? "Estudiante" : "Voluntario";
            holder.txtTipo.setText(tipo);

            holder.itemView.setOnClickListener(v -> {
                cargarVoluntarioEnFormulario(voluntario);
            });
        }

        @Override
        public int getItemCount() {
            return voluntarios != null ? voluntarios.size() : 0;
        }

        class VoluntarioViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView txtNombre, txtTelefono, txtEstado, txtTipo;

            VoluntarioViewHolder(View itemView) {
                super(itemView);
                txtNombre = itemView.findViewById(R.id.txtNombre);
                txtTelefono = itemView.findViewById(R.id.txtTelefono);
                txtEstado = itemView.findViewById(R.id.txtEstado);
                txtTipo = itemView.findViewById(R.id.txtTipo);
            }
        }
    }
}