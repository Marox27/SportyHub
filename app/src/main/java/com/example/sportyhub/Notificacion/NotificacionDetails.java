package com.example.sportyhub.Notificacion;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Adapters.EquipoAdapter;
import com.example.sportyhub.Adapters.NotificationAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Equipo.EquipoDetails;
import com.example.sportyhub.Fragments.NotificationFragment;
import com.example.sportyhub.Fragments.UserFragment;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Notificacion;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionDetails extends AppCompatActivity {
    private List<Notificacion> listaNotificaciones;
    private Usuario usuario;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notificaciones);

        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);
        recyclerView = findViewById(R.id.recyclerViewNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageButton imageButton = findViewById(R.id.imMarcarTodasLeidas);

        // Agregar listener para mostrar el menú emergente
        imageButton.setOnClickListener(this::mostrarPopupMenu);


        obtenerNotificaciones();
    }

    private void prepararLayout(){
        adapter = new NotificationAdapter(listaNotificaciones, getApplicationContext());
        adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Notificacion notificacion = listaNotificaciones.get(position);
                Toast.makeText(getApplicationContext(), notificacion.getTitulo(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void mostrarPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.notification_menu, popup.getMenu());

        // Manejar clics en el menú
        popup.setOnMenuItemClickListener(item -> {
            int idItem = item.getItemId();

            if (idItem == R.id.action_refresh){
                listaNotificaciones.clear();
                obtenerNotificaciones();
                return true;
            } else if (idItem == R.id.action_mark_as_read) {
                marcarTodasComoLeidas();
                listaNotificaciones.clear();
                Toast.makeText(this, "Todas las notificaciones pendientes leídas.", Toast.LENGTH_SHORT).show();
                return true;
            } else if (idItem == R.id.action_all_notifications){
                listaNotificaciones.clear();
                obtenerTodasLasNotificaciones();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void marcarTodasComoLeidas() {
        if (listaNotificaciones == null || listaNotificaciones.isEmpty()) {
            Toast.makeText(this, "No hay notificaciones para marcar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Extraer los IDs de las notificaciones sin leer
        List<Integer> ids = new ArrayList<>();
        for (Notificacion notificacion : listaNotificaciones) {
            if (!notificacion.isLeida()) { // Solo las no leídas
                ids.add(notificacion.getIdNotificacion());
            }
        }

        if (ids.isEmpty()) {
            Toast.makeText(this, "Todas las notificaciones ya están leídas", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<Boolean> call = apiService.marcarNotificacionesLeidas(ids);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()){
                        Log.e("API", "Notificaciones marcadas como leidas.");
                        listaNotificaciones.clear();
                        prepararLayout();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e("API", "Error marcar las notificaciones: " + t.getMessage());
            }
        });
    }

    private void obtenerNotificaciones() {
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<List<Notificacion>> call = apiService.obtenerNotificacionesNoLeidas(usuario.getIdUsuario().intValue());

        call.enqueue(new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notificacion>> call, @NonNull Response<List<Notificacion>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    listaNotificaciones = response.body();
                    Log.e("API", "Notificaciones obtenidas.");
                    prepararLayout();
                }else if (response.body()!= null && response.body().isEmpty()){
                    listaNotificaciones = new ArrayList<>();
                    Log.e("API", "Notificaciones Vacías.");
                    prepararLayout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notificacion>> call, @NonNull Throwable t) {
                Log.e("API", "Error al obtener notificaciones: " + t.getMessage());
            }
        });
    }


    private void obtenerTodasLasNotificaciones() {
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<List<Notificacion>> call = apiService.obtenerNotificaciones(usuario.getIdUsuario().intValue());

        call.enqueue(new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notificacion>> call, @NonNull Response<List<Notificacion>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    listaNotificaciones = response.body();
                    Log.e("API", "Notificaciones obtenidas.");
                    prepararLayout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Notificacion>> call, @NonNull Throwable t) {
                Log.e("API", "Error al obtener notificaciones: " + t.getMessage());
            }
        });
    }

}
