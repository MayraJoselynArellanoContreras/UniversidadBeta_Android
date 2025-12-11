package com.example.bd_sqlite_2025;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import db.UniversidadBeta;
import entities.Evento;
import controlers.EventoDAO;

public class EventosActivity extends AppCompatActivity {

    private EditText edtNombreEvento, edtFecha, edtLugar, edtMetaRecaudacion, edtDescripcion;
    private Spinner spnTipoEvento;
    private Button btnAgregar, btnEditar, btnEliminar, btnBuscar, btnLimpiar, btnVolver;
    private RecyclerView recyclerEventos;

    private int eventoSeleccionadoId = -1;
    private List<String> tiposEvento;
    private ArrayAdapter<String> spinnerAdapter;
    private EventosAdapter eventosAdapter;

    // Base de datos
    private UniversidadBeta db;
    private EventoDAO eventoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);


        db = UniversidadBeta.getAppDatabase(this);
        eventoDAO = db.eventoDAO();

        inicializarVistas();

        cargarTiposEvento();


        cargarEventosDesdeBD();
        configurarListeners();

        limpiarCampos();
    }

    private void inicializarVistas() {

        edtNombreEvento = findViewById(R.id.edtNombreEvento);
        edtFecha = findViewById(R.id.edtFecha);
        edtLugar = findViewById(R.id.edtLugar);
        edtMetaRecaudacion = findViewById(R.id.edtMetaRecaudacion);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        spnTipoEvento = findViewById(R.id.spnTipoEvento);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerEventos = findViewById(R.id.recyclerEventos);
        recyclerEventos.setLayoutManager(new LinearLayoutManager(this));


    }

    private void cargarTiposEvento() {
        tiposEvento = new ArrayList<>();
        tiposEvento.add("Seleccione tipo...");
        tiposEvento.add("Carnaval");
        tiposEvento.add("Cena Baile");
        tiposEvento.add("Torneo de Golf");
        tiposEvento.add("Concierto");
        tiposEvento.add("Subasta");
        tiposEvento.add("Maratón");

        spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tiposEvento);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoEvento.setAdapter(spinnerAdapter);


        spnTipoEvento.setSelection(0);
    }

    private void cargarEventosDesdeBD() {
        new Thread(() -> {
            List<Evento> eventos = eventoDAO.mostrarTodos();
            runOnUiThread(() -> {
                eventosAdapter = new EventosAdapter(eventos);
                recyclerEventos.setAdapter(eventosAdapter);
            });
        }).start();
    }

    private void configurarListeners() {
        btnAgregar.setOnClickListener(v -> agregarEvento());
        btnEditar.setOnClickListener(v -> editarEvento());
        btnEliminar.setOnClickListener(v -> eliminarEvento());
        btnBuscar.setOnClickListener(v -> buscarEvento());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
        btnVolver.setOnClickListener(v -> finish());
    }

    private boolean validarCampos() {
        if (edtNombreEvento.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Nombre del evento es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spnTipoEvento.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un tipo de evento", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtFecha.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fecha es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (edtLugar.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Lugar es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        String metaStr = edtMetaRecaudacion.getText().toString().trim();
        if (metaStr.isEmpty()) {
            Toast.makeText(this, "Meta de recaudación es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double meta = Double.parseDouble(metaStr);
            if (meta <= 0) {
                Toast.makeText(this, "Meta debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Meta debe ser un número válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void agregarEvento() {
        if (!validarCampos()) return;

        String nombre = edtNombreEvento.getText().toString().trim();
        String tipo = spnTipoEvento.getSelectedItem().toString();
        String fecha = edtFecha.getText().toString().trim();
        String lugar = edtLugar.getText().toString().trim();
        double meta = Double.parseDouble(edtMetaRecaudacion.getText().toString().trim());
        String descripcion = edtDescripcion.getText().toString().trim();

        new Thread(() -> {
            Evento nuevoEvento = new Evento(nombre, descripcion, fecha, lugar, tipo, meta);
            eventoDAO.agregarEvento(nuevoEvento);

            runOnUiThread(() -> {
                Toast.makeText(this, "Evento agregado", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                cargarEventosDesdeBD();
            });
        }).start();
    }

    private void editarEvento() {
        if (eventoSeleccionadoId == -1) {
            Toast.makeText(this, "Seleccione un evento primero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarCampos()) return;

        String nombre = edtNombreEvento.getText().toString().trim();
        String tipo = spnTipoEvento.getSelectedItem().toString();
        String fecha = edtFecha.getText().toString().trim();
        String lugar = edtLugar.getText().toString().trim();
        double meta = Double.parseDouble(edtMetaRecaudacion.getText().toString().trim());
        String descripcion = edtDescripcion.getText().toString().trim();

        new Thread(() -> {
            Evento evento = eventoDAO.obtenerPorId(eventoSeleccionadoId);
            if (evento != null) {
                evento.nombre = nombre;
                evento.tipo = tipo;
                evento.fecha = fecha;
                evento.lugar = lugar;
                evento.metaRecaudacion = meta;
                evento.descripcion = descripcion;

                eventoDAO.actualizarEvento(evento);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Evento actualizado", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                    cargarEventosDesdeBD();
                });
            }
        }).start();
    }

    private void eliminarEvento() {
        if (eventoSeleccionadoId == -1) {
            Toast.makeText(this, "Seleccione un evento primero", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Evento")
                .setMessage("¿Está seguro de eliminar este evento?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    new Thread(() -> {
                        eventoDAO.eliminarEventoPorId(eventoSeleccionadoId);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Evento eliminado", Toast.LENGTH_SHORT).show();
                            limpiarCampos();
                            cargarEventosDesdeBD();
                        });
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void buscarEvento() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buscar Evento");

        EditText input = new EditText(this);
        input.setHint("Nombre del evento");
        builder.setView(input);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String criterio = input.getText().toString().trim();
            if (!criterio.isEmpty()) {
                new Thread(() -> {
                    List<Evento> resultados = eventoDAO.buscarPorCoincidencia(criterio);
                    runOnUiThread(() -> {
                        if (resultados.isEmpty()) {
                            Toast.makeText(this, "No se encontraron eventos", Toast.LENGTH_SHORT).show();
                        } else {
                            eventosAdapter.actualizarLista(resultados);
                            Toast.makeText(this, resultados.size() + " eventos encontrados", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void limpiarCampos() {

        if (edtNombreEvento != null) edtNombreEvento.setText("");
        if (edtFecha != null) edtFecha.setText("");
        if (edtLugar != null) edtLugar.setText("");
        if (edtMetaRecaudacion != null) edtMetaRecaudacion.setText("");
        if (edtDescripcion != null) edtDescripcion.setText("");
        if (spnTipoEvento != null) spnTipoEvento.setSelection(0);

        eventoSeleccionadoId = -1;
        if (btnAgregar != null) btnAgregar.setEnabled(true);
        if (btnEditar != null) btnEditar.setEnabled(false);
        if (btnEliminar != null) btnEliminar.setEnabled(false);
    }


    class EventosAdapter extends RecyclerView.Adapter<EventosAdapter.EventoViewHolder> {
        private List<Evento> eventos;

        EventosAdapter(List<Evento> eventos) {
            this.eventos = eventos;
        }

        void actualizarLista(List<Evento> nuevosEventos) {
            this.eventos = nuevosEventos;
            notifyDataSetChanged();
        }

        @Override
        public EventoViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_evento, parent, false);
            return new EventoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(EventoViewHolder holder, int position) {
            Evento evento = eventos.get(position);
            holder.txtNombre.setText(evento.nombre);
            holder.txtFecha.setText(evento.fecha);
            holder.txtTipo.setText(evento.tipo);
            holder.txtLugar.setText(evento.lugar);
            holder.txtMeta.setText("$" + evento.metaRecaudacion);

            holder.itemView.setOnClickListener(v -> {
                eventoSeleccionadoId = evento.idEvento;
                cargarEventoEnFormulario(evento);
            });
        }

        @Override
        public int getItemCount() {
            return eventos != null ? eventos.size() : 0;
        }

        class EventoViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView txtNombre, txtFecha, txtTipo, txtLugar, txtMeta;

            EventoViewHolder(View itemView) {
                super(itemView);
                txtNombre = itemView.findViewById(R.id.txtNombre);
                txtFecha = itemView.findViewById(R.id.txtFecha);
                txtTipo = itemView.findViewById(R.id.txtTipo);
                txtLugar = itemView.findViewById(R.id.txtLugar);
                txtMeta = itemView.findViewById(R.id.txtMeta);
            }
        }
    }

    private void cargarEventoEnFormulario(Evento evento) {
        eventoSeleccionadoId = evento.idEvento;
        edtNombreEvento.setText(evento.nombre);
        edtFecha.setText(evento.fecha);
        edtLugar.setText(evento.lugar);
        edtMetaRecaudacion.setText(String.valueOf(evento.metaRecaudacion));
        edtDescripcion.setText(evento.descripcion);

        // Seleccionar tipo en spinner
        for (int i = 0; i < tiposEvento.size(); i++) {
            if (tiposEvento.get(i).equals(evento.tipo)) {
                spnTipoEvento.setSelection(i);
                break;
            }
        }

        btnAgregar.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

}