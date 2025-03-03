package com.example.sportyhub.UserConfig;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Adapters.EquipoAdapter;
import com.example.sportyhub.Adapters.PagoAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Pago;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialPagosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PagoAdapter adapter;
    private List<Pago> pagosList;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_pagos_activity);
        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);

        recyclerView = findViewById(R.id.recyclerViewPagos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarListaDePagos();
    }

    public void cargarListaDePagos(){
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<List<Pago>> call = apiService.obtenerPagosDeUsuario(usuario.getIdUsuario().intValue());

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pagosList = response.body();
                    adapter = new PagoAdapter(pagosList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.d("APP_DEBUG", "ERROR AL CARGAR LOS PAGOS");
                }
            }
            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                Log.d("APP_DEBUG", "ERROR AL CARGAR LOS PAGOS", t);
            }
        });

    }

}

