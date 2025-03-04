package com.example.sportyhub.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sportyhub.Actividad.ActivityDetails;
import com.example.sportyhub.Adapters.ActividadAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Equipo.CrearEquipo;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Deporte;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityListFragment extends Fragment {

    // Componentes del Layout
// ================================================================================================
    private View rootView, filterView;
    private DrawerLayout drawerLayout;
    private ScrollView scrollView;
    private FrameLayout frameLayout;
    private ViewPager2 viewPager;
    private Button btnFiltros, btnAplicarFiltros, btnLimpiarFiltros, btnFecha;
    private Spinner spinnerDeporte;
    private TextView txtDistancia, txtDuracion;
    private RecyclerView recyclerView;
    private ActividadAdapter actividadAdapter;
    private Calendar selectedDate;
    private GoogleMap mMap;
    private ChipGroup chipGroupIntesidad, chipGroupObjetivo, chipGroupParticipantes, chipGroupUbicacion, chipGroupCoste;
    private RangeSlider sliderDuracion, sliderDistacia;
    private Circle mapaCirculo;

//==================================================================================================

    // Variables para los filtros
//==================================================================================================
    private int filtroDistancia = 0;
    private String filtroDeporte = "";

    // Variables para los filtros de coste
    private boolean filtroGratis = false;
    private boolean filtroPago = false;

    // Variable para los filtros de duración
    private boolean filtroCorta = false;
    private boolean filtroMedia = false;
    private boolean filtroLarga = false;

    // Variables para filtros
    private String filtroIntensidad = "";
    private String filtroObjetivo = "";
    private String filtroParticipantes = "";
    private String filtroUbicacion = "";
    private String filtroCoste = "";
    private String filtroFecha = "";
    private int filtroDuracion = 0;

//==================================================================================================

    // Variables lógica
    private double latitud, longitud, distancia = 50;
    private Set<String> etiquetasSeleccionadas = new HashSet<>();
    private List<Actividad> listaActividadesOriginal = new ArrayList<>();
    private List<Actividad> listaActividadesFiltrada = new ArrayList<>();
    private Usuario usuario;

    public ActivityListFragment() {
    }

    public ActivityListFragment(List<Actividad> actividades, Usuario usuario) {
        this.listaActividadesOriginal = actividades;
        this.usuario = usuario;
    }

    public ActivityListFragment(List<Actividad> actividades, Usuario usuario, double latitud, double longitud, double distancia) {
        this.listaActividadesOriginal = actividades;
        this.usuario = usuario;
        this.latitud = latitud;
        this.longitud = longitud;
        this.distancia = distancia;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_filters_drawer, container, false);

        // Inicializar el DrawerLayout correctamente
        drawerLayout = rootView.findViewById(R.id.drawer_layout);

        // Inicializar UI con rootView en lugar de filterView
        inicializarUI(rootView);
        configurarFiltros();
        configurarRecyclerView();
        //cargarActividades();

        return rootView;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void inicializarUI(View rootView) {
        btnFiltros = rootView.findViewById(R.id.btn_filtros);
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout); // Ahora lo obtiene del MainActivity

        // Elementos del filtro que están en el MainActivity
        btnAplicarFiltros = requireActivity().findViewById(R.id.btn_aplicar_filtros);
        btnLimpiarFiltros = requireActivity().findViewById(R.id.btn_limpiar_filtros);
        btnFecha = requireActivity().findViewById(R.id.btn_fecha);

        chipGroupIntesidad = requireActivity().findViewById(R.id.chipGroupIntensidad);
        chipGroupObjetivo = requireActivity().findViewById(R.id.chipGroupObjetivo);
        chipGroupParticipantes = requireActivity().findViewById(R.id.chipGroupParticipantes);
        chipGroupUbicacion = requireActivity().findViewById(R.id.chipGroupUbicacion);
        chipGroupCoste = requireActivity().findViewById(R.id.chipGroupCoste);
        //sliderDuracion = requireActivity().findViewById(R.id.slider_duracion);
        //txtDuracion = requireActivity().findViewById(R.id.tvDuracion);


        spinnerDeporte = requireActivity().findViewById(R.id.spinner_deporte);
        sliderDistacia = requireActivity().findViewById(R.id.slider_distancia);
        txtDistancia = requireActivity().findViewById(R.id.tvDistancia);
        scrollView = rootView.findViewById(R.id.Scroll_actividades);
        recyclerView = rootView.findViewById(R.id.recyclerViewActividadesParticipante);
        frameLayout = rootView.findViewById(R.id.fl_mapa_de_actividades);
        viewPager = requireActivity().findViewById(R.id.view_pager);


        frameLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false; // Permitir que el mapa maneje los eventos
        });

        btnFiltros.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
    }



    private void configurarFiltros() {
        cargarDeportes();
        if (spinnerDeporte != null) {
            // Configurar selector de deportes
            spinnerDeporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Deporte deporte = (Deporte)parent.getItemAtPosition(position);
                    filtroDeporte = deporte.getNombre();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    filtroDeporte = "TODOS";
                }
            });

            chipGroupIntesidad.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    Chip chip = group.findViewById(checkedIds.get(0));
                    filtroIntensidad = (chip != null) ? chip.getText().toString() : "";
                    if (!filtroIntensidad.equalsIgnoreCase(""))
                        etiquetasSeleccionadas.add(filtroIntensidad);
                } else {
                    filtroIntensidad = "";
                }
            });

            // Chipgroup objetivo Recreativo/Competitivo
            chipGroupObjetivo.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    Chip chip = group.findViewById(checkedIds.get(0));
                    filtroObjetivo = (chip != null) ? chip.getText().toString() : "";
                    if (!filtroObjetivo.equalsIgnoreCase(""))
                        etiquetasSeleccionadas.add(filtroObjetivo);
                } else {
                    filtroObjetivo = "";
                }
            });

            chipGroupParticipantes.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    Chip chip = group.findViewById(checkedIds.get(0));
                    filtroParticipantes = (chip != null) ? chip.getText().toString() : "";
                    if (!filtroParticipantes.equalsIgnoreCase(""))
                        etiquetasSeleccionadas.add(filtroParticipantes);
                } else {
                    filtroParticipantes = "";
                }
            });

            chipGroupUbicacion.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    Chip chip = group.findViewById(checkedIds.get(0));
                    filtroUbicacion = (chip != null) ? chip.getText().toString() : "";
                    if (!filtroUbicacion.equalsIgnoreCase(""))
                        etiquetasSeleccionadas.add(filtroUbicacion);
                } else {
                    filtroUbicacion = "";
                }
            });

            chipGroupCoste.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    Chip chip = group.findViewById(checkedIds.get(0));
                    if (chip != null) {
                        String chipCoste = chip.getText().toString();
                        filtroGratis = chipCoste.equalsIgnoreCase("Gratis");
                        filtroPago = chipCoste.equalsIgnoreCase("De Pago");
                    }
                } else {
                    filtroCoste = "";
                }
            });


            // Configurar Slider de distancia
            sliderDistacia.addOnChangeListener((slider, value, fromUser) -> {
                filtroDistancia = (int) value;
                txtDistancia.setText("Distancia máxima " + filtroDistancia + "(km)");
            });

           /* sliderDuracion.addOnChangeListener((slider, value, fromUser) -> {
                filtroDuracion = (int) value;
                txtDuracion.setText("Duración (minutos):\t" + value);
            });*/

            // Configurar selector de fecha
            btnFecha.setOnClickListener(view -> mostrarSelectorFecha());

            // Aplicar filtros al hacer clic
            btnAplicarFiltros.setOnClickListener(view -> {
                aplicarFiltros();
                drawerLayout.closeDrawer(GravityCompat.START);
            });

            // Limpiar filtros
            btnLimpiarFiltros.setOnClickListener(view -> limpiarFiltros());
        }
    }

    private void mostrarSelectorFecha() {
        final Calendar calendar = Calendar.getInstance();

        // Abrir el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    // Guardar la fecha en formato ISO 8601 (yyyy-MM-dd)
                    filtroFecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", yearSelected, monthOfYear + 1, dayOfMonth);
                    btnFecha.setText(filtroFecha);
                },
                calendar.get(Calendar.YEAR), // Año inicial
                calendar.get(Calendar.MONTH), // Mes inicial
                calendar.get(Calendar.DAY_OF_MONTH) // Día inicial
        );
        datePickerDialog.show();
    }

    private void limpiarFiltros() {
        spinnerDeporte.setSelection(0);
        // sliderDistacia.setValue(0);
        txtDistancia.setText("Distancia máxima (km):");

        chipGroupIntesidad.clearCheck();
        chipGroupObjetivo.clearCheck();
        chipGroupParticipantes.clearCheck();
        chipGroupUbicacion.clearCheck();
        chipGroupCoste.clearCheck();

        filtroDistancia = 50;
        filtroDeporte = "TODOS";
        filtroGratis = false;
        filtroPago = false;
        //filtroCorta = false;
        //filtroMedia = false;
        //filtroLarga = false;
        filtroFecha = "";

        etiquetasSeleccionadas.clear();
        btnFecha.setText("Seleccionar fecha");

        listaActividadesFiltrada.clear();
        listaActividadesFiltrada.addAll(listaActividadesOriginal);

        actividadAdapter = new ActividadAdapter(listaActividadesOriginal, getContext());
        recyclerView.setAdapter(actividadAdapter); // Restauramos la lista original
        actualizarMapa(listaActividadesOriginal);
        //cargarActividadesDe0();
    }


    private void aplicarFiltros() {
        List<Actividad> actividadesFiltradas = new ArrayList<>();
        distancia = filtroDistancia;

        for (Actividad actividad : listaActividadesOriginal) {
            boolean coincideEtiquetas = etiquetasSeleccionadas.isEmpty() || actividadTieneTodasLasEtiquetas(actividad);
            boolean coincideCosto = (!filtroGratis && !filtroPago) ||
                    (filtroGratis && actividad.getPrecio() == 0) ||
                    (filtroPago && actividad.getPrecio() > 0);

            boolean coincideFecha = (filtroFecha.isEmpty()) ||
                    actividad.getFecha().equals(filtroFecha);

            boolean coincideDeporte = (filtroDeporte.equalsIgnoreCase("TODOS")) ||
                    (filtroDeporte.equalsIgnoreCase(actividad.getDeporteName()));
            boolean coincideDistancia = (filtroDistancia == 0) || (calcularDistancia(actividad) <= filtroDistancia);

            Log.d("APP_DEBUG", coincideEtiquetas + " " + coincideCosto + " " + coincideFecha + " " +  filtroDeporte +coincideDeporte + " " + coincideDistancia);

            if (coincideEtiquetas && coincideCosto && coincideFecha && coincideDeporte && coincideDistancia) {
                actividadesFiltradas.add(actividad);
            }
        }

        etiquetasSeleccionadas.clear();
        listaActividadesFiltrada.clear();
        listaActividadesFiltrada.addAll(actividadesFiltradas);

        actividadAdapter = new ActividadAdapter(actividadesFiltradas, getContext());
        recyclerView.setAdapter(actividadAdapter); // Restauramos la lista original
        //actividadAdapter.updateActividades(new ArrayList<>(listaActividadesFiltrada)); // Pasamos una copia para evitar modificar la lista original
        actualizarMapa(actividadesFiltradas);
    }

    private void comprobarEtiquetasSeleccionadas(String filtroIntensidad, String filtroObjetivo,
                                                 String filtroParticipantes, String filtroUbicacion) {

        String [] listaEtiquetas = {filtroIntensidad, filtroObjetivo, filtroParticipantes, filtroUbicacion};
        etiquetasSeleccionadas.clear();

        for (int i = 0; i < listaEtiquetas.length; i++) {
            Log.d("APP_ETIQUETA_CHECK", listaEtiquetas[i]);
            if (!listaEtiquetas[i].equals("")){
                etiquetasSeleccionadas.add(listaEtiquetas[i]);
            }
        }
    }

    // Método para verificar si la actividad tiene todas las etiquetas seleccionadas
    private boolean actividadTieneTodasLasEtiquetas(Actividad actividad) {
        Set<String> etiquetasActividad = new HashSet<>(actividad.getEtiquetasString()); // Obtener etiquetas de la actividad
        Log.d("APP_ETIQUETAS_ACTIVIDAD", etiquetasActividad.toString());
        Log.d("APP_ETIQUETAS_SELECCIONADAS", etiquetasSeleccionadas.toString() + " " + etiquetasActividad.containsAll(etiquetasSeleccionadas));
        return etiquetasActividad.containsAll(etiquetasSeleccionadas); // Comprobar si tiene todas las seleccionadas
    }


    // Método para calcular distancia entre actividad y ubicación del usuario
    private double calcularDistancia(Actividad actividad) {
        LatLng ubicacionActividad = new LatLng(actividad.getLatitud(), actividad.getLongitud());
        LatLng ubicacionUsuario = new LatLng(latitud, longitud);
        double distanciaUbicaciones = SphericalUtil.computeDistanceBetween(ubicacionUsuario, ubicacionActividad) / 1000; // Convertir metros a km
        Log.d("DEBUG_DISTANCIA_FILTRO", "Distancia entre " + ubicacionActividad.toString()
                + " y " + ubicacionUsuario.toString() + " es de " + distanciaUbicaciones);
        return distanciaUbicaciones;
    }


    private void configurarRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        actividadAdapter = new ActividadAdapter(listaActividadesOriginal, requireContext());
        recyclerView.setAdapter(actividadAdapter);

        actividadAdapter.setOnItemClickListener(position -> {
            if (!listaActividadesOriginal.isEmpty()) {
                Actividad actividadSeleccionada = listaActividadesOriginal.get(position);
                Intent intent = new Intent(getActivity(), ActivityDetails.class);
                intent.putExtra("actividad_seleccionada", actividadSeleccionada);
                intent.putExtra("usuario_actual", usuario);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No hay actividades disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarMapa(Bundle savedInstanceState) {
        MapView mapView = filterView.findViewById(R.id.mapa_de_actividades);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            if (googleMap != null) {
                mMap = googleMap;
                actualizarMapa(listaActividadesOriginal); // Llama a tu método para actualizar el mapa
            } else {
                Log.e("MapError", "No se pudo obtener el GoogleMap.");
            }
        });
    }

    private void actualizarMapa(List<Actividad> actividades) {
        if (mMap!=null) mMap.clear();

        if (latitud != 0 && longitud != 0) {
            LatLng center = new LatLng(latitud, longitud);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 8)); // Zoom 15 para vista cercana

            // Deshabilita el desplazamiento del ScrollView cuando el mapa se mueve
            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
            });


            // Si hay un círculo anterior, eliminarlo
            if (mapaCirculo != null) {
                mapaCirculo.remove();
            }

            if (distancia == 0) distancia = 50;
            // Dibujar nuevo círculo en el mapa
            CircleOptions circleOptions = new CircleOptions()
                    .center(center)
                    .radius(distancia * 1000)
                    .strokeWidth(10)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(70, 200, 0, 255));

            // Guardar el nuevo círculo en la variable
            mapaCirculo = mMap.addCircle(circleOptions);
        }

        // Mostrar actividades en el mapa
        if (actividades != null && !actividades.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Actividad actividad : actividades) {
                LatLng coordenadas = new LatLng(actividad.getLatitud(), actividad.getLongitud());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(coordenadas)
                        .title(actividad.getDeporteName() + ": " + actividad.getTitulo())
                        .snippet("Fecha: " + actividad.getFecha() + "\nHora: " + actividad.getHora() + "\nPrecio: " + actividad.getPrecio());

                // Crear el marcador y asociar el objeto Actividad al marcador usando setTag
                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(actividad);  // Aquí es donde se asocia la actividad al marcador

                builder.include(coordenadas);
            }
            if (latitud == 0 && longitud == 0) {
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        // Configurar el listener para el click en el marcador
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Obtener la actividad asociada al marcador
                Actividad actividad = (Actividad) marker.getTag();

                // Crear el Intent para la actividad de detalles
                Intent intent = new Intent(getContext(), ActivityDetails.class);

                // Pasar los detalles de la actividad al Intent
                intent.putExtra("actividad_seleccionada", actividad);
                intent.putExtra("usuario_actual", usuario);

                // Iniciar la actividad de detalles
                startActivity(intent);
                return true; // Indica que hemos manejado el click
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapView mapView = view.findViewById(R.id.mapa_de_actividades);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(googleMap -> {
                if (googleMap != null) {
                    mMap = googleMap;
                    actualizarMapa(listaActividadesOriginal);
                } else {
                    Log.e("MapError", "No se pudo obtener el GoogleMap.");
                }
            });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        MapView mapView = requireActivity().findViewById(R.id.mapa_de_actividades);
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MapView mapView = requireActivity().findViewById(R.id.mapa_de_actividades);
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MapView mapView = requireActivity().findViewById(R.id.mapa_de_actividades);
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MapView mapView = requireActivity().findViewById(R.id.mapa_de_actividades);
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapView mapView = requireActivity().findViewById(R.id.mapa_de_actividades);
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MapView mapView = requireActivity().findViewById(R.id.mapa_de_actividades);
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    private void cargarActividades() {
        // Obtén el cliente configurado con el interceptor
        ApiService apiService = ApiClient.getApiService(getContext());
        Call<List<Actividad>> call = apiService.getActividades();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (response.isSuccessful()) {
                    listaActividadesFiltrada = response.body();
                } else if (response.code() == 403) {
                    // Si el token no es válido o ha expirado
                    Log.e("API", "Token inválido o expirado");
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Log.e("API", "Error al cargar actividades: " + t.getMessage());
            }
        });
    }

    private void cargarDeportes() {
        ApiService apiService = ApiClient.getApiService(getContext());
        Deporte deporte = new Deporte();
        deporte.setNombre("TODOS");
        List<Deporte> deportes = new ArrayList<>();
        deportes.add(deporte);

        apiService.obtenerDeportes().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Deporte>> call, Response<List<Deporte>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deportes.addAll(response.body());
                    Log.d("API", "Deportes recibidos: " + deportes.size());

                    // Crear un ArrayAdapter personalizado
                    ArrayAdapter<Deporte> adapter = new ArrayAdapter<>(requireActivity().getApplicationContext(),
                            R.layout.item_spinner, deportes) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            // Solo mostramos el nombre del deporte en el Spinner
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setText(deportes.get(position).getNombre());
                            return textView;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            // Configuraramos el diseño de cada opción desplegable
                            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                            textView.setText(deportes.get(position).getNombre());
                            return textView;
                        }
                    };
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDeporte.setAdapter(adapter);
                }else{
                    Log.e("API", "Respuesta no exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Deporte>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void updateData(List<Actividad> actividades) {
        this.listaActividadesOriginal = actividades;
        if (actividadAdapter != null) {
            actividadAdapter.updateData(actividades);
            actividadAdapter.notifyDataSetChanged();
        }
    }

}
