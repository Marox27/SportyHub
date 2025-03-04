package com.example.sportyhub.Equipo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class EquipoDetails extends AppCompatActivity {

    ImageView equipo_imagen;
    TextView equipo_nombre, equipo_deporte, equipo_privacidad, equipo_miembros, equipo_detalles,
            equipo_provincia, equipo_municipio;
    RecyclerView recyclerView;
    Button button;
    boolean unido = false;;
    Usuario usuario = null;
    Equipo equipo = null;
    boolean isAdmin = false;

    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_screen);
        setContentView(R.layout.equipo_detalles);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        // Obtenemos la información que se ha pasado por el fragment.
        equipo = getIntent().getParcelableExtra("Equipo", Equipo.class);
        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);
        int id_usuario = usuario.getIdUsuario().intValue();
        isAdmin = usuario.isAdmin();


        equipo_imagen = findViewById(R.id.imageViewEquipo);
        equipo_nombre = findViewById(R.id.textViewEquipoNombre);
        equipo_deporte = findViewById(R.id.textViewDeporte);
        equipo_privacidad = findViewById(R.id.textViewPrivacidad);
        equipo_miembros = findViewById(R.id.textViewMiembros);
        equipo_provincia = findViewById(R.id.textViewProvincia);
        equipo_municipio = findViewById(R.id.textViewMunicipio);
        equipo_detalles = findViewById(R.id.textViewDetalles);
        recyclerView = findViewById(R.id.recyclerViewMiembros);
        button = findViewById(R.id.btnUnirse);

        int id_equipo = equipo.getIdequipo();
        cargarListaMiembros(id_equipo, id_usuario, button);

        // Datos del equipo cargado
        String nombre_equipo = equipo.getNombre();
        String deporte_equipo = equipo.getDeporteName();
        String privacidad_equipo = equipo.getPrivacidad();
        int miembros_equipo = equipo.getMiembros();
        String provincia_equipo = equipo.getProvincia();
        String municipio_equipo = equipo.getMunicipio();
        String detalles_equipo = equipo.getDetalles();
        String imagenUrl = equipo.getImagen();

        // Con los datos cargados ahora los ponemos en sus correspondientes views

        // Cargar la imagen desde la URL usando Glide
        Glide.with(this)
                .load(imagenUrl)
                .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                .into(equipo_imagen);

        equipo_nombre.setText(nombre_equipo);
        equipo_deporte.setText(deporte_equipo);
        equipo_privacidad.setText(privacidad_equipo);
        equipo_miembros.setText("" + miembros_equipo);
        equipo_provincia.setText(provincia_equipo);
        equipo_municipio.setText(municipio_equipo);
        equipo_detalles.setText(detalles_equipo);

        /*if (privacidad_equipo.equals("PÚBLICA")){
            button.setText("Unirse al equipo");
        }else {
            button.setText("Solicitar unirse");
        }*/

        button.setText(isAdmin ? "Eliminar equipo" : "Unirse al equipo");

        // Botón para unirse al equipo
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin){
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("¿Estás seguro?")
                            .setMessage("¿Estás seguro de eliminar el equipo? Esta acción no puede deshacerse.")
                            .setPositiveButton("Eliminar el equipo", (dialog2, which2) ->
                                    eliminarEquipo())
                            .setNegativeButton("Cancelar", (dialog2, which2) -> {
                                dialog2.dismiss();})
                            .show();

                }else{
                    if (unido){
                        Toast.makeText(getApplicationContext(), "¡Ya estás unido a este equipo!", Toast.LENGTH_SHORT).show();
                        button.setEnabled(false);
                    }
                    else if (privacidad_equipo.equals("PÚBLICA")){
                        unirseAEquipo(usuario, equipo);
                    }else {
                        Toast.makeText(getApplicationContext(), "¡Se ha solicitado la unión a este equipo!", Toast.LENGTH_SHORT).show();
                        // INSERTAR LA LÓGICA DE SOLICITUD DE UNIÓN
                        finish();
                    }
                }
            }
        });
    }


    @Override
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

    private void cargarListaMiembros(int idEquipo, int idUsuario, Button button) {
        ApiService apiService = ApiClient.getApiService(this);

        apiService.obtenerMiembrosPorEquipo(idEquipo).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<EquipoMiembro>> call, Response<List<EquipoMiembro>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("API","Miembros del equipo cargados.");
                    List<EquipoMiembro> listaMiembros = response.body();
                    EquipoMiembro usuario_participante = null;
                    int numero_miembros = listaMiembros.size();
                    boolean unido = false;

                    for (EquipoMiembro miembro: listaMiembros) {
                        if (miembro.getUsuario().getIdUsuario() == idUsuario){
                            unido = true;
                            usuario_participante = miembro;
                        }
                    }

                    // Actualizar la UI
                    if (unido) {
                        button.setEnabled(false);
                        Intent intent;
                        if (usuario_participante.getRol().equals(EquipoMiembro.Rol.ADMIN)){
                            intent = new Intent(getApplicationContext(), EquipoDetailsAdmin.class);
                        }else{
                            intent = new Intent(getApplicationContext(), EquipoDetailsJoined.class);
                        }

                        intent.putExtra("usuario", usuario);
                        intent.putExtra("equipo", equipo);
                        intent.putExtra("idMiembro", usuario_participante.getIdMiembro());
                        startActivity(intent);
                        finish();

                    }
                    EquipoMiembroAdapter adapter2 = new EquipoMiembroAdapter(listaMiembros);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(adapter2);

                    recyclerView.post(() -> {
                        int totalHeight = 0;
                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        if (adapter != null) {
                            for (int i = 0; i < adapter.getItemCount(); i++) {
                                View item = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i)).itemView;
                                item.measure(0, 0);
                                totalHeight += item.getMeasuredHeight();
                            }
                        }
                        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                        params.height = totalHeight;
                        recyclerView.setLayoutParams(params);
                    });

                    equipo_miembros.setText("" + numero_miembros);
                } else {
                    Toast.makeText(getApplicationContext(), "Error al cargar los miembros.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EquipoMiembro>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al cargar los miembros.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Función para crear un nuevo miembro al equipo dado el equipo y usuario mediante petición a la API.
    private void unirseAEquipo(Usuario idUsuario, Equipo idEquipo) {
         ApiService apiService = ApiClient.getApiService(this);
         EquipoMiembro Joinrequest = new EquipoMiembro(idEquipo, idUsuario, EquipoMiembro.Rol.MIEMBRO);
         Log.d("APP", "Datos: " + idEquipo.getIdequipo() + " " + idUsuario.getIdUsuario());

         apiService.crearMiembro(Joinrequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<EquipoMiembro> call, Response<EquipoMiembro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getApplicationContext(), "¡Te has unido al equipo!", Toast.LENGTH_SHORT).show();
                    // Crear el Intent para la actividad principal
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("usuario", usuario);
                    // Cerrar la actividad padre
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    // Cerrar la actividad hija
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "¡No se pudo unir al equipo!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EquipoMiembro> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al unirse al equipo.", Toast.LENGTH_SHORT).show();
                Log.e("API", "Error al unirse al equipo.");
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
                    Toast.makeText(getApplicationContext(), "Equipo eliminado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al eliminar equipo", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
