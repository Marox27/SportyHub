package com.example.sportyhub.Equipo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Adapters.EquipoMiembroAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.MainActivity;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.EquipoMiembro;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipoDetailsJoined extends AppCompatActivity {
    private Equipo equipo;
    private Usuario usuario;
    private int idMiembro;
    private RecyclerView recyclerViewMiembros;
    private TextView nombreEquipo, descripcionEquipo;
    Button btnAbandonar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo_details_joined);

        equipo = getIntent().getParcelableExtra("equipo", Equipo.class);
        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);
        idMiembro = getIntent().getIntExtra("idMiembro", 0);


        nombreEquipo = findViewById(R.id.nombreEquipo);
        descripcionEquipo = findViewById(R.id.descripcionEquipo);
        btnAbandonar = findViewById(R.id.btnAbandonarEquipo);
        recyclerViewMiembros = findViewById(R.id.recyclerViewMiembros);
        recyclerViewMiembros.setLayoutManager(new LinearLayoutManager(this));

        nombreEquipo.setText(equipo.getNombre());
        descripcionEquipo.setText(equipo.getDetalles());

        btnAbandonar.setOnClickListener(v -> abandonarEquipo());

        cargarMiembros();
    }

    private void abandonarEquipo() {
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<Void> call = apiService.eliminarMiembro(idMiembro);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EquipoDetailsJoined.this, "¡Has abandonado el equipo!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EquipoDetailsJoined.this, "Error al cargar miembros", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarMiembros() {
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<List<EquipoMiembro>> call = apiService.obtenerMiembrosPorEquipo(equipo.getIdequipo());

        call.enqueue(new Callback<List<EquipoMiembro>>() {
            @Override
            public void onResponse(Call<List<EquipoMiembro>> call, Response<List<EquipoMiembro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EquipoMiembro> listaMiembros = response.body();
                    EquipoMiembroAdapter adapter = new EquipoMiembroAdapter(listaMiembros);
                    recyclerViewMiembros.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<EquipoMiembro>> call, Throwable t) {
                Toast.makeText(EquipoDetailsJoined.this, "Error al cargar miembros", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
