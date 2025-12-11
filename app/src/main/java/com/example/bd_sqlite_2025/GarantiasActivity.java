package com.example.bd_sqlite_2025;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import controlers.GarantiaDAO;
import controlers.DonadorDAO;
import controlers.CorporacionDAO;
import db.UniversidadBeta;
import entities.Garantia;
import entities.Donador;
import entities.Corporacion;
import ReciclerViews.GarantiaAdapter;

public class GarantiasActivity extends AppCompatActivity {

    // Views del formulario
    private TextView txtIdGarantia;
    private EditText txtBuscarDonador, txtFechaGarantia, txtCantidadGarantizada;
    private EditText txtCantidadEnviada, txtNumTarjeta, txtDireccionCorporacion;
    private TextView txtNombreDonador, txtIdDonador;
    private Spinner spinnerMetodoPago, spinnerNumeroPagos, spinnerCorporacion;
    private Button btnBuscarDonador, btnRegistrarGarantia, btnLimpiar, btnVolver;
    private Button btnBuscarGarantia, btnEditarGarantia, btnEliminarGarantia;
    private RecyclerView recyclerViewGarantias;

    // Base de datos y DAOs
    private UniversidadBeta db;
    private GarantiaDAO garantiaDAO;
    private DonadorDAO donadorDAO;
    private CorporacionDAO corporacionDAO;

    // Datos
    private List<Garantia> listaGarantias;
    private List<Donador> listaDonadores;
    private List<Corporacion> listaCorporaciones;
    private GarantiaAdapter adapter;
    private Garantia garantiaSeleccionada = null;
    private Donador donadorSeleccionado = null;
    private Corporacion corporacionSeleccionada = null;
    private int maxIdGarantia = 0;

    // Arrays para spinners
    private String[] metodosPago = {"Efectivo", "Tarjeta Crédito", "Transferencia", "Cheque"};
    private String[] numerosPagos = {"1", "2", "3", "4", "6", "12"};

    // Formato de moneda
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garantias);

        // Inicializar formato de moneda
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

        // Inicializar base de datos
        db = UniversidadBeta.getAppDatabase(this);
        garantiaDAO = db.garantiaDAO();
        donadorDAO = db.donadorDAO();
        corporacionDAO = db.corporacionDAO();

        inicializarVistas();
        configurarSpinners();
        configurarRecyclerView();
        cargarDatosIniciales();
        configurarEventos();
        establecerFechaActual();
    }

    private void inicializarVistas() {
        txtIdGarantia = findViewById(R.id.txtIdGarantia);
        txtBuscarDonador = findViewById(R.id.txtBuscarDonador);
        txtFechaGarantia = findViewById(R.id.txtFechaGarantia);
        txtCantidadGarantizada = findViewById(R.id.txtCantidadGarantizada);
        txtCantidadEnviada = findViewById(R.id.txtCantidadEnviada);
        txtNumTarjeta = findViewById(R.id.txtNumTarjeta);
        txtDireccionCorporacion = findViewById(R.id.txtDireccionCorporacion);

        txtNombreDonador = findViewById(R.id.txtNombreDonador);
        txtIdDonador = findViewById(R.id.txtIdDonador);

        spinnerMetodoPago = findViewById(R.id.spinnerMetodoPago);
        spinnerNumeroPagos = findViewById(R.id.spinnerNumeroPagos);
        spinnerCorporacion = findViewById(R.id.spinnerCorporacion);

        btnBuscarDonador = findViewById(R.id.btnBuscarDonador);
        btnRegistrarGarantia = findViewById(R.id.btnRegistrarGarantia);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnVolver = findViewById(R.id.btnVolver);
        btnBuscarGarantia = findViewById(R.id.btnBuscarGarantia);
        btnEditarGarantia = findViewById(R.id.btnEditarGarantia);
        btnEliminarGarantia = findViewById(R.id.btnEliminarGarantia);

        recyclerViewGarantias = findViewById(R.id.recyclerViewGarantias);
    }

    private void configurarSpinners() {
        // Método de pago
        ArrayAdapter<String> metodoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, metodosPago);
        metodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodoPago.setAdapter(metodoAdapter);

        spinnerMetodoPago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean esTarjeta = metodosPago[position].equals("Tarjeta Crédito");
                txtNumTarjeta.setEnabled(esTarjeta);
                txtNumTarjeta.setBackgroundColor(getResources().getColor(
                        esTarjeta ? android.R.color.white : R.color.gray_light));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Número de pagos
        ArrayAdapter<String> numPagosAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, numerosPagos);
        numPagosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumeroPagos.setAdapter(numPagosAdapter);

        // Corporación (se cargará dinámicamente)
        ArrayAdapter<String> corporacionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        corporacionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorporacion.setAdapter(corporacionAdapter);

        spinnerCorporacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && listaCorporaciones != null && position - 1 < listaCorporaciones.size()) {
                    corporacionSeleccionada = listaCorporaciones.get(position - 1);
                    // Actualizar dirección de corporación si está disponible
                    if (corporacionSeleccionada.direccion != null) {
                        txtDireccionCorporacion.setText(corporacionSeleccionada.direccion);
                    }
                } else {
                    corporacionSeleccionada = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configurarRecyclerView() {
        listaGarantias = new ArrayList<>();
        adapter = new GarantiaAdapter(listaGarantias, new GarantiaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Garantia garantia) {
                seleccionarGarantia(garantia);
            }

            @Override
            public void onItemLongClick(Garantia garantia) {
                mostrarDialogoOpciones(garantia);
            }
        });

        recyclerViewGarantias.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewGarantias.setAdapter(adapter);
    }

    private void cargarDatosIniciales() {
        new Thread(() -> {
            // Cargar garantías para obtener máximo ID
            List<Garantia> garantias = garantiaDAO.mostrarTodos();
            maxIdGarantia = 0;
            for (Garantia g : garantias) {
                if (g.idGarantia > maxIdGarantia) {
                    maxIdGarantia = g.idGarantia;
                }
            }

            // Cargar corporaciones para spinner
            listaCorporaciones = corporacionDAO.mostrarTodos();
            List<String> nombresCorporaciones = new ArrayList<>();
            nombresCorporaciones.add("-- Seleccionar --");
            for (Corporacion c : listaCorporaciones) {
                nombresCorporaciones.add(c.nombre);
            }

            runOnUiThread(() -> {
                // Actualizar ID siguiente
                txtIdGarantia.setText(String.valueOf(maxIdGarantia + 1));

                // Actualizar spinner de corporaciones
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCorporacion.getAdapter();
                adapter.clear();
                adapter.addAll(nombresCorporaciones);
                adapter.notifyDataSetChanged();

                // Cargar historial de garantías
                cargarGarantias();

                // Verificar si se recibió algún dato de otra activity
                procesarDatosRecibidos();
            });
        }).start();
    }

    private void procesarDatosRecibidos() {
        // Si esta activity fue abierta desde otra (ej: DonativoActivity)
        // con datos específicos, procesarlos aquí
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String busquedaSugerida = extras.getString("busquedaSugerida", "");
            if (!busquedaSugerida.isEmpty()) {
                txtBuscarDonador.setText(busquedaSugerida);
                buscarDonador();
            }
        }
    }

    private void cargarGarantias() {
        new Thread(() -> {
            List<Garantia> garantias = garantiaDAO.mostrarTodos();
            runOnUiThread(() -> {
                listaGarantias.clear();
                listaGarantias.addAll(garantias);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void configurarEventos() {
        btnBuscarDonador.setOnClickListener(v -> buscarDonador());
        btnRegistrarGarantia.setOnClickListener(v -> registrarGarantia());
        btnEditarGarantia.setOnClickListener(v -> editarGarantia());
        btnEliminarGarantia.setOnClickListener(v -> eliminarGarantia());
        btnBuscarGarantia.setOnClickListener(v -> buscarGarantia());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void establecerFechaActual() {
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        txtFechaGarantia.setHint("Ej: " + fechaActual);
    }

    private void buscarDonador() {
        String busqueda = txtBuscarDonador.getText().toString().trim();

        if (busqueda.isEmpty()) {
            Toast.makeText(this, "Ingrese ID o nombre del donador", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            List<Donador> resultados = new ArrayList<>();

            // Buscar por ID si es numérico
            if (busqueda.matches("\\d+")) {
                try {
                    int id = Integer.parseInt(busqueda);
                    Donador donador = donadorDAO.obtenerPorId(id);
                    if (donador != null) {
                        resultados.add(donador);
                    }
                } catch (NumberFormatException e) {
                    // Continuar con búsqueda por nombre
                }
            }

            // Si no encontró por ID, buscar por nombre
            if (resultados.isEmpty()) {
                resultados = donadorDAO.buscarPorCoincidencia(busqueda);
            }

            final List<Donador> finalResultados = resultados;
            runOnUiThread(() -> {
                if (finalResultados.isEmpty()) {
                    Toast.makeText(this, "No se encontraron donadores", Toast.LENGTH_SHORT).show();
                    limpiarInfoDonador();
                } else if (finalResultados.size() == 1) {
                    // Un solo resultado, seleccionarlo automáticamente
                    donadorSeleccionado = finalResultados.get(0);
                    mostrarInfoDonador(donadorSeleccionado);
                } else {
                    // Múltiples resultados, mostrar diálogo para seleccionar
                    mostrarSeleccionDonador(finalResultados);
                }
            });
        }).start();
    }

    private void mostrarSeleccionDonador(List<Donador> donadores) {
        String[] nombres = new String[donadores.size()];
        for (int i = 0; i < donadores.size(); i++) {
            nombres[i] = donadores.get(i).nombre + " (ID: " + donadores.get(i).idDonador + ")";
        }

        new AlertDialog.Builder(this)
                .setTitle("Seleccionar Donador")
                .setItems(nombres, (dialog, which) -> {
                    donadorSeleccionado = donadores.get(which);
                    mostrarInfoDonador(donadorSeleccionado);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarInfoDonador(Donador donador) {
        txtNombreDonador.setText(donador.nombre);
        txtIdDonador.setText("ID: " + donador.idDonador);

        // Si el donador tiene corporación, seleccionarla en el spinner
        if (donador.idCorporacion != null) {
            new Thread(() -> {
                Corporacion corp = corporacionDAO.mostrarTodos().stream()
                        .filter(c -> c.idCorporacion == donador.idCorporacion)
                        .findFirst()
                        .orElse(null);

                if (corp != null) {
                    runOnUiThread(() -> {
                        for (int i = 0; i < spinnerCorporacion.getCount(); i++) {
                            if (spinnerCorporacion.getItemAtPosition(i).toString().equals(corp.nombre)) {
                                spinnerCorporacion.setSelection(i);
                                break;
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private void limpiarInfoDonador() {
        txtNombreDonador.setText("Nombre del donador");
        txtIdDonador.setText("ID: ");
        donadorSeleccionado = null;
    }

    private void registrarGarantia() {
        // Validar donador seleccionado
        if (donadorSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un donador", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar nombre de garantía (usaremos un nombre generado)
        String fechaGarantia = txtFechaGarantia.getText().toString().trim();
        if (fechaGarantia.isEmpty()) {
            Toast.makeText(this, "La fecha de garantía es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar formato de fecha
        if (!validarFormatoFecha(fechaGarantia)) {
            Toast.makeText(this, "Formato de fecha inválido. Use dd/mm/aaaa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar cantidades
        double cantidadGarantizada = 0;
        double cantidadEnviada = 0;

        try {
            if (!txtCantidadGarantizada.getText().toString().isEmpty()) {
                cantidadGarantizada = Double.parseDouble(txtCantidadGarantizada.getText().toString());
            }
            if (!txtCantidadEnviada.getText().toString().isEmpty()) {
                cantidadEnviada = Double.parseDouble(txtCantidadEnviada.getText().toString());
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidades inválidas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar tarjeta si método es tarjeta de crédito
        if (spinnerMetodoPago.getSelectedItemPosition() == 1) { // Tarjeta Crédito
            String numTarjeta = txtNumTarjeta.getText().toString().trim();
            if (numTarjeta.length() < 13 || numTarjeta.length() > 19) {
                Toast.makeText(this, "Número de tarjeta inválido (13-19 dígitos)", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Crear nombre y descripción de la garantía
        String nombreGarantia = generarNombreGarantia(donadorSeleccionado.nombre, fechaGarantia);
        String descripcion = generarDescripcionGarantia(
                donadorSeleccionado.nombre,
                fechaGarantia,
                cantidadGarantizada,
                cantidadEnviada,
                spinnerMetodoPago.getSelectedItem().toString()
        );

        // Crear nueva garantía
        Garantia nuevaGarantia = new Garantia(
                maxIdGarantia + 1,
                nombreGarantia,
                descripcion
        );

        new Thread(() -> {
            garantiaDAO.agregarGarantia(nuevaGarantia);

            runOnUiThread(() -> {
                Toast.makeText(this, "Garantía registrada exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarGarantias();
            });
        }).start();
    }

    private String generarNombreGarantia(String nombreDonador, String fecha) {
        // Formato: Garantía - [Nombre Donador] - [Fecha]
        return "Garantía - " + nombreDonador + " - " + fecha;
    }

    private String generarDescripcionGarantia(String nombreDonador, String fecha,
                                              double cantidadGarantizada, double cantidadEnviada,
                                              String metodoPago) {
        StringBuilder descripcion = new StringBuilder();
        descripcion.append("Garantía registrada para: ").append(nombreDonador).append("\n");
        descripcion.append("Fecha: ").append(fecha).append("\n");

        if (cantidadGarantizada > 0) {
            descripcion.append("Cantidad garantizada: ").append(currencyFormat.format(cantidadGarantizada)).append("\n");
        }

        if (cantidadEnviada > 0) {
            descripcion.append("Cantidad enviada: ").append(currencyFormat.format(cantidadEnviada)).append("\n");
        }

        descripcion.append("Método de pago: ").append(metodoPago).append("\n");

        if (spinnerNumeroPagos.getSelectedItemPosition() > 0) {
            descripcion.append("Número de pagos: ").append(spinnerNumeroPagos.getSelectedItem()).append("\n");
        }

        if (corporacionSeleccionada != null) {
            descripcion.append("Corporación: ").append(corporacionSeleccionada.nombre).append("\n");
            if (txtDireccionCorporacion.getText().toString().trim().length() > 0) {
                descripcion.append("Dirección: ").append(txtDireccionCorporacion.getText().toString()).append("\n");
            }
        }

        return descripcion.toString();
    }

    private boolean validarFormatoFecha(String fecha) {
        // Validación simple de formato dd/mm/aaaa
        return fecha.matches("\\d{2}/\\d{2}/\\d{4}");
    }

    private void seleccionarGarantia(Garantia garantia) {
        garantiaSeleccionada = garantia;

        // Cargar información de la garantía en el formulario
        txtIdGarantia.setText(String.valueOf(garantia.idGarantia));
        txtFechaGarantia.setText(extraerFechaDeDescripcion(garantia.descripcion));

        // Extraer cantidades de la descripción
        extraerCantidadesDeDescripcion(garantia.descripcion);

        // Extraer información adicional de la descripción
        extraerInfoAdicionalDeDescripcion(garantia.descripcion);

        // Habilitar botones de edición y eliminación
        btnEditarGarantia.setEnabled(true);
        btnEliminarGarantia.setEnabled(true);
        btnRegistrarGarantia.setEnabled(false);
    }

    private String extraerFechaDeDescripcion(String descripcion) {
        if (descripcion == null) return "";

        String[] lineas = descripcion.split("\n");
        for (String linea : lineas) {
            if (linea.startsWith("Fecha: ")) {
                return linea.substring(7);
            }
        }
        return "";
    }

    private void extraerCantidadesDeDescripcion(String descripcion) {
        if (descripcion == null) return;

        String[] lineas = descripcion.split("\n");
        for (String linea : lineas) {
            if (linea.startsWith("Cantidad garantizada: ")) {
                String monto = linea.substring(22).replace("$", "").replace(",", "").trim();
                txtCantidadGarantizada.setText(monto);
            } else if (linea.startsWith("Cantidad enviada: ")) {
                String monto = linea.substring(18).replace("$", "").replace(",", "").trim();
                txtCantidadEnviada.setText(monto);
            }
        }
    }

    private void extraerInfoAdicionalDeDescripcion(String descripcion) {
        if (descripcion == null) return;

        String[] lineas = descripcion.split("\n");
        for (String linea : lineas) {
            if (linea.startsWith("Método de pago: ")) {
                String metodo = linea.substring(16);
                for (int i = 0; i < metodosPago.length; i++) {
                    if (metodosPago[i].equals(metodo)) {
                        spinnerMetodoPago.setSelection(i);
                        break;
                    }
                }
            } else if (linea.startsWith("Número de pagos: ")) {
                String numPagos = linea.substring(17);
                for (int i = 0; i < numerosPagos.length; i++) {
                    if (numerosPagos[i].equals(numPagos)) {
                        spinnerNumeroPagos.setSelection(i);
                        break;
                    }
                }
            } else if (linea.startsWith("Corporación: ")) {
                String corpNombre = linea.substring(13);
                for (int i = 0; i < spinnerCorporacion.getCount(); i++) {
                    if (spinnerCorporacion.getItemAtPosition(i).toString().equals(corpNombre)) {
                        spinnerCorporacion.setSelection(i);
                        break;
                    }
                }
            } else if (linea.startsWith("Dirección: ")) {
                String direccion = linea.substring(11);
                txtDireccionCorporacion.setText(direccion);
            } else if (linea.startsWith("Garantía registrada para: ")) {
                String nombreDonador = linea.substring(26);
                txtBuscarDonador.setText(nombreDonador);
                // Buscar el donador automáticamente
                buscarDonadorPorNombre(nombreDonador);
            }
        }
    }

    private void buscarDonadorPorNombre(String nombre) {
        new Thread(() -> {
            List<Donador> resultados = donadorDAO.buscarPorCoincidencia(nombre);
            if (!resultados.isEmpty()) {
                donadorSeleccionado = resultados.get(0);
                runOnUiThread(() -> mostrarInfoDonador(donadorSeleccionado));
            }
        }).start();
    }

    private void editarGarantia() {
        if (garantiaSeleccionada == null) {
            Toast.makeText(this, "Seleccione una garantía para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar donador seleccionado
        if (donadorSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un donador", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaGarantia = txtFechaGarantia.getText().toString().trim();
        if (fechaGarantia.isEmpty()) {
            Toast.makeText(this, "La fecha de garantía es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar nueva descripción
        double cantidadGarantizada = 0;
        double cantidadEnviada = 0;

        try {
            if (!txtCantidadGarantizada.getText().toString().isEmpty()) {
                cantidadGarantizada = Double.parseDouble(txtCantidadGarantizada.getText().toString());
            }
            if (!txtCantidadEnviada.getText().toString().isEmpty()) {
                cantidadEnviada = Double.parseDouble(txtCantidadEnviada.getText().toString());
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidades inválidas", Toast.LENGTH_SHORT).show();
            return;
        }

        String nuevaDescripcion = generarDescripcionGarantia(
                donadorSeleccionado.nombre,
                fechaGarantia,
                cantidadGarantizada,
                cantidadEnviada,
                spinnerMetodoPago.getSelectedItem().toString()
        );

        String nuevoNombre = generarNombreGarantia(donadorSeleccionado.nombre, fechaGarantia);

        // Actualizar garantía
        Garantia garantiaActualizada = new Garantia(
                garantiaSeleccionada.idGarantia,
                nuevoNombre,
                nuevaDescripcion
        );

        new Thread(() -> {
            garantiaDAO.actualizarGarantia(garantiaActualizada);

            runOnUiThread(() -> {
                Toast.makeText(this, "Garantía actualizada exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarGarantias();
            });
        }).start();
    }

    private void eliminarGarantia() {
        if (garantiaSeleccionada == null) {
            Toast.makeText(this, "Seleccione una garantía para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar la garantía?\n\n" + garantiaSeleccionada.nombre)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    new Thread(() -> {
                        garantiaDAO.eliminarGarantia(garantiaSeleccionada);

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Garantía eliminada exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarFormulario();
                            cargarGarantias();
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void buscarGarantia() {
        String busqueda = txtBuscarDonador.getText().toString().trim();

        if (busqueda.isEmpty()) {
            cargarGarantias();
            return;
        }

        new Thread(() -> {
            List<Garantia> resultados = garantiaDAO.buscarPorCoincidencia(busqueda);
            runOnUiThread(() -> {
                listaGarantias.clear();
                listaGarantias.addAll(resultados);
                adapter.notifyDataSetChanged();

                if (resultados.isEmpty()) {
                    Toast.makeText(this, "No se encontraron garantías", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void mostrarDialogoOpciones(Garantia garantia) {
        String[] opciones = {"Editar", "Eliminar", "Ver detalles", "Copiar información"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones: " + garantia.nombre)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            seleccionarGarantia(garantia);
                            break;
                        case 1: // Eliminar
                            garantiaSeleccionada = garantia;
                            eliminarGarantia();
                            break;
                        case 2: // Ver detalles
                            mostrarDetallesGarantia(garantia);
                            break;
                        case 3: // Copiar información
                            copiarInformacionGarantia(garantia);
                            break;
                    }
                })
                .show();
    }

    private void mostrarDetallesGarantia(Garantia garantia) {
        new AlertDialog.Builder(this)
                .setTitle("Detalles de la Garantía")
                .setMessage("ID: " + garantia.idGarantia + "\n\n" +
                        "Nombre: " + garantia.nombre + "\n\n" +
                        "Descripción:\n" + garantia.descripcion)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void copiarInformacionGarantia(Garantia garantia) {
        // Aquí podrías implementar la lógica para copiar información al portapapeles
        Toast.makeText(this, "Funcionalidad de copiar en desarrollo", Toast.LENGTH_SHORT).show();
    }

    private void limpiarFormulario() {
        txtIdGarantia.setText(String.valueOf(maxIdGarantia + 1));
        txtBuscarDonador.setText("");
        txtFechaGarantia.setText("");
        txtCantidadGarantizada.setText("");
        txtCantidadEnviada.setText("");
        txtNumTarjeta.setText("");
        txtDireccionCorporacion.setText("");

        limpiarInfoDonador();

        spinnerMetodoPago.setSelection(0);
        spinnerNumeroPagos.setSelection(0);
        spinnerCorporacion.setSelection(0);

        garantiaSeleccionada = null;
        donadorSeleccionado = null;
        corporacionSeleccionada = null;

        btnEditarGarantia.setEnabled(false);
        btnEliminarGarantia.setEnabled(false);
        btnRegistrarGarantia.setEnabled(true);

        establecerFechaActual();

        // Volver a cargar todas las garantías
        cargarGarantias();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}