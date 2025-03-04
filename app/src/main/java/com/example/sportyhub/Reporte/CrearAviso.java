package com.example.sportyhub.Reporte;


import static com.example.sportyhub.Login.mostrarSnackbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportyhub.Admin.AdminMainActivity;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Modelos.Notificacion;
import com.example.sportyhub.Modelos.Reporte;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearAviso extends AppCompatActivity {
    private EditText etTitulo, etMensaje;
    private Button btnEnviar;

    private Usuario usuarioDestinatario, usuarioActual;
    private Reporte reporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificacion_crear_notificacion);
        usuarioDestinatario = getIntent().getParcelableExtra("usuario_seleccionado", Usuario.class);
        usuarioActual = getIntent().getParcelableExtra("usuario_admin", Usuario.class);
        reporte = getIntent().getParcelableExtra("reporte", Reporte.class);

        etTitulo = findViewById(R.id.etTitulo);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = etTitulo.getText().toString().trim();
                String mensaje = etMensaje.getText().toString().trim();

                if (titulo.isEmpty() || mensaje.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if (usuarioDestinatario !=null) {
                        enviarNotificacion(titulo, mensaje);
                    }else{
                        crearAviso(titulo, mensaje, usuarioActual.getIdUsuario().intValue());
                    }
                }
            }
        });
    }

    private void enviarNotificacion(String titulo, String mensaje) {
        Notificacion notificacion = new Notificacion(usuarioActual, usuarioDestinatario, titulo, mensaje);

        ApiService apiService = ApiClient.getApiService(this);
        Call<Notificacion> call = apiService.crearNotificacion(notificacion);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Notificacion> call, @NonNull Response<Notificacion> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(getApplicationContext(), "Aviso creado y enviado al usuario.", Toast.LENGTH_SHORT).show();
                        if (reporte != null){
                            marcarReporteRevisado();
                        }else{
                            finish();
                        }
                    }
                } else if (response.code() == 403) {
                    // Si el token no es válido o ha expirado
                    Log.e("API", "Token inválido o expirado");
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Notificacion> call, @NonNull Throwable t) {
                Log.e("API", "Error al enviar el aviso al usuario: " + t.getMessage());
            }
        });
    }

    private void crearAviso(String titulo, String cuerpo, int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Void> call = apiService.crearAviso(titulo, cuerpo, idUsuario);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Aviso creado y enviado a los usuario.", Toast.LENGTH_SHORT).show();
                        finish();
                } else if (response.code() == 403) {
                    // Si el token no es válido o ha expirado
                    Log.e("API", "Token inválido o expirado");
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("API", "Error al crear el aviso: " + t.getMessage());
            }
        });
    }

    private void marcarReporteRevisado(){
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.revisarReporte(reporte.getId());

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if (response.body()) {
                        Toast.makeText(getApplicationContext(), "El reporte ha sido resuelto.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                        intent.putExtra("usuario", usuarioActual);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
