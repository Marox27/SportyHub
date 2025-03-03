package com.example.sportyhub.Utilidades;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import androidx.concurrent.futures.CallbackToFutureAdapter;

import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Api.LoginRequest;
import com.example.sportyhub.Api.LoginResponse;
import com.example.sportyhub.Api.TokenManager;
import com.example.sportyhub.Modelos.Notificacion;
import com.example.sportyhub.Notificacion.NotificacionDetails;
import com.example.sportyhub.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionWorker extends ListenableWorker {
    private static final String CHANNEL_ID = "notificacion_canal";

    public NotificacionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            Log.d("Worker", "🔔 Worker ejecutado correctamente 🔔");

            TokenManager tokenManager = new TokenManager(getApplicationContext());
            String token = tokenManager.getToken();
            int idUser = tokenManager.getIdUser();
            String username = tokenManager.getUsername();
            String password = tokenManager.getUserKey();

            ApiService apiService = ApiClient.getApiService(getApplicationContext());

            // Validar token
            apiService.validarToken(token).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body()) {
                            Log.d("Worker", "Token válido. Buscando notificaciones...");
                            verificarNuevasNotificaciones(apiService, token, idUser, completer);
                        } else {
                            Log.d("Worker", "Token inválido. Renovando...");
                            renovarToken(apiService, username, password, idUser, tokenManager, completer);
                        }
                    } else {
                        Log.e("Worker", "Error validando token.");
                        completer.set(Result.failure());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                    Log.e("Worker", "Fallo al validar token: " + t.getMessage());
                    completer.setException(t);
                }
            });

            return "NotificacionWorker";
        });
    }

    private void verificarNuevasNotificaciones(ApiService apiService, String token, int idUser, CallbackToFutureAdapter.Completer<Result> completer) {
        apiService.obtenerNotificacionesNoLeidas(idUser).enqueue(new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notificacion>> call, @NonNull Response<List<Notificacion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notificacion> notificaciones = response.body();
                    for (Notificacion notificacion : notificaciones) {
                        mostrarNotificacion(notificacion.getTitulo(), notificacion.getMensaje(), idUser);
                    }
                }
                completer.set(Result.success());
            }

            @Override
            public void onFailure(@NonNull Call<List<Notificacion>> call, @NonNull Throwable t) {
                Log.e("Worker", "Error al obtener notificaciones: " + t.getMessage());
                completer.setException(t);
            }
        });
    }

    private void renovarToken(ApiService apiService, String username, String password, int idUser, TokenManager tokenManager, CallbackToFutureAdapter.Completer<Result> completer) {
        apiService.login(new LoginRequest(username, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String nuevoToken = response.body().getToken();
                    tokenManager.saveToken(nuevoToken);
                    Log.d("Worker", "Token renovado con éxito");
                    verificarNuevasNotificaciones(apiService, nuevoToken, idUser, completer);
                } else {
                    Log.e("Worker", "Error al renovar el token.");
                    completer.set(Result.failure());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e("Worker", "Error al renovar el token: " + t.getMessage());
                completer.setException(t);
            }
        });
    }

    private void mostrarNotificacion(String titulo, String mensaje, int idUser) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notificaciones", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        /*
        // Crear Intent para abrir la actividad deseada
        Intent intent = new Intent(context, NotificacionDetails.class);
        intent.putExtra("titulo", titulo);
        intent.putExtra("mensaje", mensaje);
        intent.putExtra("usuarioNickname", idUser);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Crear el PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        */
        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icono_sportyhub_statusbar)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
                //.setContentIntent(pendingIntent); // Asociar el PendingIntent

        // Mostrar la notificación
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

}

