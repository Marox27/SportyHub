package com.example.sportyhub.Admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.sportyhub.Adapters.MainAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Fragments.ActivityListAdminFragment;
import com.example.sportyhub.Fragments.ActivityListFragment;
import com.example.sportyhub.Fragments.AdminOtherFragment;
import com.example.sportyhub.Fragments.ReportFragment;
import com.example.sportyhub.Fragments.TeamFragment;
import com.example.sportyhub.Fragments.UserFragment;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Reporte;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayoutMediator;
import com.paypal.pyplcheckout.data.model.pojo.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMainActivity extends AppCompatActivity {
    private List<Actividad>actividades;
    private List<Usuario>usuarios;
    private List<Equipo>equipos;
    private List<Reporte>reportes;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main_activity);
        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        cargarUsuarios();
        cargarActividades();
        cargarReportes();
        cargarEquipos();
    }

    private final NavigationBarView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_usuarios) {
                    // cargarInfo + fragment pertinente
                    selectedFragment = new UserFragment(usuarios, usuario);
                } else if (itemId == R.id.nav_actividades) {
                    selectedFragment = new ActivityListAdminFragment(actividades, usuario);
                } else if (itemId == R.id.nav_reportes) {
                    selectedFragment = new ReportFragment(reportes, usuario);
                } else if (itemId == R.id.nav_equipos) {
                    selectedFragment = new TeamFragment(equipos, usuario);
                } else if (itemId == R.id.nav_others) {
                    selectedFragment = new AdminOtherFragment();
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            };

    private void cargarUsuarios() {
        ApiService apiService = ApiClient.getApiService(this);
        Call<List<Usuario>> call = apiService.getAllUsers();

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    usuarios = response.body();

                    // Filtramos para crear una lista solo con los usuarios que no son administradores.
                    List<Usuario> listaUsuarios = new ArrayList<>();
                    for (Usuario usuario: usuarios) {
                        if (!usuario.isAdmin()){
                            listaUsuarios.add(usuario);
                        }
                    }

                    // Cargar el fragmento inicial (Usuarios)
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new UserFragment(listaUsuarios, usuario)).commit();
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("API", "Error al los usuarios: " + t.getMessage());
            }
        });
    }

    private void cargarActividades(){
        ApiService apiService = ApiClient.getApiService(this);
        Call<List<Actividad>> call = apiService.getActividades();

        call.enqueue(new Callback<List<Actividad>>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (response.isSuccessful()) {
                    actividades = response.body();
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                Log.e("API", "Error al cargar las actividades: " + t.getMessage());
            }
        });
    }

    private void cargarReportes(){
        ApiService apiService = ApiClient.getApiService(this);
        Call<List<Reporte>> call = apiService.obtenerReportesPendientes();

        call.enqueue(new Callback<List<Reporte>>() {
            @Override
            public void onResponse(Call<List<Reporte>> call, Response<List<Reporte>> response) {
                if (response.isSuccessful()) {
                    reportes = response.body();
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Reporte>> call, Throwable t) {
                Log.e("API", "Error al cargar los reportes pendientes: " + t.getMessage());
            }
        });
    }

    private void cargarEquipos(){
        ApiService apiService = ApiClient.getApiService(this);
        Call<List<Equipo>> call = apiService.getEquipos();

        call.enqueue(new Callback<List<Equipo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Equipo>> call, @NonNull Response<List<Equipo>> response) {
                if (response.isSuccessful()) {
                    equipos = response.body();
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Equipo>> call, Throwable t) {
                Log.e("API", "Error al cargar los equipos: " + t.getMessage());
            }
        });
    }

}

