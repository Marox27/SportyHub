package com.example.sportyhub;

import static com.example.sportyhub.Login.mostrarSnackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroPaso2 extends AppCompatActivity {
    private EditText codeInput;

    private String userEmail;
    private String userPassword;
    private boolean canResend = true; // Controla si se puede reenviar el código

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_paso2);

        // Referencias a los elementos del layout
        codeInput = findViewById(R.id.code_input);
        Button verifyButton = findViewById(R.id.verify_button);
        TextView resendCodeText = findViewById(R.id.resend_code_text);

        // Recuperar datos del Intent
        userEmail = getIntent().getStringExtra("user_email");
        userPassword = getIntent().getStringExtra("user_pass");

        // Botón para verificar el código
        verifyButton.setOnClickListener(view -> {
            String code = codeInput.getText().toString();

            if (code.length() != 6) {
                Toast.makeText(getApplicationContext(),
                        "Por favor, introduce un código válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            verificarCodigo(code);
        });

        // Texto para reenviar el código
        resendCodeText.setOnClickListener(view -> {
            if (canResend) {
                reenviarCodigo();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Por favor, espera unos segundos antes de reenviar el código.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para verificar el código
    private void verificarCodigo(String code) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.verifyCode(userEmail, code);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body()) {
                        // Código verificado correctamente, continuar al paso 3
                        mostrarSnackbar(findViewById(android.R.id.content), "Código verificado.");
                        Intent intent = new Intent(getApplicationContext(), RegistroPaso3.class);
                        intent.putExtra("user_email", userEmail);
                        intent.putExtra("user_pass", userPassword);
                        startActivity(intent);
                        finish();
                    } else {
                        // Código incorrecto
                        Toast.makeText(getApplicationContext(),
                                "El código es incorrecto. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                    Toast.makeText(getApplicationContext(),
                            "Error al verificar el código.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e("API", "Error en la solicitud: " + t.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error al conectar con el servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para reenviar el código
    private void reenviarCodigo() {
        ApiService apiService = ApiClient.getApiService(this);
        Call<String> call = apiService.sendVerificationCode(userEmail);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Código reenviado al correo: " +
                            userEmail, Toast.LENGTH_SHORT).show();
                    iniciarTemporizadorReenvio();
                } else {
                    Log.e("API", "Error al reenviar el código: " + response.code());
                    Toast.makeText(getApplicationContext(),
                            "No se pudo reenviar el código.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("API", "Error en la solicitud: " + t.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error al conectar con el servidor.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para controlar el tiempo entre reenvíos de código
    private void iniciarTemporizadorReenvio() {
        canResend = false;
        new Handler(Looper.getMainLooper()).postDelayed(() -> canResend = true, 30000); // Bloquea el reenvío durante 30 segundos
    }

}

