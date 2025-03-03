package com.example.sportyhub.Reporte;

import static com.example.sportyhub.Login.mostrarSnackbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Admin.AdminMainActivity;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Reporte;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Usuario.UsuarioDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteDetails extends AppCompatActivity {

    ImageView ivUsuarioReportante, ivEntidadReportada;
    TextView tvNicknameUsuarioReportante, tvReporteMotivo, tvReporteFecha, tvReporteDescripcion,
            tvEntidadReportadaNombre, tvReporteStatus;
    Button buttonBanear, buttonAdvertencia, buttonCerrarReporte;
    Reporte reporte;
    Usuario usuario_reportado = null, usuarioAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_details);

        // Obtener datos del intent
        reporte = getIntent().getParcelableExtra("reporte", Reporte.class);
        usuarioAdmin = getIntent().getParcelableExtra("usuario_admin", Usuario.class);

        // Asignar los ImageViews del layout
        ivUsuarioReportante = findViewById(R.id.imageViewAvatarReportante);
        ivEntidadReportada = findViewById(R.id.imageViewAvatarReportado);

        // Asignar los TextViews del layout
        tvNicknameUsuarioReportante = findViewById(R.id.tvNicknameUsuarioReportante);
        tvReporteMotivo = findViewById(R.id.tvReporteMotivo);
        tvReporteDescripcion = findViewById(R.id.tvReporteDescripcion);
        tvReporteStatus = findViewById(R.id.tvReporteStatus);
        tvReporteFecha = findViewById(R.id.tvReporteFecha);
        tvEntidadReportadaNombre = findViewById(R.id.tvEntidadReportadaNombre);

        // Asignar los botones del layout
        buttonBanear = findViewById(R.id.buttonBanear);
        buttonAdvertencia = findViewById(R.id.buttonAdvertencia);
        buttonCerrarReporte = findViewById(R.id.buttonCerrarReporte);

        // Asignar datos al layout
        tvNicknameUsuarioReportante.setText(reporte.getUsuarioReportante().getNickname());
        tvReporteMotivo.setText(reporte.getMotivo());
        tvReporteDescripcion.setText(reporte.getDescripcion());
        tvReporteFecha.setText(reporte.getFechaCreacion());

        // Cargamos la imagen del usuario que ha realizado el reporte
        Glide.with(this)
                .load(reporte.getUsuarioReportante().getPfp())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.error_placeholder)
                .into(ivUsuarioReportante);

        // Cargamos la imagen de la entidad reportada
        establecerImagenEntidad();

        // Estado del reporte (revisado o no)
        if (reporte.isRevisado()) {
            tvReporteStatus.setText("Revisado");
            tvReporteStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        } else {
            tvReporteStatus.setText("Pendiente");
            tvReporteStatus.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }

        if (reporte.getUsuarioReportado() != null){
            usuario_reportado = reporte.getUsuarioReportado();
        }else if (reporte.getActividadReportada() != null){
            getUsuarioByID(reporte.getActividadReportada().getCreador());
        }else if(reporte.getEquipoReportado()!= null){
            getUsuarioByID(reporte.getEquipoReportado().getCreador());
        }


        // Configurar funcionalidad de los botones
        buttonBanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Banear a usuario
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Confirme su Acción")
                        .setMessage("¿Seguro que desea banear a este usuario?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            banearUsuario();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Cierra el diálogo si elige "No"
                        .show();

                // Eliminar equipo o actividad (POR DESARROLLAR)
            }
        });

        buttonAdvertencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CrearAviso.class);
                intent.putExtra("usuario_seleccionado", usuario_reportado);
                intent.putExtra("usuario_admin", usuarioAdmin);
                startActivity(intent);
            }
        });

        buttonCerrarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("¿Cerrar el reporte?")
                        .setMessage("Hacer esto no conllevará ninguna acción")
                        .setPositiveButton("Cerrar reporte", (dialog, which) -> {
                            cerrarReporte();
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()) // Cierra el diálogo si elige "No"
                        .show();
            }
        });


    }

    private void getUsuarioByID(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Usuario> call = apiService.getUser(idUsuario);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Usuario> call, @NonNull Response<Usuario> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        usuario_reportado = response.body();
                    }
                } else if (response.code() == 403) {
                    // Si el token no es válido o ha expirado
                    Log.e("API", "Token inválido o expirado");
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Usuario> call, @NonNull Throwable t) {
                Log.e("API", "Error al intentar obtener al usuario reportado: " + t.getMessage());
            }
        });
    }


    private void establecerImagenEntidad() {
        Usuario usuarioReportado = reporte.getUsuarioReportado();
        Equipo equipoReportado = reporte.getEquipoReportado();
        Actividad actividadReportada = reporte.getActividadReportada();

        String imagenEntidadReportada = null;
        int imagenRecurso = R.drawable.ic_profile; // Imagen por defecto

        if (usuarioReportado != null) {
            tvEntidadReportadaNombre.setText(usuarioReportado.getNombre());
            imagenEntidadReportada = usuarioReportado.getPfp(); // URL de imagen
        } else if (equipoReportado != null) {
            tvEntidadReportadaNombre.setText(equipoReportado.getNombre());
            imagenEntidadReportada = equipoReportado.getImagen(); // URL de imagen
        } else if (actividadReportada != null) {
            tvEntidadReportadaNombre.setText(actividadReportada.getTitulo());
            int deporte = actividadReportada.getDeporte();

            switch (deporte) {
                case 1:
                    imagenRecurso = R.drawable.fut7;
                    break;
                case 2:
                    imagenRecurso = R.drawable.futbol;
                    break;
                case 3:
                    imagenRecurso = R.drawable.futsal;
                    break;
                case 4:
                    imagenRecurso = R.drawable.tenis;
                    break;
                case 5:
                    imagenRecurso = R.drawable.padel;
                    break;
                case 6:
                    imagenRecurso = R.drawable.baloncesto;
                    break;
                case 7:
                    imagenRecurso = R.drawable.beisbol;
                    break;
                default:
                    imagenRecurso = R.drawable.activities;
                    break;
            }
        }

        // Cargar la imagen con Glide
        if (imagenEntidadReportada != null && !imagenEntidadReportada.isEmpty()) {
            // Si hay una URL de imagen, la cargamos
            Glide.with(this)
                    .load(imagenEntidadReportada)
                    .placeholder(R.drawable.ic_profile) // Imagen temporal mientras carga
                    .error(R.drawable.error_placeholder) // Imagen si falla la carga
                    .into(ivEntidadReportada);
        } else {
            // Si no hay URL, cargamos la imagen de recursos locales
            Glide.with(this)
                    .load(imagenRecurso)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.error_placeholder)
                    .into(ivEntidadReportada);
        }
    }



    // Funciones para los botones
    private void banearUsuario(){
        ApiService apiService = ApiClient.getApiService(this);
        int idUsuarioReportado = reporte.getUsuarioReportado().getIdUsuario().intValue();
        Call<Boolean> call = apiService.banearUsuario(idUsuarioReportado);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if (response.body()) {
                        mostrarSnackbar(findViewById(android.R.id.content), "Usuario baneado. El reporte ha sido resuelto.");
                        cerrarReporte();
                    }
                } else if (response.code() == 403) {
                    // Si el token no es válido o ha expirado
                    Log.e("API", "Token inválido o expirado");
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e("API", "Error al intentar banear al usuario reportado: " + t.getMessage());
            }
        });
    }

    private void cerrarReporte(){
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.revisarReporte(reporte.getId());
        Log.d("APP", reporte.getId() + "");

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if (response.body()) {
                        Toast.makeText(getApplicationContext(), "El reporte ha sido resuelto.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                        intent.putExtra("usuario", usuarioAdmin);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();

                        finish();
                    }
                } else if (response.code() == 403) {
                    // Si el token no es válido o ha expirado
                    Log.e("API", "Token inválido o expirado");
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e("API", "Error al intentar cerrar el reporte: " + t.getMessage());
            }
        });
    }


}
