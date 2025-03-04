package com.example.sportyhub;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sportyhub.Actividad.CrearActividad;
import com.example.sportyhub.Adapters.MainAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Api.TokenManager;
import com.example.sportyhub.Equipo.CrearEquipo;
import com.example.sportyhub.Fragments.ActivityUserListFragment;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Notificacion;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.Notificacion.NotificacionDetails;
import com.example.sportyhub.UserConfig.Ajustes;
import com.example.sportyhub.UserConfig.DeportesActivity;
import com.example.sportyhub.UserConfig.EditProfile;
import com.example.sportyhub.Utilidades.GeocodingTask;
import com.example.sportyhub.Utilidades.NotificacionWorker;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ActivityUserListFragment.OnActivityInteractionListener{
    FloatingActionButton fab;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    DrawerLayout drawerLayout, drawerLayoutFilter;
    MainAdapter mainAdapter;
    ImageView userPfp, userIcon, notificationIcon;

    List<Actividad> actividades;
    List<Actividad> actividades_usuario = new ArrayList<>();
    List<Equipo> equipos = new ArrayList<>();
    List<Equipo> equiposUsuario = new ArrayList<>();
    List<Notificacion> notificaciones;

    private Usuario usuario;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient fusedLocationClient;

    double latitud, longitud, distancia;

    int id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_main);

        // Comprobamos de que el usuario tenga un token válido, de lo contrario de vuelta al login
        TokenManager tokenManager = new TokenManager(getApplicationContext());
        String token = tokenManager.getToken();
        if (token == null) {
            // Redirigir al login si no hay token
            Toast.makeText(this, "La sesión ha caducado. Vuelva a iniciar sesión.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        //OneTimeWorkRequest testWork = new OneTimeWorkRequest.Builder(NotificacionWorker.class).build();
        //WorkManager.getInstance(getApplicationContext()).enqueue(testWork);



        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);
        id_usuario = usuario.getIdUsuario().intValue();
        distancia = 50000;  //Distancia por defecto de 20km

        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.getWorkInfosForUniqueWorkLiveData("notificacionesWorker")
                .observe(this, workInfos -> {
                    if (workInfos == null || workInfos.isEmpty()) {
                        Log.d("Worker", "No hay workers encolados");
                    } else {
                        for (WorkInfo workInfo : workInfos) {
                            Log.d("Worker", "Estado del Worker: " + workInfo.getState());
                        }
                    }
                });

        // Iniciamos el worker de las notificaciones el cual trabajara siempre en segundo plano.
        iniciarWorkerNotificaciones();


        fab = findViewById(R.id.fab);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        notificationIcon = findViewById(R.id.adminIcon);

        setUserPfp(usuario);

        // Inicializar el lanzador para solicitar permisos
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configura un listener para el cambio de página en el ViewPager
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
           @Override
           public void onPageSelected(int position) {
               super.onPageSelected(position);
               // Cambia la función del FAB según la pestaña seleccionada
               switch (position) {
                   case 0: // Pestaña actividades
                       fab.setVisibility(View.GONE);
                       break;

                   case 1: // Pestaña Mis Actividades
                       fab.setVisibility(View.VISIBLE);
                       fab.setOnClickListener(view -> {
                           // Iniciamos la nueva actividad
                           Intent intent = new Intent(getApplicationContext(), CrearActividad.class);
                           intent.putExtra("username", usuario.getNickname());
                           intent.putExtra("usuario", usuario);
                           intent.putExtra("latitud", latitud);
                           intent.putExtra("longitud", longitud);
                           startActivity(intent);
                       });
                       break;

                   case 2: // Pestaña Equipos
                       fab.setVisibility(View.GONE);
                       break;

                   case 3: // Pestaña Mis Equipos
                       fab.setVisibility(View.VISIBLE);
                       fab.setOnClickListener(view -> {
                           // Iniciar la nueva actividad
                           Intent intent = new Intent(getApplicationContext(), CrearEquipo.class);
                           intent.putExtra("usuario", usuario);
                           startActivity(intent);
                       });
                       break;
               }
           }
        });

        cargarNotificaciones();
        cargarActividadesUsuario(id_usuario);
        cargarEquiposUsuario(id_usuario);

        userPfp.setOnClickListener(view -> {
            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.navigation_view);

            // Configurar toggle para abrir y cerrar el Drawer
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            // Abrir el menú lateral al tocar el icono de usuario
            userPfp.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));

            // Configurar clics en las opciones del menú
            navigationView.setNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if(itemId == R.id.nav_deportes){
                    Intent intent = new Intent(getApplicationContext(), DeportesActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.nav_notifications) {
                    Log.d("APP_DEBUG", "Apartado de notificaciones del usuario");
                    Intent intent = new Intent(getApplicationContext(), NotificacionDetails.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                } else if (itemId == R.id.nav_settings) {
                    Intent intent = new Intent(getApplicationContext(), Ajustes.class);
                    intent.putExtra("usuario", usuario);
                    startActivity(intent);
                } else if (itemId == R.id.nav_logout) {
                    Toast.makeText(this, "Cerrando sesión", Toast.LENGTH_SHORT).show();
                    cerrarSesion();
                }
                drawerLayout.closeDrawers(); // Cierra el menú
                return true;
            });

        });

        // Intentar obtener la ubicación antes de cargar actividades
        obtenerUbicacionYActualizar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("usuario", usuario);
        outState.putDouble("latitud", latitud);
        outState.putDouble("longitud", longitud);
        outState.putInt("id_usuario", id_usuario);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        usuario = savedInstanceState.getParcelable("usuario", Usuario.class);
        latitud = savedInstanceState.getDouble("latitud");
        longitud = savedInstanceState.getDouble("longitud");
        id_usuario = savedInstanceState.getInt("id_usuario");

        // Recargar datos
        setUserPfp(usuario);
        cargarActividadesUsuario(id_usuario);
        cargarEquiposUsuario(id_usuario);
        cargarNotificaciones();
    }

    private void cargarActividadesUsuario(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<List<Actividad>> call = apiService.getActividadesUsuario(idUsuario);

        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (response.isSuccessful() && !response.body().isEmpty()) {

                    // Manejar las actividades del usuario
                    for (Actividad actividad : response.body()) {
                        if (actividad.isActivo()){
                            actividades_usuario.add(actividad);
                        }
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Log.e("API", "Error al cargar actividades del usuario: " + t.getMessage());
            }
        });
    }

    private void cargarEquiposUsuario(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<List<Equipo>> call = apiService.getEquiposUsuario(idUsuario);

        call.enqueue(new Callback<List<Equipo>>() {
            @Override
            public void onResponse(Call<List<Equipo>> call, Response<List<Equipo>> response) {
                if (response.isSuccessful()) {
                    equiposUsuario = response.body();
                    // Manejar las actividades del usuario
                    for (Actividad actividad : actividades_usuario) {
                        System.out.println(actividad);
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Equipo>> call, Throwable t) {
                Log.e("API", "Error al cargar equipos del usuario: " + t.getMessage());
            }
        });
    }

    public CompletableFuture<List<Actividad>> cargarActividadesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Actividad>> response = ApiClient.getApiService(this).getActividades().execute();
                if (response.isSuccessful()) {
                    for (Actividad actividad:response.body()) {
                        System.out.println(actividad.getIdActividad());
                    }
                    return response.body();
                } else {
                    Log.e("API", "Error al cargar actividades: " + response.code());
                    return new ArrayList<>();
                }
            } catch (Exception e) {
                Log.e("API", "Error al ejecutar la solicitud: " + e.getMessage());
                return new ArrayList<>();
            }
        });
    }


    private CompletableFuture<List<Actividad>> cargarActividadesCercanasAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.d("DEBUG_CERCANAS_MAIN", latitud + "," + longitud +"," + distancia);
                Response<List<Actividad>> response = ApiClient.getApiService(this)
                        .getActividadesCercanas(latitud, longitud, distancia).execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<Actividad> actividadesActivas = new ArrayList<>();

                    for (Actividad actividad: response.body()) {
                        if (actividad.isActivo()){
                            actividadesActivas.add(actividad);
                        }
                    }
                    return actividadesActivas;
                } else {
                    Log.e("API", "Error al cargar actividades cercanas: " + response.code());
                    return new ArrayList<>();
                }
            } catch (IOException e) {
                Log.e("API", "Error al ejecutar solicitud de actividades cercanas", e);
                return new ArrayList<>();
            }
        });
    }


    private CompletableFuture<List<Actividad>> cargarActividadesUsuarioAsync(int idUsuario) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Actividad>> response = ApiClient.getApiService(this)
                        .getActividadesUsuario(idUsuario).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                    return new ArrayList<>();
                }
            } catch (IOException e) {
                Log.e("API", "Error en la llamada asíncrona", e);
                return new ArrayList<>();
            }
        });
    }



    private CompletableFuture<List<Equipo>> cargarEquiposAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Response<List<Equipo>> response = ApiClient.getApiService(this)
                        .getEquipos().execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                } else {
                    Log.e("API", "Error al cargar equipos: " + response.code());
                    return new ArrayList<>();
                }
            } catch (IOException e) {
                Log.e("API", "Error al ejecutar solicitud de equipos", e);
                return new ArrayList<>();
            }
        });
    }

    @Override
    public void recargarActividadesUsuario() {
        cargarActividadesUsuario(id_usuario);
    }

    // Si el token ha expirado o no es válido volvemos a la actividad del login.
    private void redirectToLogin() {
        TokenManager tokenManager = new TokenManager(this);
        tokenManager.clearToken(); // Borra el token actual
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish(); // Cierra la actividad actual
    }

    private void actualizarAdaptador() {
        if (mainAdapter == null) {
            mainAdapter = new MainAdapter(getSupportFragmentManager(), getLifecycle(), actividades, equipos, actividades_usuario, usuario);
            viewPager.setAdapter(mainAdapter);
        } else {
            mainAdapter.updateData(actividades, equipos, actividades_usuario, equiposUsuario, usuario,0,0);
        }
    }




    // Se encarga cargar y fijar los ImageViews del layout con la foto de perfil del usuario.
    private void setUserPfp(Usuario usuario){
        userPfp = findViewById(R.id.userPfp);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0); // Obtenemos el header para poder obtener el IV
        userIcon = headerView.findViewById(R.id.header_pfp);
        TextView headerUsername = headerView.findViewById(R.id.header_text);
        String userImageUrl = usuario.getPfp();

        Glide.with(this)
                .load(userImageUrl)
                .apply(RequestOptions.circleCropTransform()) // Transformación circular
                .placeholder(R.drawable.default_pfp) // Imagen de carga mientras se descarga
                .error(R.drawable.error_placeholder) // Imagen a mostrar si falla la descarga
                .into(userPfp);
        userPfp.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // También para la imagen del menú lateral
        if (userIcon != null ) {
            Glide.with(this)
                    .load(userImageUrl)
                    .apply(RequestOptions.circleCropTransform()) // Transformación circular
                    .placeholder(R.drawable.default_pfp) // Imagen de carga mientras se descarga
                    .error(R.drawable.error_placeholder) // Imagen a mostrar si falla la descarga
                    .into(userIcon);
            userIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        headerUsername.setText("Bienvenido, " + usuario.getNombre());
    }


    private void editarPerfil(){
        Intent intent = new Intent(MainActivity.this, EditProfile.class);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
    }

    // Se borran las credenciales guardadas del usuario y lo devuelve a la actividad de Login
    private void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Elimina todos los datos
        editor.apply();

        // Vuelve a la pantalla de inicio de sesión
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void cargarActividadesYCargarUI() {
        CompletableFuture<List<Actividad>> actividadesFuture = cargarActividadesCercanasAsync();
        CompletableFuture<List<Equipo>> equiposFuture = cargarEquiposAsync();

        actividadesFuture.thenAcceptBothAsync(equiposFuture, (actividadesResult, equiposResult) -> {
            runOnUiThread(() -> {
                actividades = actividadesResult;
                equipos = equiposResult;

                mainAdapter = new MainAdapter(getSupportFragmentManager(), getLifecycle(), actividades, equipos, actividades_usuario, equiposUsuario, usuario, latitud, longitud);
                viewPager.setAdapter(mainAdapter);

                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                    View customView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

                    ImageView tabIcon = customView.findViewById(R.id.tab_icon);
                    TextView tabText = customView.findViewById(R.id.tab_text);

                    tabIcon.setImageResource(MainAdapter.getTabIcon(position));
                    tabText.setText(MainAdapter.getTabTitles(position));

                    tab.setCustomView(customView);
                }).attach();

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        View customView = tab.getCustomView();
                        if (customView != null) {
                            ImageView tabIcon = customView.findViewById(R.id.tab_icon);
                            tabIcon.setColorFilter(ContextCompat.getColor(customView.getContext(), R.color.mainOrange));
                            TextView tabText = customView.findViewById(R.id.tab_text);
                            tabText.setTextColor(ContextCompat.getColor(customView.getContext(), R.color.mainOrange));
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        View customView = tab.getCustomView();
                        if (customView != null) {
                            ImageView tabIcon = customView.findViewById(R.id.tab_icon);
                            tabIcon.setColorFilter(ContextCompat.getColor(customView.getContext(), R.color.bottom_toolbar_text_color));
                            TextView tabText = customView.findViewById(R.id.tab_text);
                            tabText.setTextColor(ContextCompat.getColor(customView.getContext(), R.color.bottom_toolbar_text_color));
                        }
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) { }
                });

                // ✅ Marcar la primera pestaña como seleccionada al inicio
                TabLayout.Tab firstTab = tabLayout.getTabAt(0);
                if (firstTab != null) {
                    View customView = firstTab.getCustomView();
                    if (customView != null) {
                        ImageView tabIcon = customView.findViewById(R.id.tab_icon);
                        tabIcon.setColorFilter(ContextCompat.getColor(customView.getContext(), R.color.mainOrange));
                        TextView tabText = customView.findViewById(R.id.tab_text);
                        tabText.setTextColor(ContextCompat.getColor(customView.getContext(), R.color.mainOrange));
                    }
                }


            });
        });
    }

    private void cargarNotificaciones(){
        ApiService apiService = ApiClient.getApiService(this);
        Call<List<Notificacion>> call = apiService.obtenerNotificaciones(usuario.getIdUsuario().intValue());

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Notificacion>> call, @NonNull Response<List<Notificacion>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        notificaciones = response.body();
                        // Si se encuentra una notificación sin leer se hace visible el icono de alerta.
                        for (Notificacion notificacion: notificaciones) {
                            if (!notificacion.isLeida()){
                                Log.d("APP_DEBUG", "Icono alerta notificacion activdado");
                                notificationIcon.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                    }
                } else if (response.code() == 403) {
                    // Si el token no es válido o ha expirado
                    Log.e("API", "Token inválido o expirado");
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notificacion>> call, @NonNull Throwable t) {
                Log.e("API", "Error al obtener las notificaciones del usuario: " + t.getMessage());
            }
        });
    }

    private void iniciarWorkerNotificaciones(){
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        PeriodicWorkRequest notificacionWork = new PeriodicWorkRequest.Builder(
                NotificacionWorker.class,
                15, TimeUnit.MINUTES) // Se ejecutará cada 15 min
                .build();

        workManager.enqueueUniquePeriodicWork(
                "notificacionesWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                notificacionWork
        );
    }




    /*==============================================================================================
    *  FUNCIONES RELACIONADAS CON LA UBICACIÓN
    * ============================================================================================*/
    private void obtenerUbicacionYActualizar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                    Log.d("Ubicación", "Latitud: " + latitud + ", Longitud: " + longitud);

                    cargarActividadesYCargarUI();
                } else {
                    // Si no hay ubicación, obtener de la provincia
                    ObtenerCoordenadasProvincia(usuario.getCiudad());
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            ObtenerCoordenadasProvincia(usuario.getCiudad());
        }
    }


    /**
     * Comprueba si la app tiene permisos de ubicación. Si no, los solicita.
     * Si los tiene, verifica si la ubicación está activada.
     */
    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si el permiso ya está concedido, verificamos si la ubicación está activada
            checkIfLocationIsEnabled();
        } else {
            // Si no tiene permiso, lo solicita al usuario
            ObtenerCoordenadasProvincia(usuario.getCiudad());
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    /**
     * Comprueba si la ubicación (GPS) está activada en el dispositivo.
     * Si no está activada, solicita al usuario que la active.
     */
    private void checkIfLocationIsEnabled() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000) // Intervalo más rápido de actualización (5s)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        ((Task<?>) task).addOnSuccessListener(this, locationSettingsResponse -> {
            // Si la ubicación está activada, iniciamos la obtención de coordenadas
            requestLocationUpdates();
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    // Si la ubicación no está activada, mostramos un diálogo para que el usuario la active
                    ((ResolvableApiException) e).startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            } else {
                Toast.makeText(this, "La ubicación no está habilitada y no se puede " +
                        "cambiar automáticamente", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Maneja la respuesta del usuario cuando se le solicita activar la ubicación.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                // El usuario activó la ubicación, iniciamos la obtención de coordenadas
                requestLocationUpdates();
            } else {
                ObtenerCoordenadasProvincia(usuario.getCiudad());
                Toast.makeText(this, "Se necesita la ubicación para continuar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Maneja la respuesta del usuario cuando se le solicita el permiso de ubicación.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el usuario concedió el permiso, verificamos si la ubicación está activada
                checkIfLocationIsEnabled();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Inicia la solicitud de actualizaciones de ubicación y obtiene las coordenadas.
     */
    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // Intervalo de 10 segundos
                .setMinUpdateIntervalMillis(5000) // Intervalo más rápido de 5 segundos
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        Log.e("Coordenadas", "Latitud: " + latitud + ", Longitud: " + longitud);
                    }
                }
            }
        }, Looper.getMainLooper());
    }

    private void ObtenerCoordenadasProvincia(String provincia) {
        GeocodingTask.getCoordinates(provincia + ", España", new GeocodingTask.GeocodingCallback() {
            @Override
            public void onSuccess(double latitude, double longitude) {
                runOnUiThread(() -> {
                    latitud = latitude;
                    longitud = longitude;
                    Log.d("Geocoding", "Usando coordenadas de la provincia: Latitud: " + latitud + ", Longitud: " + longitud);
                    cargarActividadesYCargarUI();
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Geocoding", "Error obteniendo coordenadas: " + errorMessage);
                runOnUiThread(() -> cargarActividadesYCargarUI()); // Si falla, cargar UI sin ubicación
            }
        });
    }

}


