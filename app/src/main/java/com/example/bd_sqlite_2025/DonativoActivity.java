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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import controlers.DonativoDAO;
import controlers.DonadorDAO;
import controlers.CorporacionDAO;
import controlers.GarantiaDAO;
import controlers.PagoDAO;
import db.UniversidadBeta;
import entities.Donativo;
import entities.Donador;
import entities.Corporacion;
import entities.Garantia;
import entities.Pago;
import ReciclerViews.DonativoAdapter;

public class DonativoActivity extends AppCompatActivity {

    // Views del formulario
    private EditText txtIdDonativo, txtBuscarDonador, txtFechaGarantia, txtFechaRegistro;
    private EditText txtCantidadGarantizada, txtCantidadRecibida, txtNumTarjeta, txtObservaciones;
    private TextView txtNombreDonador, txtIdDonador;
    private Spinner spinnerMetodo, spinnerNumPagos, spinnerCorporacion;
    private Button btnBuscarDonador, btnRegistrar, btnLimpiar, btnVolver;
    private Button btnEditar, btnEliminar, btnGestionarPagos;
    private RecyclerView recyclerViewDonativos;

    // Base de datos y DAOs
    private UniversidadBeta db;
    private DonativoDAO donativoDAO;
    private DonadorDAO donadorDAO;
    private CorporacionDAO corporacionDAO;
    private GarantiaDAO garantiaDAO;
    private PagoDAO pagoDAO;

    // Datos
    private List<Donativo> listaDonativos;
    private List<Donador> listaDonadores;
    private List<Corporacion> listaCorporaciones;
    private DonativoAdapter adapter;
    private Donativo donativoSeleccionado = null;
    private Donador donadorSeleccionado = null;
    private Corporacion corporacionSeleccionada = null;
    private int maxIdDonativo = 0;

    // Arrays para spinners
    private String[] metodosPago = {"Efectivo", "Tarjeta Crédito", "Transferencia", "Cheque"};
    private String[] numerosPagos = {"1", "2", "3", "4", "6", "12"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donativos);

        // Inicializar base de datos
        db = UniversidadBeta.getAppDatabase(this);
        donativoDAO = db.donativoDAO();
        donadorDAO = db.donadorDAO();
        corporacionDAO = db.corporacionDAO();
        garantiaDAO = db.garantiaDAO();
        pagoDAO = db.pagoDAO();

        inicializarVistas();
        configurarSpinners();
        configurarRecyclerView();
        cargarDatosIniciales();
        configurarEventos();
        establecerFechaActual();
    }

    private void inicializarVistas() {
        txtIdDonativo = findViewById(R.id.txtIdDonativo);
        txtBuscarDonador = findViewById(R.id.txtBuscarDonador);
        txtFechaGarantia = findViewById(R.id.txtFechaGarantia);
        txtFechaRegistro = findViewById(R.id.txtFechaRegistro);
        txtCantidadGarantizada = findViewById(R.id.txtCantidadGarantizada);
        txtCantidadRecibida = findViewById(R.id.txtCantidadRecibida);
        txtNumTarjeta = findViewById(R.id.txtNumTarjeta);
        txtObservaciones = findViewById(R.id.txtObservaciones);

        txtNombreDonador = findViewById(R.id.txtNombreDonador);
        txtIdDonador = findViewById(R.id.txtIdDonador);

        spinnerMetodo = findViewById(R.id.spinnerMetodo);
        spinnerNumPagos = findViewById(R.id.spinnerNumPagos);
        spinnerCorporacion = findViewById(R.id.spinnerCorporacion);

        btnBuscarDonador = findViewById(R.id.btnBuscarDonador);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnVolver = findViewById(R.id.btnVolver);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnGestionarPagos = findViewById(R.id.btnGestionarPagos);

        recyclerViewDonativos = findViewById(R.id.recyclerViewDonativos);
    }

    private void configurarSpinners() {
        // Método de pago
        ArrayAdapter<String> metodoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, metodosPago);
        metodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodo.setAdapter(metodoAdapter);

        spinnerMetodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        spinnerNumPagos.setAdapter(numPagosAdapter);

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
                } else {
                    corporacionSeleccionada = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configurarRecyclerView() {
        listaDonativos = new ArrayList<>();
        adapter = new DonativoAdapter(listaDonativos, new DonativoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Donativo donativo) {
                seleccionarDonativo(donativo);
            }

            @Override
            public void onItemLongClick(Donativo donativo) {
                mostrarDialogoOpciones(donativo);
            }
        });

        recyclerViewDonativos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDonativos.setAdapter(adapter);
    }

    private void cargarDatosIniciales() {
        new Thread(() -> {
            // Cargar donativos para obtener máximo ID
            List<Donativo> donativos = donativoDAO.mostrarTodos();
            maxIdDonativo = 0;
            for (Donativo d : donativos) {
                if (d.idDonativo > maxIdDonativo) {
                    maxIdDonativo = d.idDonativo;
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
                txtIdDonativo.setText(String.valueOf(maxIdDonativo + 1));

                // Actualizar spinner de corporaciones
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerCorporacion.getAdapter();
                adapter.clear();
                adapter.addAll(nombresCorporaciones);
                adapter.notifyDataSetChanged();

                // Cargar historial de donativos
                cargarDonativos();
            });
        }).start();
    }

    private void cargarDonativos() {
        new Thread(() -> {
            List<Donativo> donativos = donativoDAO.mostrarTodos();
            runOnUiThread(() -> {
                listaDonativos.clear();
                listaDonativos.addAll(donativos);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void configurarEventos() {
        btnBuscarDonador.setOnClickListener(v -> buscarDonador());
        btnRegistrar.setOnClickListener(v -> registrarDonativo());
        btnEditar.setOnClickListener(v -> editarDonativo());
        btnEliminar.setOnClickListener(v -> eliminarDonativo());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
        btnVolver.setOnClickListener(v -> finish());
        btnGestionarPagos.setOnClickListener(v -> gestionarPagos());
    }

    private void establecerFechaActual() {
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        txtFechaRegistro.setText(fechaActual);
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

    private void registrarDonativo() {
        // Validar donador seleccionado
        if (donadorSeleccionado == null) {
            Toast.makeText(this, "Debe seleccionar un donador", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar fechas
        String fechaGarantia = txtFechaGarantia.getText().toString().trim();
        String fechaRegistro = txtFechaRegistro.getText().toString().trim();

        if (fechaRegistro.isEmpty()) {
            Toast.makeText(this, "La fecha de registro es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar cantidades
        double cantidadGarantizada = 0;
        double cantidadRecibida = 0;

        try {
            if (!txtCantidadGarantizada.getText().toString().isEmpty()) {
                cantidadGarantizada = Double.parseDouble(txtCantidadGarantizada.getText().toString());
            }
            if (!txtCantidadRecibida.getText().toString().isEmpty()) {
                cantidadRecibida = Double.parseDouble(txtCantidadRecibida.getText().toString());
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidades inválidas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar tarjeta si método es tarjeta de crédito
        if (spinnerMetodo.getSelectedItemPosition() == 1) { // Tarjeta Crédito
            String numTarjeta = txtNumTarjeta.getText().toString().trim();
            if (numTarjeta.length() < 13 || numTarjeta.length() > 19) {
                Toast.makeText(this, "Número de tarjeta inválido (13-19 dígitos)", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Crear nueva garantía si hay cantidad garantizada
        Integer idGarantia = null;
        if (cantidadGarantizada > 0 && !fechaGarantia.isEmpty()) {
            idGarantia = crearGarantia(fechaGarantia, cantidadGarantizada);
        }

        // Crear nuevo donativo
        Donativo nuevoDonativo = new Donativo(
                maxIdDonativo + 1,
                cantidadRecibida,
                fechaRegistro,
                donadorSeleccionado.idDonador,
                idGarantia
        );

        // Crear pagos según número seleccionado
        int numPagos = Integer.parseInt(spinnerNumPagos.getSelectedItem().toString());
        double montoPorPago = cantidadRecibida / numPagos;

        new Thread(() -> {
            // 1. Guardar donativo
            donativoDAO.agregarDonativo(nuevoDonativo);

            // 2. Crear pagos programados
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            for (int i = 0; i < numPagos; i++) {
                try {
                    // Calcular fecha para cada pago (30 días entre cada uno)
                    Date fechaPago = sdf.parse(fechaRegistro);
                    fechaPago.setTime(fechaPago.getTime() + (i * 30L * 24 * 60 * 60 * 1000));

                    Pago nuevoPago = new Pago(
                            0, // ID se generará automáticamente
                            montoPorPago,
                            sdf.format(fechaPago),
                            maxIdDonativo + 1
                    );
                    pagoDAO.agregarPago(nuevoPago);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Donativo registrado exitosamente", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Se crearon " + numPagos + " pagos programados", Toast.LENGTH_LONG).show();
                limpiarFormulario();
                cargarDonativos();
            });
        }).start();
    }

    private Integer crearGarantia(String fechaGarantia, double monto) {
        try {
            // Crear una nueva garantía
            String nombreGarantia = "Garantía " + new SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                    .format(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaGarantia));
            String descripcion = "Garantía por $" + monto + " con fecha " + fechaGarantia;

            // Obtener máximo ID de garantías
            List<Garantia> garantias = garantiaDAO.mostrarTodos();
            int maxIdGarantia = 0;
            for (Garantia g : garantias) {
                if (g.idGarantia > maxIdGarantia) {
                    maxIdGarantia = g.idGarantia;
                }
            }

            Garantia nuevaGarantia = new Garantia(
                    maxIdGarantia + 1,
                    nombreGarantia,
                    descripcion
            );

            garantiaDAO.agregarGarantia(nuevaGarantia);
            return nuevaGarantia.idGarantia;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void seleccionarDonativo(Donativo donativo) {
        donativoSeleccionado = donativo;

        // Cargar información del donativo en el formulario
        txtIdDonativo.setText(String.valueOf(donativo.idDonativo));
        txtFechaRegistro.setText(donativo.fecha);
        txtCantidadRecibida.setText(String.valueOf(donativo.monto));

        // Cargar información del donador
        new Thread(() -> {
            Donador donador = donadorDAO.obtenerPorId(donativo.idDonador);
            if (donador != null) {
                runOnUiThread(() -> {
                    mostrarInfoDonador(donador);
                    txtBuscarDonador.setText(donador.nombre);
                });
            }

            // Cargar información de garantía si existe
            if (donativo.idGarantia != null) {
                Garantia garantia = garantiaDAO.mostrarTodos().stream()
                        .filter(g -> g.idGarantia == donativo.idGarantia)
                        .findFirst()
                        .orElse(null);

                if (garantia != null) {
                    runOnUiThread(() -> {
                        // Parsear descripción para extraer monto y fecha
                        String desc = garantia.descripcion;
                        if (desc != null) {
                            if (desc.contains("$")) {
                                String montoStr = desc.substring(desc.indexOf("$") + 1, desc.indexOf(" con fecha"));
                                txtCantidadGarantizada.setText(montoStr.trim());
                            }
                            if (desc.contains("fecha ")) {
                                String fechaStr = desc.substring(desc.indexOf("fecha ") + 6);
                                txtFechaGarantia.setText(fechaStr.trim());
                            }
                        }
                    });
                }
            }
        }).start();

        // Habilitar botones de edición y eliminación
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnGestionarPagos.setEnabled(true);
        btnRegistrar.setEnabled(false);
    }

    private void editarDonativo() {
        if (donativoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un donativo para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validaciones similares a registrar
        double cantidadRecibida;
        try {
            cantidadRecibida = Double.parseDouble(txtCantidadRecibida.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad recibida inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        String fechaRegistro = txtFechaRegistro.getText().toString().trim();
        if (fechaRegistro.isEmpty()) {
            Toast.makeText(this, "Fecha de registro obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar donativo
        Donativo donativoActualizado = new Donativo(
                donativoSeleccionado.idDonativo,
                cantidadRecibida,
                fechaRegistro,
                donativoSeleccionado.idDonador,
                donativoSeleccionado.idGarantia
        );

        new Thread(() -> {
            donativoDAO.actualizarDonativo(donativoActualizado);
            runOnUiThread(() -> {
                Toast.makeText(this, "Donativo actualizado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarDonativos();
            });
        }).start();
    }

    private void eliminarDonativo() {
        if (donativoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un donativo para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar este donativo?\n\nEsta acción también eliminará todos los pagos asociados.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    new Thread(() -> {
                        // Eliminar pagos asociados primero
                        List<Pago> pagos = pagoDAO.mostrarTodos();
                        for (Pago pago : pagos) {
                            if (pago.idDonativo == donativoSeleccionado.idDonativo) {
                                pagoDAO.eliminarPago(pago);
                            }
                        }

                        // Eliminar donativo
                        donativoDAO.eliminarDonativo(donativoSeleccionado);

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Donativo eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            limpiarFormulario();
                            cargarDonativos();
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void gestionarPagos() {
        if (donativoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un donativo primero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aquí iría la lógica para abrir una Activity de gestión de pagos
        Toast.makeText(this, "Funcionalidad de gestión de pagos en desarrollo", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogoOpciones(Donativo donativo) {
        String[] opciones = {"Editar", "Eliminar", "Ver detalles", "Gestionar Pagos"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones: Donativo #" + donativo.idDonativo)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            seleccionarDonativo(donativo);
                            break;
                        case 1: // Eliminar
                            donativoSeleccionado = donativo;
                            eliminarDonativo();
                            break;
                        case 2: // Ver detalles
                            mostrarDetallesDonativo(donativo);
                            break;
                        case 3: // Gestionar Pagos
                            donativoSeleccionado = donativo;
                            gestionarPagos();
                            break;
                    }
                })
                .show();
    }

    private void mostrarDetallesDonativo(Donativo donativo) {
        new Thread(() -> {
            Donador donador = donadorDAO.obtenerPorId(donativo.idDonador);
            Garantia garantia = null;
            if (donativo.idGarantia != null) {
                garantia = garantiaDAO.mostrarTodos().stream()
                        .filter(g -> g.idGarantia == donativo.idGarantia)
                        .findFirst()
                        .orElse(null);
            }

            // Contar pagos
            List<Pago> pagos = pagoDAO.mostrarTodos();
            long numPagos = pagos.stream().filter(p -> p.idDonativo == donativo.idDonativo).count();

            final String nombreDonador = donador != null ? donador.nombre : "Desconocido";
            final String infoGarantia = garantia != null ? garantia.nombre : "Sin garantía";
            final String numPagosStr = String.valueOf(numPagos);

            runOnUiThread(() -> {
                String detalles = "ID: " + donativo.idDonativo + "\n" +
                        "Donador: " + nombreDonador + "\n" +
                        "Monto: $" + donativo.monto + "\n" +
                        "Fecha: " + donativo.fecha + "\n" +
                        "Garantía: " + infoGarantia + "\n" +
                        "Número de pagos: " + numPagosStr;

                new AlertDialog.Builder(this)
                        .setTitle("Detalles del Donativo")
                        .setMessage(detalles)
                        .setPositiveButton("Aceptar", null)
                        .show();
            });
        }).start();
    }

    private void limpiarFormulario() {
        txtIdDonativo.setText(String.valueOf(maxIdDonativo + 1));
        txtBuscarDonador.setText("");
        txtFechaGarantia.setText("");
        txtFechaRegistro.setText("");
        txtCantidadGarantizada.setText("");
        txtCantidadRecibida.setText("");
        txtNumTarjeta.setText("");
        txtObservaciones.setText("");

        limpiarInfoDonador();

        spinnerMetodo.setSelection(0);
        spinnerNumPagos.setSelection(0);
        spinnerCorporacion.setSelection(0);

        donativoSeleccionado = null;
        donadorSeleccionado = null;
        corporacionSeleccionada = null;

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGestionarPagos.setEnabled(false);
        btnRegistrar.setEnabled(true);

        establecerFechaActual();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}