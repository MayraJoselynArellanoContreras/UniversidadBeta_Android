package com.example.bd_sqlite_2025;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import controlers.PagoDAO;
import controlers.GarantiaDAO;
import controlers.DonativoDAO;
import db.UniversidadBeta;
import entities.Pago;
import entities.Garantia;
import entities.Donativo;
import ReciclerViews.PagoAdapter;

public class PagosGarantiasActivity extends AppCompatActivity {

    // Views
    private EditText txtIdGarantia, txtMontoPago, txtFechaPago, txtObservaciones;
    private LinearLayout layoutInfoGarantia;
    private TextView txtInfoGarantia, txtSaldoInfo;
    private Button btnBuscarGarantia, btnRegistrarPago, btnEliminarPago, btnVerHistorial, btnVolver;
    private RecyclerView recyclerViewPagos;

    // Base de datos y DAOs
    private UniversidadBeta db;
    private PagoDAO pagoDAO;
    private GarantiaDAO garantiaDAO;
    private DonativoDAO donativoDAO;

    // Datos
    private List<Pago> listaPagos;
    private PagoAdapter adapter;
    private Garantia garantiaSeleccionada = null;
    private Pago pagoSeleccionado = null;
    private int maxIdPago = 0;

    // Formato de moneda y fecha
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos);

        // Inicializar formatos
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Inicializar base de datos
        db = UniversidadBeta.getAppDatabase(this);
        pagoDAO = db.pagoDAO();
        garantiaDAO = db.garantiaDAO();
        donativoDAO = db.donativoDAO();

        inicializarVistas();
        configurarRecyclerView();
        cargarMaxIdPago();
        configurarEventos();
        establecerFechaActual();
    }

    private void inicializarVistas() {
        txtIdGarantia = findViewById(R.id.txtIdGarantia);
        txtMontoPago = findViewById(R.id.txtMontoPago);
        txtFechaPago = findViewById(R.id.txtFechaPago);
        txtObservaciones = findViewById(R.id.txtObservaciones);

        layoutInfoGarantia = findViewById(R.id.layoutInfoGarantia);
        txtInfoGarantia = findViewById(R.id.txtInfoGarantia);
        txtSaldoInfo = findViewById(R.id.txtSaldoInfo);

        btnBuscarGarantia = findViewById(R.id.btnBuscarGarantia);
        btnRegistrarPago = findViewById(R.id.btnRegistrarPago);
        btnEliminarPago = findViewById(R.id.btnEliminarPago);
        btnVerHistorial = findViewById(R.id.btnVerHistorial);
        btnVolver = findViewById(R.id.btnVolver);

        recyclerViewPagos = findViewById(R.id.recyclerViewPagos);
    }

    private void configurarRecyclerView() {
        listaPagos = new ArrayList<>();
        adapter = new PagoAdapter(listaPagos, new PagoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Pago pago) {
                seleccionarPago(pago);
            }

            @Override
            public void onItemLongClick(Pago pago) {
                mostrarDialogoOpciones(pago);
            }
        });

        recyclerViewPagos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPagos.setAdapter(adapter);
    }

    private void cargarMaxIdPago() {
        new Thread(() -> {
            List<Pago> pagos = pagoDAO.mostrarTodos();
            maxIdPago = 0;
            for (Pago p : pagos) {
                if (p.idPago > maxIdPago) {
                    maxIdPago = p.idPago;
                }
            }
        }).start();
    }

    private void configurarEventos() {
        btnBuscarGarantia.setOnClickListener(v -> buscarGarantia());
        btnRegistrarPago.setOnClickListener(v -> registrarPago());
        btnEliminarPago.setOnClickListener(v -> eliminarPago());
        btnVerHistorial.setOnClickListener(v -> verHistorialCompleto());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void establecerFechaActual() {
        String fechaActual = dateFormat.format(new Date());
        txtFechaPago.setHint("Ej: " + fechaActual);
    }

    private void buscarGarantia() {
        String idGarantiaStr = txtIdGarantia.getText().toString().trim();

        if (idGarantiaStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el ID de la garantía", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int idGarantia = Integer.parseInt(idGarantiaStr);

            new Thread(() -> {
                // Buscar la garantía por ID
                List<Garantia> garantias = garantiaDAO.mostrarTodos();
                garantiaSeleccionada = null;

                for (Garantia g : garantias) {
                    if (g.idGarantia == idGarantia) {
                        garantiaSeleccionada = g;
                        break;
                    }
                }

                if (garantiaSeleccionada != null) {
                    // Buscar donativo asociado a esta garantía
                    List<Donativo> donativos = donativoDAO.mostrarTodos();
                    double saldoGarantia = calcularSaldoGarantia(garantiaSeleccionada, donativos);

                    runOnUiThread(() -> {
                        // Mostrar información de la garantía
                        layoutInfoGarantia.setVisibility(View.VISIBLE);
                        txtInfoGarantia.setText("Garantía: " + garantiaSeleccionada.nombre);
                        txtSaldoInfo.setText("Saldo pendiente: " + currencyFormat.format(saldoGarantia));

                        // Cargar pagos asociados a esta garantía
                        cargarPagosPorGarantia(idGarantia);

                        Toast.makeText(PagosGarantiasActivity.this,
                                "Garantía encontrada", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        layoutInfoGarantia.setVisibility(View.GONE);
                        Toast.makeText(PagosGarantiasActivity.this,
                                "Garantía no encontrada", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID de garantía inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private double calcularSaldoGarantia(Garantia garantia, List<Donativo> donativos) {
        double saldo = 0.0;

        // Buscar donativos asociados a esta garantía
        for (Donativo donativo : donativos) {
            if (donativo.idGarantia != null && donativo.idGarantia == garantia.idGarantia) {
                // Sumar el monto del donativo
                saldo += donativo.monto;

                // Restar los pagos realizados para este donativo
                List<Pago> pagos = pagoDAO.mostrarTodos();
                for (Pago pago : pagos) {
                    if (pago.idDonativo == donativo.idDonativo) {
                        saldo -= pago.monto;
                    }
                }
            }
        }

        return saldo;
    }

    private void cargarPagosPorGarantia(int idGarantia) {
        new Thread(() -> {
            // 1. Buscar donativos asociados a esta garantía
            List<Donativo> donativos = donativoDAO.mostrarTodos();
            List<Integer> idsDonativos = new ArrayList<>();

            for (Donativo d : donativos) {
                if (d.idGarantia != null && d.idGarantia == idGarantia) {
                    idsDonativos.add(d.idDonativo);
                }
            }

            // 2. Buscar pagos de esos donativos
            List<Pago> todosPagos = pagoDAO.mostrarTodos();
            List<Pago> pagosFiltrados = new ArrayList<>();

            for (Pago pago : todosPagos) {
                if (idsDonativos.contains(pago.idDonativo)) {
                    pagosFiltrados.add(pago);
                }
            }

            runOnUiThread(() -> {
                listaPagos.clear();
                listaPagos.addAll(pagosFiltrados);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void registrarPago() {
        // Validar garantía seleccionada
        if (garantiaSeleccionada == null) {
            Toast.makeText(this, "Debe buscar y seleccionar una garantía primero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar monto
        String montoStr = txtMontoPago.getText().toString().trim();
        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el monto del pago", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
            if (monto <= 0) {
                Toast.makeText(this, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar fecha
        String fecha = txtFechaPago.getText().toString().trim();
        if (fecha.isEmpty()) {
            Toast.makeText(this, "Ingrese la fecha del pago", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarFormatoFecha(fecha)) {
            Toast.makeText(this, "Formato de fecha inválido. Use dd/mm/aaaa", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar donativo asociado a la garantía
        new Thread(() -> {
            List<Donativo> donativos = donativoDAO.mostrarTodos();
            Integer idDonativoAsociado = null;

            for (Donativo d : donativos) {
                if (d.idGarantia != null && d.idGarantia == garantiaSeleccionada.idGarantia) {
                    idDonativoAsociado = d.idDonativo;
                    break;
                }
            }

            if (idDonativoAsociado == null) {
                runOnUiThread(() -> {
                    Toast.makeText(PagosGarantiasActivity.this,
                            "No se encontró donativo asociado a esta garantía",
                            Toast.LENGTH_SHORT).show();
                });
                return;
            }

            // Verificar saldo disponible
            double saldoGarantia = calcularSaldoGarantia(garantiaSeleccionada, donativos);
            if (monto > saldoGarantia) {
                runOnUiThread(() -> {
                    Toast.makeText(PagosGarantiasActivity.this,
                            "El monto excede el saldo disponible: " + currencyFormat.format(saldoGarantia),
                            Toast.LENGTH_SHORT).show();
                });
                return;
            }

            // Crear nuevo pago
            Pago nuevoPago = new Pago(
                    maxIdPago + 1,
                    monto,
                    fecha,
                    idDonativoAsociado
            );

            // Guardar en base de datos
            pagoDAO.agregarPago(nuevoPago);

            runOnUiThread(() -> {
                Toast.makeText(PagosGarantiasActivity.this,
                        "Pago registrado exitosamente", Toast.LENGTH_SHORT).show();

                // Limpiar formulario
                txtMontoPago.setText("");
                txtFechaPago.setText("");
                txtObservaciones.setText("");

                // Actualizar saldo
                actualizarSaldo();

                // Recargar lista de pagos
                cargarPagosPorGarantia(garantiaSeleccionada.idGarantia);
            });

        }).start();
    }

    private boolean validarFormatoFecha(String fecha) {
        return fecha.matches("\\d{2}/\\d{2}/\\d{4}");
    }

    private void seleccionarPago(Pago pago) {
        pagoSeleccionado = pago;

        // Cargar información del pago en el formulario
        txtMontoPago.setText(String.valueOf(pago.monto));
        txtFechaPago.setText(pago.fecha);

        // Buscar información adicional del pago
        buscarInformacionPago(pago);

        // Habilitar botón de eliminación
        btnEliminarPago.setEnabled(true);
    }

    private void buscarInformacionPago(Pago pago) {
        new Thread(() -> {
            // Buscar el donativo asociado
            List<Donativo> donativos = donativoDAO.mostrarTodos();
            Donativo donativoAsociado = null;

            for (Donativo d : donativos) {
                if (d.idDonativo == pago.idDonativo) {
                    donativoAsociado = d;
                    break;
                }
            }

            if (donativoAsociado != null) {
                // Buscar la garantía asociada al donativo
                List<Garantia> garantias = garantiaDAO.mostrarTodos();
                Garantia garantiaAsociada = null;

                for (Garantia g : garantias) {
                    if (donativoAsociado.idGarantia != null &&
                            g.idGarantia == donativoAsociado.idGarantia) {
                        garantiaAsociada = g;
                        break;
                    }
                }

                final String infoGarantia = garantiaAsociada != null ?
                        garantiaAsociada.nombre : "Sin garantía asociada";

                runOnUiThread(() -> {
                    txtObservaciones.setText("Pago asociado a garantía: " + infoGarantia);
                });
            }
        }).start();
    }

    private void eliminarPago() {
        if (pagoSeleccionado == null) {
            Toast.makeText(this, "Seleccione un pago para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Está seguro de eliminar este pago?\n\n" +
                        "Monto: " + currencyFormat.format(pagoSeleccionado.monto) + "\n" +
                        "Fecha: " + pagoSeleccionado.fecha)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    new Thread(() -> {
                        pagoDAO.eliminarPago(pagoSeleccionado);

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Pago eliminado exitosamente", Toast.LENGTH_SHORT).show();

                            // Limpiar selección
                            pagoSeleccionado = null;
                            btnEliminarPago.setEnabled(false);

                            // Actualizar saldo
                            actualizarSaldo();

                            // Recargar lista de pagos
                            if (garantiaSeleccionada != null) {
                                cargarPagosPorGarantia(garantiaSeleccionada.idGarantia);
                            }
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void verHistorialCompleto() {
        new Thread(() -> {
            List<Pago> todosPagos = pagoDAO.mostrarTodos();

            if (todosPagos.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "No hay pagos registrados", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            // Calcular total de pagos
            double totalPagos = 0;
            for (Pago pago : todosPagos) {
                totalPagos += pago.monto;
            }

            // Construir mensaje del historial
            StringBuilder historial = new StringBuilder();
            historial.append("Historial Completo de Pagos\n\n");
            historial.append("Total de pagos registrados: ").append(todosPagos.size()).append("\n");
            historial.append("Monto total pagado: ").append(currencyFormat.format(totalPagos)).append("\n\n");

            // Agrupar por mes para resumen
            historial.append("Resumen por Fecha:\n");

            // Usar un mapa simple para agrupar por mes
            java.util.Map<String, Double> pagosPorMes = new java.util.HashMap<>();
            for (Pago pago : todosPagos) {
                try {
                    String mes = pago.fecha.substring(3, 10); // dd/MM/yyyy -> extraer MM/yyyy
                    pagosPorMes.put(mes, pagosPorMes.getOrDefault(mes, 0.0) + pago.monto);
                } catch (Exception e) {
                    // Ignorar fechas mal formateadas
                }
            }

            for (java.util.Map.Entry<String, Double> entry : pagosPorMes.entrySet()) {
                historial.append(entry.getKey()).append(": ").append(currencyFormat.format(entry.getValue())).append("\n");
            }

            final String historialStr = historial.toString();

            runOnUiThread(() -> {
                new AlertDialog.Builder(PagosGarantiasActivity.this)
                        .setTitle("Historial Completo")
                        .setMessage(historialStr)
                        .setPositiveButton("Aceptar", null)
                        .show();
            });
        }).start();
    }

    private void actualizarSaldo() {
        if (garantiaSeleccionada == null) return;

        new Thread(() -> {
            List<Donativo> donativos = donativoDAO.mostrarTodos();
            double saldoGarantia = calcularSaldoGarantia(garantiaSeleccionada, donativos);

            runOnUiThread(() -> {
                txtSaldoInfo.setText("Saldo pendiente: " + currencyFormat.format(saldoGarantia));
            });
        }).start();
    }

    private void mostrarDialogoOpciones(Pago pago) {
        String[] opciones = {"Editar", "Eliminar", "Ver detalles", "Exportar"};

        new AlertDialog.Builder(this)
                .setTitle("Opciones: Pago #" + pago.idPago)
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            seleccionarPago(pago);
                            // Nota: Para editar completamente, necesitarías implementar la lógica de edición
                            Toast.makeText(this, "Funcionalidad de edición en desarrollo",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case 1: // Eliminar
                            pagoSeleccionado = pago;
                            eliminarPago();
                            break;
                        case 2: // Ver detalles
                            mostrarDetallesPago(pago);
                            break;
                        case 3: // Exportar
                            exportarPago(pago);
                            break;
                    }
                })
                .show();
    }

    private void mostrarDetallesPago(Pago pago) {
        new Thread(() -> {
            // Buscar información adicional
            List<Donativo> donativos = donativoDAO.mostrarTodos();
            Donativo donativoAsociado = null;

            for (Donativo d : donativos) {
                if (d.idDonativo == pago.idDonativo) {
                    donativoAsociado = d;
                    break;
                }
            }

            String infoDonativo = donativoAsociado != null ?
                    "Donativo ID: " + donativoAsociado.idDonativo :
                    "Donativo no encontrado";

            final String detalles = "ID Pago: " + pago.idPago + "\n" +
                    "Monto: " + currencyFormat.format(pago.monto) + "\n" +
                    "Fecha: " + pago.fecha + "\n" +
                    infoDonativo + "\n" +
                    "ID Donativo: " + pago.idDonativo;

            runOnUiThread(() -> {
                new AlertDialog.Builder(PagosGarantiasActivity.this)
                        .setTitle("Detalles del Pago")
                        .setMessage(detalles)
                        .setPositiveButton("Aceptar", null)
                        .show();
            });
        }).start();
    }

    private void exportarPago(Pago pago) {
        // Aquí podrías implementar la lógica para exportar el pago (PDF, Excel, etc.)
        Toast.makeText(this, "Funcionalidad de exportación en desarrollo",
                Toast.LENGTH_SHORT).show();
    }

    private void limpiarFormulario() {
        txtMontoPago.setText("");
        txtFechaPago.setText("");
        txtObservaciones.setText("");
        establecerFechaActual();

        pagoSeleccionado = null;
        btnEliminarPago.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}