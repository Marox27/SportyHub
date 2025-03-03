    package com.example.sportyhub;

import static com.example.sportyhub.Login.mostrarSnackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

    public class Registro extends AppCompatActivity {
    Button nextButton;
    EditText etEmail, etEmailRep, etPassword, etPasswordRep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nextButton = findViewById(R.id.next_step_button);
        etEmail = findViewById(R.id.email_input);
        etEmailRep = findViewById(R.id.email_input_rep);
        etPassword = findViewById(R.id.password_input);
        etPasswordRep = findViewById(R.id.password_input_rep);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String rep_mail = etEmailRep.getText().toString();
                String pass = etPassword.getText().toString();
                String rep_pass = etPasswordRep.getText().toString();

                // Si se cumplen las condiciones en los datos, se enviará la solicitud.
                if (comprobarCorreo(email, rep_mail) && comprobarPassword(pass, rep_pass)){
                    comprobarUsuario(email, pass);
                }

            }
        });
    }




    // Método para comprobar que el correo introducido es válido.
    private boolean comprobarCorreo(String correo, String correo_rep){
        if (correo.equals(correo_rep) && Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            return true;
        }
        Toast.makeText(getApplicationContext(),
                "El correo introducido es inválido.", Toast.LENGTH_SHORT).show();
        return false;
    }

    // Función para comprobar que la contraseña del usuario sea fuerte
    public boolean comprobarPassword(String password, String password_rep) {
        // Longitud mínima de 8 caracteres
        if (password.length() < 8) {
            Toast.makeText(getApplicationContext(),
                    "La contraseña debe tener al menos 8 carácteres.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Al menos una letra mayúscula
        if (!password.matches(".*[A-Z].*")) {
            Toast.makeText(getApplicationContext(),
                    "La contraseña debe tener al menos una letra mayúscula.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Al menos una letra minúscula
        if (!password.matches(".*[a-z].*")) {
            Toast.makeText(getApplicationContext(),
                    "La contraseña debe tener al menos una letra minúscula.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Al menos un número
        if (!password.matches(".*\\d.*")) {
            Toast.makeText(getApplicationContext(),
                    "La contraseña debe tener al menos un número.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Al menos un carácter especial
        if (!password.matches(".*[!@#$%^&*()-_=+{};:,<.>/?\\[\\]\\\\].*")) {
            Toast.makeText(getApplicationContext(),
                    "La contraseña debe tener al menos un carácter especial.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Deben de coincidir
        if (!password.equals(password_rep)){
            Toast.makeText(getApplicationContext(),
                    "Las contraseñas deben de coincidir.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Si cumple con todas las condiciones, la contraseña es válida
        return true;
    }

    private void enviarRegistro(String pass, String email){
        ApiService apiService = ApiClient.getApiService(this);
        Call<String> call = apiService.sendVerificationCode(email);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    mostrarSnackbar(findViewById(android.R.id.content), response.body());
                    // Pasamos al siguiente paso del registro
                    Intent intent = new Intent(getApplicationContext(), RegistroPaso2.class);
                    intent.putExtra("user_email", email);
                    intent.putExtra("user_pass", pass);
                    startActivity(intent);
                    finish();

                } else if (response.code() == 403) {
                    // Si el token no es válido o ha expirado
                    Log.e("API", "Token inválido o expirado");
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("API", "Error al crear al usuario: " + t.getMessage());
            }
        });
    }

    private void comprobarUsuario(String email, String pass) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.checkUser(email);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body()) {
                        enviarRegistro(pass, email);
                        nextButton.setEnabled(false);
                    } else {
                        mostrarSnackbar(findViewById(android.R.id.content), "El correo introducido ya está registrado");
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("API", "Error al crear al usuario: " + t.getMessage());
            }
        });

    }

}

