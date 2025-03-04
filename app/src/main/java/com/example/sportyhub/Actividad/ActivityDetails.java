package com.example.sportyhub.Actividad;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Adapters.ParticipantesAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.MainActivity;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Etiqueta;
import com.example.sportyhub.Modelos.Pago;
import com.example.sportyhub.Modelos.Participante;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Reporte.CrearReporte;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.ShippingPreference;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButtonContainer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityDetails extends AppCompatActivity implements OnMapReadyCallback {
    //Variables de los componentes del layout
    GoogleMap mMap;
    ImageView ivImagenActividad;
    LinearLayout linearLayoutEtiquetas;
    Toolbar toolbar;
    EditText etDescripcion;
    TextView tvLugarActividad, tvInicioActividad, tvPrecioActividad, tvEtiquetaTitle, tvDuracion,
            tvEtiqueta1, tvEtiqueta2, tvEtiqueta3, tvEtiqueta4;
    Button btnUnirse;
    PaymentButtonContainer paymentButtonContainer;

    // Variables para la lógica
    boolean isUsuarioUnido;
    boolean isUsuarioCreador;
    Usuario usuario;
    Actividad actividadSeleccionada;
    double precioActividad;
    boolean is_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String scheme = data.getScheme(); // "com.example.sportyhub"
            String host = data.getHost();     // "paypalpay"

            if ("paypalpay".equals(host)) {
                // Aquí procesas la respuesta del pago
                Toast.makeText(this, "Pago procesado correctamente", Toast.LENGTH_LONG).show();
            }
        }

        // Obtenemos la informacion de los extras del intent y asigno a variables lo que vaya a usar
        usuario = getIntent().getParcelableExtra("usuario_actual", Usuario.class);
        int id_user = usuario.getIdUsuario().intValue();
        actividadSeleccionada = getIntent().getParcelableExtra("actividad_seleccionada", Actividad.class);
        precioActividad = actividadSeleccionada.getPrecio() / actividadSeleccionada.getParticipantesNecesarios();
        precioActividad = Math.round(precioActividad * 100.0) / 100.0;
        int idActividad = actividadSeleccionada.getIdActividad();
        is_admin = usuario.isAdmin();

        // Configuramos los ajustes para la pasarela de pago
        PayPalCheckout.setConfig(new CheckoutConfig(
                this.getApplication(),
                "ARuGMSfzKGwETPZgsWXISPAxXqKEh39LNsGn4PcFG-LnHfIWgsZp3S3JPu1odVHxXqM6yOgwvuE9GyEs",
                Environment.SANDBOX,
                CurrencyCode.EUR,
                UserAction.PAY_NOW,
                "com.example.sportyhub://paypalpay"
        ));



        // Asignamos los componentes del layout a variables
        setContentView(R.layout.activity_details);
        toolbar = findViewById(R.id.toolbar_detallesActividad);
        tvLugarActividad = findViewById(R.id.holderLugar);
        tvInicioActividad = findViewById(R.id.holderInicio);
        tvPrecioActividad = findViewById(R.id.priceHolder);
        etDescripcion = findViewById(R.id.etDescripcion);
        ivImagenActividad = findViewById(R.id.ImagenActividad);
        tvEtiquetaTitle = findViewById(R.id.tv_etiquetas);
        tvDuracion = findViewById(R.id.duracionHolder);
        linearLayoutEtiquetas = findViewById(R.id.ll_etiquetas);
        tvEtiqueta1 = findViewById(R.id.tv_etiqueta1);
        tvEtiqueta2 = findViewById(R.id.tv_etiqueta2);
        tvEtiqueta3 = findViewById(R.id.tv_etiqueta3);
        tvEtiqueta4 = findViewById(R.id.tv_etiqueta4);


        btnUnirse = findViewById(R.id.btnUnirse);
        paymentButtonContainer = findViewById(R.id.btnCheckout);
        paymentButtonContainer.post(() -> paymentButtonContainer.setVisibility(View.GONE));

        botonPago(paymentButtonContainer, precioActividad);


        // Buscamos y asignamos el recyclerView de nuestro layout.
        RecyclerView recyclerView = findViewById(R.id.recyclerViewParticipantes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Puedes elegir el layout manager adecuado para tus necesidades

        // Aquí utilizamos el ID obtenido anteriormente para cargar los participantes de la actividad
        cargarListaParticipantes(recyclerView, idActividad, id_user);

        // Preparamos el mapa que usaremos para cargar la ubicación de la actividad
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaActividad);
        mapFragment.getMapAsync(this);

        // Mostramos el Titulo de la actividad en el layout
        toolbar.setTitle(actividadSeleccionada.getTitulo());

        // Preparamos el resto de datos sobre la actividad
        cargarDetalles();

        // Configuración del botón para unirse o abandonar la actividad
        btnUnirse.setOnClickListener(v -> {
            btnUnirse.setEnabled(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

            // Determina el mensaje según el estado del usuario en la actividad
            String titulo, mensaje;
            if (is_admin){
                titulo = "Eliminar actividad";
                mensaje = "Al eliminar la actividad, se cancelaran todas las participaciones y se devolveran los pagos.\n" +
                        "¿Seguro que desea continuar?";
            }else {
                if (isUsuarioUnido) {
                    if (isUsuarioCreador) {
                        titulo = "Cancelar Actividad";
                        mensaje = "¿Seguro que deseas cancelar la actividad?";
                    } else {
                        titulo = "Confirmar salida";
                        mensaje = "¿Seguro que quieres abandonar la actividad?";
                    }
                } else {
                    titulo = "Confirmar unión";
                    mensaje = "¿Seguro que quieres unirte a la actividad?";
                }
            }

            builder.setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton("Sí", (dialog, which) -> {
                        if (is_admin){
                            cancelarActividad(idActividad);
                        }else {
                            if (isUsuarioUnido) {
                                if (isUsuarioCreador){
                                    cancelarActividad(idActividad);
                                }else{
                                    // Si la actividad es gratuita, se abandona directamente
                                    if (precioActividad == 0) {
                                        abandonarActividadGratis(idActividad, id_user);
                                    } else {
                                        // Si es de pago, verificamos si el usuario tiene derecho a reembolso
                                        if (!comprobarPosibleReembolso()) {
                                            // Si no tiene derecho a reembolso, se muestra una advertencia adicional
                                            new AlertDialog.Builder(v.getContext())
                                                    .setTitle("Último aviso")
                                                    .setMessage("Debido a que la cancelación de tu participación es muy cercana " +
                                                            "al inicio de la actividad, no tendrás derecho a la devolución del coste.")
                                                    .setPositiveButton("Confirmar abandono", (dialog2, which2) ->
                                                            abandonarActividadGratis(idActividad, id_user))
                                                    .setNegativeButton("Cancelar", (dialog2, which2) -> {
                                                            dialog2.dismiss();
                                                            btnUnirse.setEnabled(true); // Volver a activar el botón si cancela
                                                    })
                                                    .show();
                                        } else {
                                            // Si tiene derecho a reembolso, se abandona directamente
                                            abandonarActividadPago(idActividad, id_user);
                                        }
                                    }
                                }
                            } else {
                                // Si la actividad es gratuita, el usuario puede unirse sin problemas
                                if (precioActividad == 0) {
                                    unirseActividadGratis(idActividad, id_user);
                                }
                            }
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                        btnUnirse.setEnabled(true); // Volver a activar el botón si cancela
                    })
                    .show();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reportar) {
            comprobarSiExisteReporte(usuario, actividadSeleccionada);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void comprobarSiExisteReporte(Usuario usuario, Actividad actividadSeleccionada) {
        int idUsuario = usuario.getIdUsuario().intValue();
        int idActividad = actividadSeleccionada.getIdActividad();

        ApiService apiService = ApiClient.getApiService(this);

        // Realizar la llamada al servidor
        Call<Boolean> call = apiService.comprobarReporteExistente(idUsuario, idActividad, "ACTIVIDAD");

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()){
                        Toast.makeText(getApplicationContext(),
                                "¡Ya has reportado esta actividad!", Toast.LENGTH_SHORT).show();
                    }else{
                        // Acción para editar el equipo
                        Intent intent = new Intent(getApplicationContext(), CrearReporte.class);
                        intent.putExtra("actividad_reportada", actividadSeleccionada);
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("API", "Error al cargar participantes: " + t.getMessage());
            }
        });
    }

    private void cancelarActividad(int idActividad) {
        ApiService apiService = ApiClient.getApiService(this);

        // Realizar la llamada al servidor
        Call<Boolean> call = apiService.cancelarActividad(idActividad);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()){
                        Toast.makeText(getApplicationContext(), "Actividad cancelada.", Toast.LENGTH_LONG).show();
                        volverAPantallaInicial();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error interno. No se ha podido cancelar", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("API", "Error al cargar participantes: " + t.getMessage());
            }
        });
    }

    private boolean comprobarPosibleReembolso() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Convertir fecha y hora a LocalDateTime
        String fechaHoraStr = actividadSeleccionada.getFechaLimiteCancelacion();
        LocalDateTime fechaLimiteCancelacion = LocalDateTime.parse(fechaHoraStr, formatterFecha);

        return !ahora.isAfter(fechaLimiteCancelacion);
    }

    // Función asíncrona la cual devuelve los participantes que forman parte de la actividad.
    private void cargarListaParticipantes(RecyclerView recyclerView, int idActividad, int user) {
        ApiService apiService = ApiClient.getApiService(this);

        // Realizar la llamada al servidor
        Call<List<Participante>> call = apiService.getParticipantes(idActividad);

        call.enqueue(new Callback<List<Participante>>() {
            @Override
            public void onResponse(Call<List<Participante>> call, Response<List<Participante>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Participante> listaParticipantes = response.body();

                    Log.e("API", "Lista de participantes" + listaParticipantes.size());

                    // Verificar si el usuario está en la lista
                    comprobarUnido(listaParticipantes, user, precioActividad);

                    // Configurar el RecyclerView con un adaptador
                    ParticipantesAdapter adapter = new ParticipantesAdapter(listaParticipantes, usuario);
                    recyclerView.setAdapter(adapter);

                    // Agregar línea separadora
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
                    dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider)));
                    recyclerView.addItemDecoration(dividerItemDecoration);
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Participante>> call, Throwable t) {
                Log.e("API", "Error al cargar participantes: " + t.getMessage());
            }
        });
    }

    // Prepara el mapa para mostrar la ubicación de la actividad seleccionada
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double latitud = actividadSeleccionada.getLatitud();
        double longitud = actividadSeleccionada.getLongitud();

        // Setear una ubicacion inicial. Necesario recoger la ubicación del usuario.
        LatLng inicial = new LatLng(latitud, longitud);

        // Añadir un marcador
        mMap.addMarker(new MarkerOptions().position(inicial).title(actividadSeleccionada.getLugar()));

        // Mover mapa a la ubicación inicial
        mMap.moveCamera(CameraUpdateFactory.newLatLng(inicial));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(inicial, 18.0f));
    }

    // Se encarga de cargar la información de la actividad seleccionada y mostrarla en el layout
    private void cargarDetalles(){
        String fecha = actividadSeleccionada.getFecha() + ", " + actividadSeleccionada.getHora();
        String lugar = actividadSeleccionada.getLugar();
        double price = actividadSeleccionada.getPrecio();
        int duracion = actividadSeleccionada.getDuracion();
        int deporte = actividadSeleccionada.getDeporte();
        String descripcion = actividadSeleccionada.getDescripcion();

        tvInicioActividad.setText(fecha);
        tvLugarActividad.setText(lugar);
        tvPrecioActividad.setText(price == 0 ? "GRATIS" : precioActividad + "€");
        tvDuracion.setText(duracion + " minutos");
        etDescripcion.setText(descripcion);
        etDescripcion.setEnabled(false);
        etDescripcion.setFocusable(false);



        switch (deporte){
            case 1:
                ivImagenActividad.setImageResource(R.drawable.fut7);
                break;

            case 2:
                ivImagenActividad.setImageResource(R.drawable.futbol);
                break;

            case 3:
                ivImagenActividad.setImageResource(R.drawable.futsal);
                break;

            case 4:
                ivImagenActividad.setImageResource(R.drawable.tenis);
                break;

            case 5:
                ivImagenActividad.setImageResource(R.drawable.padel);
                break;

            case 6:
                ivImagenActividad.setImageResource(R.drawable.baloncesto);
                break;

            case 7:
                ivImagenActividad.setImageResource(R.drawable.beisbol);
                break;

            default:
                ivImagenActividad.setImageResource(R.drawable.activities);
                break;
        }

        Set<Etiqueta> etiquetas = actividadSeleccionada.getEtiquetas();

        if (etiquetas.isEmpty()){
            tvEtiquetaTitle.setVisibility(View.GONE);
            linearLayoutEtiquetas.setVisibility(View.GONE);
        }else{
            CargarEtiquetas(etiquetas);
        }

    }

    private void CargarEtiquetas(Set<Etiqueta> etiquetas) {
        // Convertir el Set en un array correctamente
        Etiqueta[] arrayEtiquetas = etiquetas.toArray(new Etiqueta[0]);

        // Hacemos no visibles todos los TextViews por defecto
        tvEtiqueta1.setVisibility(View.GONE);
        tvEtiqueta2.setVisibility(View.GONE);
        tvEtiqueta3.setVisibility(View.GONE);
        tvEtiqueta4.setVisibility(View.GONE);

        // Asignar valores según la cantidad de etiquetas
        if (arrayEtiquetas.length > 0) {
            Log.d("APP_DEBUG", arrayEtiquetas[0].getNombre());
            tvEtiqueta1.setText(arrayEtiquetas[0].getNombre());
            tvEtiqueta1.setVisibility(View.VISIBLE);
        }
        if (arrayEtiquetas.length > 1) {
            tvEtiqueta2.setText(arrayEtiquetas[1].getNombre());
            tvEtiqueta2.setVisibility(View.VISIBLE);
        }
        if (arrayEtiquetas.length > 2) {
            tvEtiqueta3.setText(arrayEtiquetas[2].getNombre());
            tvEtiqueta3.setVisibility(View.VISIBLE);
        }
        if (arrayEtiquetas.length > 3) {
            tvEtiqueta4.setText(arrayEtiquetas[3].getNombre());
            tvEtiqueta4.setVisibility(View.VISIBLE);
        }
    }

    private void unirseActividadGratis(int idActividad, int usuarioId) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.unirseActividad(idActividad, usuarioId);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exito = response.body();
                    if (exito) {
                        Toast.makeText(ActivityDetails.this, "¡Te has unido a la actividad!", Toast.LENGTH_SHORT).show();
                        volverAPantallaInicial();
                    } else {
                        Toast.makeText(ActivityDetails.this, "¡Ha ocurrido un error, vuelve a intentarlo más tarde!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityDetails.this, "Error al procesar la solicitud.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ActivityDetails.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void unirseActividadPago(int idActividad, int usuarioId, String orderId, double cantidad) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.unirseActividad(idActividad, usuarioId);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exito = response.body();
                    if (exito) {
                        Toast.makeText(ActivityDetails.this, "¡Pago realizado y unido a la actividad!", Toast.LENGTH_SHORT).show();
                        capturePayment(orderId, cantidad);
                    } else {
                        Toast.makeText(ActivityDetails.this, "¡Error al realizar el pago o unirse a la actividad!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityDetails.this, "Error al procesar la solicitud.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ActivityDetails.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Función para abandonar la actividad si el usuario ya estaba inscrito
    private void abandonarActividadGratis(int idActividad, int usuarioId) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.cancelarParticipacion(idActividad, usuarioId);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exito = response.body();
                    if (exito) {
                        Toast.makeText(ActivityDetails.this, "¡Has abandonado la actividad!", Toast.LENGTH_LONG).show();
                        volverAPantallaInicial();
                    } else {
                        Toast.makeText(ActivityDetails.this, "¡No se ha podido abandonar la actividad!", Toast.LENGTH_SHORT).show();
                        btnUnirse.setEnabled(true);
                    }
                } else {
                    Toast.makeText(ActivityDetails.this, "Error al procesar la solicitud.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ActivityDetails.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abandonarActividadPago(int idActividad, int idUsuario){
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.cancelarParticipacionConReembolso(idActividad, idUsuario);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exito = response.body();
                    if (exito) {
                        Toast.makeText(ActivityDetails.this, "¡Has abandonado la actividad!", Toast.LENGTH_LONG).show();
                        volverAPantallaInicial();
                    } else {
                        Toast.makeText(ActivityDetails.this, "¡No se ha podido abandonar la actividad!", Toast.LENGTH_SHORT).show();
                        btnUnirse.setEnabled(true);
                    }
                } else {
                    Toast.makeText(ActivityDetails.this, "Error al procesar la solicitud.", Toast.LENGTH_SHORT).show();
                    btnUnirse.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ActivityDetails.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Funcion para comprobar si el usuario ya está unido a la actividad
    private void comprobarUnido(List<Participante> participantes, int id_usuario, double precioActividad){
        isUsuarioUnido = false;
        for (Participante participante: participantes) {
            int id = participante.getUsuario().getIdUsuario().intValue();
            if (id == id_usuario){
                isUsuarioUnido = true;
                if (usuario.getIdUsuario().intValue() == actividadSeleccionada.getCreador()){
                    isUsuarioCreador = true;
                }else{
                    isUsuarioCreador = false;
                    setSupportActionBar(toolbar);
                }
            }
        }
        ajustarBoton(precioActividad);
    }

    private void botonPago(PaymentButtonContainer paymentButtonContainer, double cantidad){
        paymentButtonContainer.setup(
                createOrderActions -> {
                    ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                    purchaseUnits.add(
                            new PurchaseUnit.Builder()
                                    .amount(new Amount.Builder()
                                            .currencyCode(CurrencyCode.EUR)
                                            .value(String.valueOf(cantidad))
                                            .build()
                                    )
                                    .build()
                    );
                    OrderRequest order = new OrderRequest(
                            OrderIntent.CAPTURE,
                            new AppContext.Builder()
                                    .userAction(UserAction.PAY_NOW)
                                    .shippingPreference(ShippingPreference.NO_SHIPPING)
                                    .build(),
                            purchaseUnits
                    );
                    createOrderActions.create(order, s -> {
                        // La orden ha sido creada, se pasa al siguiente paso
                        Log.d("APP_PAYPAL", "Orden creada: " + s);
                    });
                },
                approval -> {
                    String orderId = approval.getData().getOrderId();
                    Log.d("PAYPAL", "Pago aprobado, Order ID: " + orderId);
                    Toast.makeText(ActivityDetails.this, "Pago completado", Toast.LENGTH_SHORT).show();

                    String order_id = approval.getData().getOrderId();
                    // Envíamos la información del pago a la API y lo registramos
                    enviarOrderIdAlBackend(order_id, cantidad, orderId);
                },
                null,
                () -> {
                    Log.d(TAG, "Buyer cancelled the PayPal experience.");
                    Toast.makeText(ActivityDetails.this, "Pago cancelado", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ActivityDetails.this, ResumenPago.class);
                    intent.putExtra("order_id", "N/A");
                    intent.putExtra("cantidad", 0.0);
                    intent.putExtra("pago_exitoso", false);
                    startActivity(intent);
                    finish();
                }, errorInfo -> {
                    Log.d(TAG, String.format("Error: %s", errorInfo));
                    Toast.makeText(ActivityDetails.this, "Error al realizar el pago", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void capturePayment(String orderId, double cantidad) {
        new Thread(() -> {
            try {
                runOnUiThread(() -> {
                        // Redirigir al Resumen de Pago si el pago fue exitoso
                        Intent intent = new Intent(ActivityDetails.this, ResumenPago.class);
                        intent.putExtra("order_id", orderId);
                        intent.putExtra("cantidad", cantidad);
                        intent.putExtra("pago_exitoso", true);
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(ActivityDetails.this, "Error en la captura del pago", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }



    private void enviarOrderIdAlBackend(String order_id, double cantidad, String orderId){
        ApiService apiService = ApiClient.getApiService(this);
        Pago pago = new Pago(order_id, usuario, actividadSeleccionada, cantidad,
                "Pago de participación para la actividad " + actividadSeleccionada.getTitulo()); // Aquí rellenas los datos del pago

        Call<Pago> call = apiService.realizarPago(pago);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Pago> call, Response<Pago> response) {
                if (response.isSuccessful()) {
                    Log.e("API", "Pago realizado y retenido correctamente");
                    unirseActividadPago(actividadSeleccionada.getIdActividad(), usuario.getIdUsuario().intValue(), orderId, cantidad);
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Pago> call, Throwable t) {
                Log.e("API", "Error al retener el pago: " + t.getMessage());
            }
        });
    }

    private void ajustarBoton(double precio){
        if (is_admin){
            btnUnirse.setVisibility(View.VISIBLE);
            btnUnirse.setText("Eliminar actividad");
            paymentButtonContainer.setPaypalButtonEnabled(false);
        }else{
            if (isUsuarioUnido){
                if (isUsuarioCreador){
                    btnUnirse.setVisibility(View.VISIBLE);
                    btnUnirse.setText("Cancelar Actividad");
                    paymentButtonContainer.setPaypalButtonEnabled(false);
                }else {
                    btnUnirse.setVisibility(View.VISIBLE);
                    btnUnirse.setText("Abandonar Actividad");
                    paymentButtonContainer.setPaypalButtonEnabled(false);
                }
            } else{
                if (precio != 0){
                    btnUnirse.setVisibility(View.INVISIBLE);
                    paymentButtonContainer.setVisibility(View.VISIBLE);
                    paymentButtonContainer.setPaypalButtonEnabled(true);
                }else{
                    btnUnirse.setVisibility(View.VISIBLE);
                    paymentButtonContainer.setPaypalButtonEnabled(false);
                }
            }
        }
    }

    private void volverAPantallaInicial(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("usuario", usuario);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}