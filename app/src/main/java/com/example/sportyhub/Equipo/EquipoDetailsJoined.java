package com.example.sportyhub.Equipo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Adapters.EquipoMiembroAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.MainActivity;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.EquipoMiembro;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Reporte.CrearReporte;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipoDetailsJoined extends AppCompatActivity {
    private Equipo equipo;
    private Usuario usuario;
    private int idMiembro;
    private RecyclerView recyclerViewMiembros;
    private TextView descripcionEquipo;
    private Toolbar toolbar;
    private ImageView imageView;
    Button btnAbandonar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo_details_joined);

        equipo = getIntent().getParcelableExtra("equipo", Equipo.class);
        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);
        idMiembro = getIntent().getIntExtra("idMiembro", 0);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        descripcionEquipo = findViewById(R.id.descripcionEquipo);
        imageView = findViewById(R.id.logoEquipo);
        btnAbandonar = findViewById(R.id.btnAbandonarEquipo);
        recyclerViewMiembros = findViewById(R.id.recyclerViewMiembros);
        recyclerViewMiembros.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setTitle(equipo.getNombre());

        descripcionEquipo.setText(equipo.getDetalles());
        // Cargar la imagen desde la URL usando Glide
        Glide.with(getApplicationContext())
                .load(equipo.getImagen())
                .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                .into(imageView);

        btnAbandonar.setOnClickListener(v -> abandonarEquipo());

        cargarMiembros();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.equipo_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reportar) {
            // Acción para editar el equipo
            Intent intent = new Intent(getApplicationContext(), CrearReporte.class);
            intent.putExtra("equipo_reportado",equipo);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
