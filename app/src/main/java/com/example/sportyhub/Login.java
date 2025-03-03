package com.example.sportyhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.sportyhub.Admin.AdminMainActivity;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Api.LoginRequest;
import com.example.sportyhub.Api.LoginResponse;
import com.example.sportyhub.Api.TokenManager;
import com.example.sportyhub.Modelos.Usuario;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    Button loginBtn, registerBtn;
    EditText userBox, passBox;
    TextView forgotView;
    ImageView logoView;
    ProgressBar progressBar;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        userBox = findViewById(R.id.UserBox);
        passBox = findViewById(R.id.PassBox);
        forgotView = findViewById(R.id.textView2);
        logoView = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userBox.getText().toString();
                String pass = passBox.getText().toString();
                CheckLogin(user, pass);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registro.class);
                startActivity(intent);
            }
        });


        verificarSesion();  // Comprueba si ya hay credenciales guardadas
    }

    // Comprueba si las credenciales introducidas en los campos son verídicos en caso afirmativo
    // se cambia a la actividad Principal
    public void CheckLogin(String user, String pass) {
        LoginRequest loginRequest = new LoginRequest(user, pass);

        ApiService apiService = ApiClient.getApiService(this);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    int idUser = loginResponse.getIdUsuario();
                    String token = loginResponse.getToken();
                    Log.e("API",idUser + " " + token + " " + user);

                    // Almacena el token si lo necesitas para futuras solicitudes
                    guardarPreferences(idUser, user, pass);

                    // Almacenamos el token usando TokenManager
                    TokenManager tokenManager = new TokenManager(getApplicationContext());
                    tokenManager.saveToken(token);
                    Log.d("LOGIN", "Token guardado correctamente: " + token);

                    // Extraemos y añadimos la información proveniente del token en los SharedPrefs
                    extraerClaims(token);

                    SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
                    boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);

                    cargarUsuario(idUser, isAdmin);

                } else if (response.code() == 401) {
                    Toast.makeText(Login.this, "Error de autenticación. Verifica tus credenciales.", Toast.LENGTH_SHORT).show();
                    Log.e("CheckLogin", "Error: " + response.code() + ", Error de credenciales.");
                } else if (response.code() == 403){
                    Toast.makeText(Login.this, "Error. El usuario introducido no existe o está inactivo.", Toast.LENGTH_SHORT).show();
                    Log.e("CheckLogin", "Error: " + response.code() + ", El usuario está inactivo");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (t instanceof java.net.SocketTimeoutException) {
                    mostrarSnackbar(findViewById(android.R.id.content), "El servidor tardó demasiado en responder. Intenta más tarde.");
                } else if (t instanceof java.net.UnknownHostException) {
                    mostrarSnackbar(findViewById(android.R.id.content), "Error de red. Verifica tu conexión a Internet.");
                } else {
                    mostrarSnackbar(findViewById(android.R.id.content), "Error al conectar con el servidor.");
                }
                Log.e("CheckLogin", "onFailure: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
                toggleLayout(progressBar);
            }
        });
    }


    // Método para guardar la información relevante del usuario de manera no volátil.
    public void guardarPreferences(int id_user, String user, String pass){
        // Creamos los objetos de SharedPreferences para guardar la información
        SharedPreferences preferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Guardamos la información relevante del usuario que será usada más adelante
        editor.putInt("id_user",id_user);
        editor.putString("username", user);
        editor.putString("password", pass);

        // Aplicamos los cambios que hemos guardado
        editor.apply();

    }

    // Método para guardar la información relevante del usuario de manera no volátil.
    private String[] cargarPreferences(){
        // Creamos los objetos de SharedPreferences para guardar la información
        SharedPreferences preferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        // Guardamos la información relevante del usuario que será usada más adelante
        String id_user = String.valueOf(preferences.getInt("id_user", -1));
        String user = preferences.getString("username", "NOT FOUND");
        String pass = preferences.getString("password", "NOT FOUND");

        String [] user_data = {id_user, user, pass};

        // Aplicamos los cambios que hemos guardado
        return user_data;
    }

    // Si ya hay datos de inicio de sesión, se iniciará la sesión de manera automática.
    private void verificarSesion() {
        progressBar.setVisibility(View.VISIBLE); // Mostrar el ProgressBar
        toggleLayout(progressBar); // Ocultar el layout de login

        // Cargamos los prefenrences de las crendeciales, para ver si hay credenciales guardadas
        SharedPreferences preferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
        String email = preferences.getString("email", null);
        String user = preferences.getString("username", null);
        String password = preferences.getString("password", null);

        if (user != null && password != null) {
            // Realizar inicio de sesión automático
            CheckLogin(user, password);
        }else {
            // Si no hay credenciales guardadas, ocultamos el ProgressBar y mostramos el login
            progressBar.setVisibility(View.GONE);
            toggleLayout(progressBar);
        }
    }

    private void toggleLayout(ProgressBar progressBar){
        if (progressBar.getVisibility() == View.VISIBLE){
            loginBtn.setVisibility(View.GONE);
            registerBtn.setVisibility(View.GONE);
            userBox.setVisibility(View.GONE);
            passBox.setVisibility(View.GONE);
            forgotView.setVisibility(View.GONE);
            //logoView.setVisibility(View.GONE);
        }else{
            loginBtn.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.VISIBLE);
            userBox.setVisibility(View.VISIBLE);
            passBox.setVisibility(View.VISIBLE);
            forgotView.setVisibility(View.VISIBLE);
            logoView.setVisibility(View.VISIBLE);
        }
    }


    public static void mostrarSnackbar(View view, String mensaje) {
        Snackbar snackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG);
        snackbar.setTextMaxLines(2); // Permite hasta 5 líneas
        snackbar.show();
    }
    private void extraerClaims(String token) {
        try {
            String[] parts = token.split("\\."); // Un JWT tiene tres partes: Header, Payload, Signature
            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT)); // Decodificar payload (claims)

            JSONObject jsonPayload = new JSONObject(payload);
            String role = jsonPayload.optString("role", "USER");
            boolean isAdmin = jsonPayload.optBoolean("isAdmin", false);

            Log.d("JWT", "Rol: " + role + " | Es Admin: " + isAdmin);

            // Guardar en SharedPreferences para usar en toda la app
            SharedPreferences sharedPreferences = getSharedPreferences("Credenciales", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("role", role);
            editor.putBoolean("isAdmin", isAdmin);
            editor.apply();

        } catch (Exception e) {
            Log.e("JWT", "Error al decodificar el token", e);
        }
    }

    private void cargarUsuario(int idUsuario, boolean isAdmin) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Usuario> call = apiService.getUser(idUsuario);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    if(response.body() != null){
                        usuario = response.body();
                        if (usuario.isBaneado()){
                            mostrarSnackbar(findViewById(android.R.id.content), "No puedes iniciar sesión. Tu cuenta está baneada.");
                        }else {
                            iniciarActividadPrincipal(usuario, isAdmin);
                        }
                    }else {
                        Log.e("API", "El usuario es nulo");
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("API", "Error al cargar los datos del usuario: " + t.getMessage());
            }
        });
    }

    private void iniciarActividadPrincipal(Usuario usuario, boolean isAdmin){
        // Lanzamos la actividad principal después de un login exitoso, comprobando si el usuario es admin o no
        if (isAdmin) {
            Intent loginAdmin = new Intent(Login.this, AdminMainActivity.class);
            loginAdmin.putExtra("usuario", usuario);
            startActivity(loginAdmin);
        } else {
            Intent login = new Intent(Login.this, MainActivity.class);
            login.putExtra("usuario", usuario);
            startActivity(login);
        }
        finish();
    }

}
