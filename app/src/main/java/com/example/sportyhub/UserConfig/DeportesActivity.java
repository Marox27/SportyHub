package com.example.sportyhub.UserConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Actividad.CrearActividad;
import com.example.sportyhub.Adapters.DeportesAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Modelos.Deporte;
import com.example.sportyhub.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeportesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DeportesAdapter adapter;
    private List<Deporte> deportesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deportes);

        recyclerView = findViewById(R.id.recyclerViewDeportes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        obtenerDeportes();
    }

    public void obtenerDeportes(){
        // Obtener la instancia de Retrofit y crear el servicio
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<List<Deporte>> call = apiService.obtenerDeportes();

        // Realizar la llamada a la API
        call.enqueue(new Callback<List<Deporte>>() {
            @Override
            public void onResponse(Call<List<Deporte>> call, Response<List<Deporte>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deportesList = response.body();
                    adapter = new DeportesAdapter(deportesList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                }else {
                    Log.e("API_ERROR", "Error en la respuesta de la API");
                }
            }
            @Override
            public void onFailure(Call<List<Deporte>> call, Throwable t) {
                Log.e("API_ERROR", "Error al conectarse a la API: " + t.getMessage());
            }
        });
    }


}

