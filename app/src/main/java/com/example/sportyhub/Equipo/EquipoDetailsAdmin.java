package com.example.sportyhub.Equipo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Adapters.EquipoMiembroAdminAdapter;
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

public class EquipoDetailsAdmin extends AppCompatActivity {
    private Equipo equipo;
    private Usuario usuario;
    private RecyclerView recyclerViewMiembros;
    private TextView nombreEquipo, descripcionEquipo;
    private Button btnEliminarEquipo, btnEditarEquipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo_details_admin);

        equipo = getIntent().getParcelableExtra("equipo", Equipo.class);
        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);

        nombreEquipo = findViewById(R.id.nombreEquipo);
        descripcionEquipo = findViewById(R.id.descripcionEquipo);
        recyclerViewMiembros = findViewById(R.id.recyclerViewMiembros);
        btnEliminarEquipo = findViewById(R.id.btnEliminarEquipo);
        btnEditarEquipo = findViewById(R.id.btnEditarEquipo);
        recyclerViewMiembros.setLayoutManager(new LinearLayoutManager(this));

        nombreEquipo.setText(equipo.getNombre());
        descripcionEquipo.setText(equipo.getDetalles());

        cargarMiembros();

        btnEliminarEquipo.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("¿Estás seguro?")
                    .setMessage("¿Estás seguro de eliminar el equipo? Esta acción no puede deshacerse.")
                    .setPositiveButton("Eliminar el equipo", (dialog2, which2) ->
                            eliminarEquipo())
                    .setNegativeButton("Cancelar", (dialog2, which2) -> {
                        dialog2.dismiss();});
        });
        btnEditarEquipo.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "Por incorporar", Toast.LENGTH_SHORT).show());
    }

    private void cargarMiembros() {
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<List<EquipoMiembro>> call = apiService.obtenerMiembrosPorEquipo(equipo.getIdequipo());

        call.enqueue(new Callback<List<EquipoMiembro>>() {
            @Override
            public void onResponse(Call<List<EquipoMiembro>> call, Response<List<EquipoMiembro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EquipoMiembro> listaMiembros = response.body();
                    EquipoMiembroAdminAdapter adapter = new EquipoMiembroAdminAdapter(listaMiembros, equipo.getIdequipo());
                    recyclerViewMiembros.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<EquipoMiembro>> call, Throwable t) {
                Toast.makeText(EquipoDetailsAdmin.this, "Error al cargar miembros", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarEquipo() {
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<Void> call = apiService.eliminarEquipo(equipo.getIdequipo());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EquipoDetailsAdmin.this, "Equipo eliminado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EquipoDetailsAdmin.this, "Error al eliminar equipo", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
