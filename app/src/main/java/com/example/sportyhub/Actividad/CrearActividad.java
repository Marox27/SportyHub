package com.example.sportyhub.Actividad;


import static com.example.sportyhub.Login.mostrarSnackbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.BuildConfig;
import com.example.sportyhub.MainActivity;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Deporte;
import com.example.sportyhub.Modelos.Etiqueta;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Utilidades.GeocodingTask;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CrearActividad extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    private GoogleMap mMap;
    private FrameLayout mapContainer;
    Button addActivity;
    EditText tituloText, fechaText, horaText, precioText, min, descripcionText;
    Slider duracionText;
    Spinner deporteText;
    ScrollView scrollView;
    private ChipGroup chipGroupObjetivo, chipGroupIntesidad, chipGroupParticipantes, chipGroupUbicacion;

    double latitud, longitud;
    String lugar, fecha, hora;
    TextView progressTextView, tvParticipantes;
    private int min_participantes, max_participantes, idDeporte;

    ArrayList<String> deportes;
    PlacesClient placesClient;

    private Set<Etiqueta> etiquetas = new HashSet<>();
    String filtroIntesidad, filtroObjetivo, filtroUbicacion, filtroParticipantes;
    LatLng inicial;
    Usuario creador;

    boolean locationTrigger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_actividad);
        scrollView = findViewById(R.id.sc_crear_actividad);
        mapContainer = findViewById(R.id.fl_map);

        // Extraemos los valores extras del intent
        creador = getIntent().getParcelableExtra("usuario", Usuario.class);
        latitud = getIntent().getDoubleExtra("latitud", 0);
        longitud = getIntent().getDoubleExtra("longitud", 0);
        locationTrigger = latitud != 0 && longitud != 0;

        // Vamos asignando a cada variable, su componente respectivo del layout
        tituloText = findViewById(R.id.tituloText);
        deporteText = findViewById(R.id.deporteBox);
        fechaText = findViewById(R.id.dateText);
        horaText = findViewById(R.id.timeText);
        min = findViewById(R.id.minParticipanteText);
        tvParticipantes = findViewById(R.id.tv_participantes);
        duracionText = findViewById(R.id.durationText);
        precioText = findViewById(R.id.precioText);
        descripcionText = findViewById(R.id.etDescripcion);
        addActivity = findViewById(R.id.crearBtn);


        chipGroupIntesidad = findViewById(R.id.chipGroupIntensidad);
        chipGroupObjetivo = findViewById(R.id.chipGroupObjetivo);
        chipGroupParticipantes = findViewById(R.id.chipGroupParticipantes);
        chipGroupUbicacion = findViewById(R.id.chipGroupUbicacion);



        chipGroupIntesidad.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                filtroIntesidad = (chip != null) ? limpiarTextoChip(chip.getText().toString()) : "";
            } else {
                filtroIntesidad = "";
            }
         });

        chipGroupObjetivo.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                filtroObjetivo = (chip != null) ? limpiarTextoChip(chip.getText().toString()) : "";
            } else {
                filtroObjetivo = "";
            }
        });

        chipGroupParticipantes.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                filtroParticipantes = (chip != null) ? limpiarTextoChip(chip.getText().toString()) : "";
            } else {
                filtroParticipantes = "";
            }
        });

        chipGroupUbicacion.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                filtroUbicacion = (chip != null) ? limpiarTextoChip(chip.getText().toString()) : "";
            } else {
                filtroUbicacion = "";
            }
        });

        //maxDecrease = findViewById(R.id.maxDecreaseButton);
        //maxIncrease = findViewById(R.id.maxIncreaseButton);

        setupAutoCompleteFragment();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;

                // Deshabilita el desplazamiento del ScrollView cuando el mapa se mueve
                mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });


                // Habilita el desplazamiento del ScrollView cuando el mapa no está siendo tocado
                mapContainer.setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            scrollView.requestDisallowInterceptTouchEvent(true);
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            scrollView.requestDisallowInterceptTouchEvent(false);
                            v.performClick(); // Llamada para accesibilidad
                        }
                        return true; // Indica que el evento ha sido manejado
                    }
                });

                mapContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Esto es solo para evitar la advertencia, no hace nada
                    }
                });


            }
        });
        mapFragment.getMapAsync(this);

        // Asignamos el spinner de nuestro layout.
        deporteText = findViewById(R.id.deporteBox);

        // Iniciamos la tarea de fondo que se encargará de cargar los deportes en el spinner.
        cargarDeportes(deporteText);

        //Listener para manejar la selección del elemento
        deporteText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el objeto seleccionado
                Deporte deporteSeleccionado = (Deporte) parent.getSelectedItem();

                // Acceder al ID del deporte
                idDeporte = deporteSeleccionado.getIdDeporte();

                min_participantes = deporteSeleccionado.getMinJugadores();
                max_participantes = deporteSeleccionado.getMaxJugadores();

                min.setText(String.valueOf(min_participantes));
                tvParticipantes.setText(String.format(Locale.getDefault(),"Número de Participantes (Min %d, Máx %d)", min_participantes, max_participantes));
                //tvParticipantes.setText(getString(R.string.num_de_participantes, min_participantes, max_participantes));

                // Mostrar el nombre y el ID para pruebas
                Log.d("SPINNER_SELECTION", "Deporte seleccionado: " + deporteSeleccionado.getNombre() + " (ID: " + idDeporte + ")");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Se debe seleccionar un deporte.", Toast.LENGTH_SHORT).show();
            }
        });


        progressTextView = findViewById(R.id.progressTextView);

        fechaText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            // Abrir el DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CrearActividad.this,
                    (view, yearSelected, monthOfYear, dayOfMonth) -> {
                        // Formatear la fecha seleccionada
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + yearSelected;
                        fechaText.setText(selectedDate);

                        // Guardar la fecha en formato ISO 8601 (yyyy-MM-dd)
                        fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", yearSelected, monthOfYear + 1, dayOfMonth);
                        System.out.println(fecha);
                    },
                    calendar.get(Calendar.YEAR), // Año inicial
                    calendar.get(Calendar.MONTH), // Mes inicial
                    calendar.get(Calendar.DAY_OF_MONTH) // Día inicial
            );

            // Mostrar el DatePicker
            datePickerDialog.show();
        });


        horaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minuteOfDay = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        CrearActividad.this,
                        (view, hourOfDay, minute) -> {
                            // Se ejecuta cuando se selecciona una hora
                            String selectedTime = hourOfDay + ":" + minute; // Ajusta el formato según necesites
                            horaText.setText(selectedTime);
                        },
                        hour,
                        minuteOfDay,
                        true // true si quieres el formato de 24 horas, false si quieres el formato de 12 horas
                );
                timePickerDialog.show();
            }
        });

        // Agregar un listener al slider para manejar el cambio en el valor seleccionado
        duracionText.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Actualizar el texto de los kilómetros seleccionados
                String distanciaText = String.format(Locale.getDefault(), "%.0f minutos", value);
                progressTextView.setText(distanciaText);
            }
        });

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addActivity.setEnabled(false);

                String titulo = tituloText.getText().toString().trim();
                hora = horaText.getText().toString().trim();
                fecha = fechaText.getText().toString().trim();
                String participantesStr = min.getText().toString().trim();
                String precioStr = precioText.getText().toString().trim();
                String descripcion = descripcionText.getText() != null ? descripcionText.getText().toString() : "";

                // Validar que los campos no estén vacíos
                if (titulo.isEmpty() ||  fecha.isEmpty() || hora.isEmpty() || participantesStr.isEmpty() || precioStr.isEmpty()) {
                    mostrarSnackbar(findViewById(android.R.id.content), "Todos los campos son obligatorios.");
                    addActivity.setEnabled(true);
                    return;
                }

                if(lugar == null || lugar.isEmpty()){
                    mostrarSnackbar(findViewById(android.R.id.content), "Es necesario seleccionar un lugar para la actividad.");
                    addActivity.setEnabled(true);
                    return;
                }
                // Validamos que el precio esté bien
                Double precio = validarPrecio();
                if (precio == null) {
                    addActivity.setEnabled(true);
                    return;
                }

                // Validar que los valores numéricos sean correctos
                int participantesNecesarios;
                try {
                    participantesNecesarios = Integer.parseInt(participantesStr);
                } catch (NumberFormatException e) {
                    mostrarSnackbar(findViewById(android.R.id.content), "Formato numérico incorrecto.");
                    addActivity.setEnabled(true);
                    return;
                }


                // Validar que el número de participantes esté dentro del rango permitido
                if (participantesNecesarios < min_participantes || participantesNecesarios > max_participantes) {
                    mostrarSnackbar(findViewById(android.R.id.content),
                            "El número de participantes debe estar entre " + min_participantes + " y " + max_participantes + ".");
                    addActivity.setEnabled(true);
                    return;
                }

                // Convertir la fecha para que tenga el formato "dd-MM-yyyy"
                fecha = fecha.replace("/", "-");
                String[] partes = fecha.split("-");
                if (partes.length == 3) {
                    String dia = partes[0].length() == 1 ? "0" + partes[0] : partes[0];
                    String mes = partes[1].length() == 1 ? "0" + partes[1] : partes[1];
                    String anio = partes[2];
                    fecha = dia + "-" + mes + "-" + anio;
                }

                // Validar y reformatear la fecha al formato "yyyy-MM-dd"
                try {
                    DateTimeFormatter formatterOriginal = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate fechaDate = LocalDate.parse(fecha, formatterOriginal);
                    DateTimeFormatter formatterNuevo = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    fecha = fechaDate.format(formatterNuevo);
                } catch (Exception e) {
                    mostrarSnackbar(findViewById(android.R.id.content), "Formato de fecha incorrecto.");
                    addActivity.setEnabled(true);
                    return;
                }

                // Asegurar que la hora y los minutos tengan dos dígitos
                String[] partesHora = hora.split(":");
                if (partesHora.length == 2) {
                    partesHora[0] = partesHora[0].length() == 1 ? "0" + partesHora[0] : partesHora[0];
                    partesHora[1] = partesHora[1].length() == 1 ? "0" + partesHora[1] : partesHora[1];
                    hora = partesHora[0] + ":" + partesHora[1];
                } else {
                    mostrarSnackbar(findViewById(android.R.id.content), "Formato de hora incorrecto.");
                    addActivity.setEnabled(true);
                    return;
                }

                // Validar que la fecha y hora sean al menos 2 horas en el futuro
                String fechaHora = fecha + " " + hora;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                try {
                    LocalDateTime fechaActividad = LocalDateTime.parse(fechaHora, formatter);
                    if (LocalDateTime.now().plusHours(2).isAfter(fechaActividad)) {
                        mostrarSnackbar(findViewById(android.R.id.content), "La actividad debe crearse con al menos 2 horas de antelación.");
                        addActivity.setEnabled(true);
                        return;
                    }
                } catch (Exception e) {
                    mostrarSnackbar(findViewById(android.R.id.content), "Fecha u hora inválida.");
                    addActivity.setEnabled(true);
                    return;
                }

                int duracion = (int) duracionText.getValue();
                int idCreador = creador.getIdUsuario().intValue();

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("¿Todo Listo?")
                        .setMessage("Comprueba que todos los datos son correctos antes de confirmar:\n\n" +
                                "📌 Título: " + titulo + "\n" +
                                "📅 Fecha: " + fecha + "\n" +
                                "⏰ Hora: " + hora + "\n" +
                                "👥 Participantes: " + participantesNecesarios + "\n" +
                                "⏳ Duración: " + duracion + " min\n" +
                                "💰 Precio: " + precio + "€")
                    .setPositiveButton("Crear Actividad", (dialog, which) -> {
                        // Enviar la actividad
                        getEtiquetas(titulo, descripcion, fecha, hora, participantesNecesarios, duracion, precio, idCreador);
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        dialog.dismiss();
                        addActivity.setEnabled(true);
                    }).show();
                addActivity.setEnabled(true);
            }
        });
    }

    private Double validarPrecio() {
        String precioStr = precioText.getText().toString().trim();

        // Verificar si está vacío
        if (precioStr.isEmpty()) {
            mostrarSnackbar(findViewById(android.R.id.content), "El precio es obligatorio.");
            return null;
        }

        // Verificar si tiene más de un punto decimal
        if (precioStr.chars().filter(ch -> ch == '.').count() > 1) {
            mostrarSnackbar(findViewById(android.R.id.content), "El formato del precio no es válido.");
            return null;
        }

        try {
            double precio = Double.parseDouble(precioStr);

            // Verificar que el precio esté en un rango lógico
            if (precio < 0.50 || precio > 100) {
                mostrarSnackbar(findViewById(android.R.id.content), "El precio debe estar entre 0.50€ y 100€.");
                return null;
            }

            // Limitar a 2 decimales
            BigDecimal bd = new BigDecimal(precio).setScale(2, RoundingMode.HALF_UP);
            return bd.doubleValue();

        } catch (NumberFormatException e) {
            mostrarSnackbar(findViewById(android.R.id.content), "Formato numérico incorrecto.");
            return null;
        }
    }


    private void cargarDeportes(Spinner spinner) {
        // Obtener la instancia de Retrofit y crear el servicio
        ApiService apiService = ApiClient.getApiService(CrearActividad.this);
        Call<List<Deporte>> call = apiService.obtenerDeportes();

        // Realizar la llamada a la API
        call.enqueue(new Callback<List<Deporte>>() {
            @Override
            public void onResponse(Call<List<Deporte>> call, Response<List<Deporte>> response) {
                // Verificar el código de respuesta
                Log.d("API_RESPONSE", "Código de respuesta: " + response.code());

                // Verificar si el cuerpo de la respuesta es nulo
                if (response.body() == null) {
                    Log.e("API_ERROR", "Cuerpo de la respuesta es nulo");
                } else {
                    Log.d("API_RESPONSE", "Cuerpo de la respuesta: " + response.body());
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Deporte> listaDeportes = response.body();
                    Log.e("Sports load","Number of sports" + listaDeportes.size());

                    // Crear un ArrayAdapter personalizado
                    ArrayAdapter<Deporte> adapter = new ArrayAdapter<>(CrearActividad.this,
                            android.R.layout.simple_spinner_item, listaDeportes) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            // Solo mostramos el nombre del deporte en el Spinner
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setText(listaDeportes.get(position).getNombre());
                            return textView;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            // Configuraramos el diseño de cada opción desplegable
                            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                            textView.setText(listaDeportes.get(position).getNombre());
                            return textView;
                        }
                    };

                    // Configuraramos el Spinner en el hilo principal
                    runOnUiThread(() -> {
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    });
                } else {
                    Log.e("API_ERROR", "Error en la respuesta de la API");
                }
            }

            @Override
            public void onFailure(Call<List<Deporte>> call, Throwable t) {
                Log.e("API_ERROR", "Error al conectarse a la API: " + t.getMessage());
            }
        });
    }

    private void crearActividad(String titulo, String descripcion, int deporte, String fecha,
                                  String hora, int participantesNecesarios,
                                  int duracion, double precio, double latitud,
                                  double longitud, String lugar, int idCreador) {
        // Crear la instancia del servicio API
        ApiService apiService = ApiClient.getApiService(this);

        // Formateador para convertir la fecha y hora en LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Convertimos la fecha y hora en un LocalDateTime
        LocalDateTime fechaHora = LocalDateTime.parse(fecha + " " + hora, formatter);

        // Restar 24 horas para calcular la fecha límite
        LocalDateTime fechaLimite = fechaHora.minusHours(24);

        // Convertir la fecha límite a String con el mismo formato
        String fechaLimiteStr = fechaLimite.format(formatter);

        // Crear el objeto de la actividad
        Actividad nuevaActividad = new Actividad(titulo, descripcion, lugar, fecha, hora, fechaLimiteStr, duracion,
                participantesNecesarios, idCreador, deporte, precio, latitud, longitud, etiquetas
        );

        Log.d("APP:CREAR_ACTIVIDAD", "Lugar al seleccionar place: " + nuevaActividad.getLugar());

        // Realizar la llamada a la API
        apiService.crearActividad(nuevaActividad).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "¡Actividad Creada!", Toast.LENGTH_SHORT).show();

                    // Creada la actividad volver a MainActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("usuario", creador);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Error al crear la actividad. Código: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Error al crear la actividad: " + response.errorBody());
                    addActivity.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Fallo en la creación de la actividad: ", t);
                addActivity.setEnabled(true);
            }
        });
    }


    private void setupAutoCompleteFragment() {
        // Inicializa la API de Places
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }

        // Obtiene una referencia al fragmento de PlaceAutocomplete
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapAutoComplete);

        if (autocompleteFragment != null) {
            View view = autocompleteFragment.getView();
            if (view != null) {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        // Configura las opciones de autocompletado
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS, Place.Field.LOCATION));

        // Configuramos para que solo salgan resultados dentro de España.
        autocompleteFragment.setCountries("ES");

        // Establece un oyente para manejar la selección de lugares
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Maneja la selección del lugar
                String placeName = place.getDisplayName();
                String placeAddress = place.getFormattedAddress();
                lugar = place.getFormattedAddress();
                Log.d("APP:CREAR_ACTIVIDAD", "Lugar al seleccionar place: " + lugar);
                LatLng latlng = place.getLocation();
                latitud = latlng.latitude;
                longitud = latlng.longitude;
                System.out.println(placeName + " " + placeAddress + " " + latlng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18.0f));
                // Puedes realizar acciones con los datos del lugar seleccionado aquí
            }

            @Override
            public void onError(@NonNull Status status) {
                System.err.println(status.getStatus());

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        placesClient = Places.createClient(getApplicationContext());
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                // Crear una solicitud de detalles del lugar
                List<Place.Field> placeFields = Arrays.asList(
                        Place.Field.DISPLAY_NAME,
                        Place.Field.FORMATTED_ADDRESS,
                        Place.Field.INTERNATIONAL_PHONE_NUMBER,
                        Place.Field.RATING,
                        Place.Field.WEBSITE_URI
                );
                latitud = poi.latLng.latitude;
                longitud = poi.latLng.longitude;

                FetchPlaceRequest request = FetchPlaceRequest.newInstance(poi.placeId, placeFields);


                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();

                    // Eliminar marcadores previos (opcional)
                    mMap.clear();

                    lugar = place.getFormattedAddress();

                    // Verificar que los datos no sean nulos
                    String nombre = place.getDisplayName() != null ? place.getDisplayName() : "Sin nombre";
                    String direccion = place.getFormattedAddress() != null ? place.getFormattedAddress() : "Dirección no disponible";
                    String telefono = place.getInternationalPhoneNumber() != null ? place.getInternationalPhoneNumber() : "Teléfono no disponible";
                    String rating = place.getRating() != null ? String.valueOf(place.getRating()) : "Sin rating";
                    String website = place.getWebsiteUri() != null ? String.valueOf(place.getWebsiteUri()) : "No disponible";

                    // Crear marcador con la info del POI
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(poi.latLng)
                            .title(nombre)
                            .snippet("📍 " + direccion + "\n📞 " + telefono + "\n⭐ " + rating + "\n\uD83C\uDF10 " + website)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)) // Cambiar color
                    );

                    // Mostrar la info del marcador automáticamente
                    if (marker != null) {
                        marker.showInfoWindow();
                    }
                }).addOnFailureListener((exception) -> {
                    Log.e("GooglePlaces", "Error obteniendo detalles del lugar", exception);
                });
            }
        });

       /* mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Vuelve a habilitar el desplazamiento del ScrollView cuando se deja de interactuar con el mapa
                scrollView.requestDisallowInterceptTouchEvent(false);
            }
        });*/
        // Configura el mapa y permite que los usuarios seleccionen una ubicación.
        // Setear una ubicacion inicial. Necesario recoger la ubicación del usuario.
        if (latitud != 0 || longitud != 0){
            inicial = new LatLng(latitud, longitud);
            configurarMapa();
        }else{
            String creadorCiudad = creador.getCiudad();
            ObtenerCoordenadasProvincia(creadorCiudad);
        }

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        // Deshabilita el desplazamiento del ScrollView
        scrollView.requestDisallowInterceptTouchEvent(true);

        // Recoger la latidud y longitud del lugar pulsado en el mapa.
        latitud = latLng.latitude;
        longitud = latLng.longitude;

        lugar = "Lugar Personalizado";

        // Borrar marcador inicial y añadir uno donde se pulse.
        mMap.clear();
        //LatLng ubicacion = new LatLng()
        mMap.addMarker(new MarkerOptions().position(latLng).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }


    private void getEtiquetas(String titulo, String descripcion, String fecha, String hora, int participantesNecesarios, int duracion, double precio, int idCreador){
        ApiService apiService = ApiClient.getApiService(this);
        List<String> etiquetasSeleccionadas = new ArrayList<>();
        etiquetasSeleccionadas.add(filtroIntesidad);
        etiquetasSeleccionadas.add(filtroObjetivo);
        etiquetasSeleccionadas.add(filtroParticipantes);
        etiquetasSeleccionadas.add(filtroUbicacion);

        Call<Set<Etiqueta>> call = apiService.comprobarEtiquetas(etiquetasSeleccionadas);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Set<Etiqueta>> call, Response<Set<Etiqueta>> response) {
                if (response.isSuccessful()) {
                    if(response.body() != null){
                        Log.e("API", "Etiquetas seleccionadas comprobadas");
                        etiquetas = response.body();
                        crearActividad(titulo, descripcion, idDeporte, fecha, hora, participantesNecesarios,
                                duracion, precio, latitud, longitud, lugar, idCreador);
                    }else {
                        Log.e("API", "Las etiquetas están mal configuradas.");
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Set<Etiqueta>> call, Throwable t) {
                Log.e("API", "Error al comprobar las etiquetas: " + t.getMessage());
            }
        });
    }

    private String limpiarTextoChip(String texto) {
        return texto.replaceAll("[^\\p{L}\\p{N}]+", "").trim();
    }

    private void ObtenerCoordenadasProvincia(String provincia){
        GeocodingTask.getCoordinates(provincia + ", España", new GeocodingTask.GeocodingCallback() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                Log.d("Geocoding", "Latitud: " + latitude + ", Longitud: " + longitude);
                inicial = new LatLng(latitude, longitude);
                configurarMapa();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Geocoding", "Error: " + errorMessage);
            }
        });

    }


    private void configurarMapa() {
        // Asegúrate de que mMap no sea null antes de usarlo
        if (mMap != null && inicial != null) {
            // Añadir un marcador
            mMap.addMarker(new MarkerOptions().position(inicial).title("Ubicación actual"));

            // Dependiendo de si tenemos el acceso a la ubicación o no ponemos un zoom diferente.
            float zoom = locationTrigger ? 14.0f : 10.0f;

            // Mover mapa a la ubicación inicial
            mMap.moveCamera(CameraUpdateFactory.newLatLng(inicial));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(inicial, zoom));
        } else {
            Log.e("Geocoding", "mMap o inicial son null al configurar el mapa");
        }
    }


}