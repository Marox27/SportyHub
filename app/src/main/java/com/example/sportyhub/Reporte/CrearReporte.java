package com.example.sportyhub.Reporte;

import static com.example.sportyhub.Login.mostrarSnackbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Notificacion;
import com.example.sportyhub.Modelos.Reporte;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearReporte extends AppCompatActivity {
    private EditText etMotivo, etDescripcion, entidadReportada;
    private Button btnEnviar;

    private Usuario usuarioReportado, usuarioReportante;
    private Equipo equipoReportado;
    private Actividad actividadReportada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte_crear_reporte);
        usuarioReportante = getIntent().getParcelableExtra("usuario", Usuario.class);

        usuarioReportado = getIntent().getParcelableExtra("usuario_reportado", Usuario.class);
        equipoReportado = getIntent().getParcelableExtra("equipo_reportado", Equipo.class);
        actividadReportada = getIntent().getParcelableExtra("actividad_reportada", Actividad.class);

        etMotivo = findViewById(R.id.etMotivo);
        etDescripcion = findViewById(R.id.etDescripcion);
        entidadReportada = findViewById(R.id.entidadReportada);
        btnEnviar = findViewById(R.id.btnEnviarReporte);

        if (usuarioReportado!= null){
            entidadReportada.setText(usuarioReportado.getNickname());
        } else if (actividadReportada!= null){
            entidadReportada.setText(actividadReportada.getTitulo());
        } else if (equipoReportado != null) {
            entidadReportada.setText(equipoReportado.getNombre());
        }
        // Desactivar edición
        entidadReportada.setFocusable(false);
        entidadReportada.setClickable(false);
        entidadReportada.setCursorVisible(false);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearReporte();
            }
        });
    }

    private void crearReporte() {
        String motivo = etMotivo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (motivo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Reporte reporte = new Reporte();
        if (usuarioReportado != null){
            reporte = new Reporte(motivo, descripcion, usuarioReportante, usuarioReportado);
        } else if (actividadReportada != null) {
            reporte = new Reporte(motivo, descripcion, usuarioReportante, actividadReportada);
        } else if (equipoReportado != null) {
            reporte = new Reporte(motivo, descripcion, usuarioReportante, equipoReportado);
        }


        ApiService apiService = ApiClient.getApiService(this);
        Call<Reporte> call = apiService.crearReporte(reporte);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Reporte> call, @NonNull Response<Reporte> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(getApplicationContext(), "Reporte creado y enviado a los administradores.", Toast.LENGTH_LONG).show();
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
            public void onFailure(@NonNull Call<Reporte> call, @NonNull Throwable t) {
                Log.e("API", "Error al crear el reporte del usuario: " + t.getMessage());
            }
        });
    }
}