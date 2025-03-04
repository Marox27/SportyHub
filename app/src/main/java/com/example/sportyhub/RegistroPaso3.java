package com.example.sportyhub;


import static com.example.sportyhub.Login.mostrarSnackbar;
import static com.example.sportyhub.Utilidades.Utils.getRealPathFromUri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sportyhub.Actividad.CrearActividad;
import com.example.sportyhub.Admin.AdminMainActivity;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Modelos.Provincia;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.Utilidades.Utils;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroPaso3 extends AppCompatActivity {
    private ImageButton ib_pfp;
    private EditText nameInput, lastNameInput, nicknameInput, birthdayInput;
    private Spinner citySpinner;
    private Button uploadImageButton, finishButton;
    private String userEmail, userPassword;
    private String profileImagePath = null; // Ruta de la imagen seleccionada

    private static final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    List<Provincia> provincias;
    String fecha_nacimiento;

    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;

    private boolean admin;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_paso3);

        // Referencias al layout
        ib_pfp = findViewById(R.id.user_pfp_ib);
        nameInput = findViewById(R.id.name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        nicknameInput = findViewById(R.id.nickname_input);
        birthdayInput = findViewById(R.id.birthday_input);
        citySpinner = findViewById(R.id.city_input);
        uploadImageButton = findViewById(R.id.upload_image_button);
        finishButton = findViewById(R.id.finish_button);

        // Recuperar datos del Intent
        userEmail = getIntent().getStringExtra("user_email");
        userPassword = getIntent().getStringExtra("user_pass");
        admin = getIntent().getBooleanExtra("admin", false);
        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);

        // Cargamos el listado de provincias
        cargarProvincias(citySpinner);

        // Asinamos el listado de provincias al spinner del layout
        /*ArrayAdapter<Provincia> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, provincias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);*/

        // Acción para el EditText de la fecha
        birthdayInput.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            // Abrir el DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, yearSelected, monthOfYear, dayOfMonth) -> {
                        // Formatear la fecha seleccionada
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + yearSelected;
                        birthdayInput.setText(selectedDate);

                        // Guardar la fecha en formato ISO 8601 (yyyy-MM-dd)
                        fecha_nacimiento = String.format(Locale.getDefault(), "%04d-%02d-%02d", yearSelected, monthOfYear + 1, dayOfMonth);
                    },
                    calendar.get(Calendar.YEAR), // Año inicial
                    calendar.get(Calendar.MONTH), // Mes inicial
                    calendar.get(Calendar.DAY_OF_MONTH) // Día inicial
            );

            // Mostrar el DatePicker
            datePickerDialog.show();
        });

        // Acción para subir imagen
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
            }
        });


        // Acción para finalizar el registro
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameInput.getText().toString();
                String last_name = lastNameInput.getText().toString();
                String nickname = nicknameInput.getText().toString();
                String city = citySpinner.getSelectedItem().toString();

                if (admin){
                    userEmail = nickname + "@sportyhub.com";
                    userPassword = "#Ab12345";
                }

                finishButton.setEnabled(false);

                // Obtener la fecha del input y reemplazar "/" por "-"
                String fechaNacimientoInput = birthdayInput.getText().toString().replace("/", "-");

                // Asegurar que el formato sea siempre dd-MM-yyyy (agregando ceros si es necesario)
                String[] partes = fechaNacimientoInput.split("-");
                if (partes.length == 3) {
                    String dia = partes[0].length() == 1 ? "0" + partes[0] : partes[0];
                    String mes = partes[1].length() == 1 ? "0" + partes[1] : partes[1];
                    String anio = partes[2];

                    fechaNacimientoInput = dia + "-" + mes + "-" + anio;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoInput, formatter);


                if (name.isEmpty() || nickname.isEmpty() || city.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show();
                    finishButton.setEnabled(true);
                    return;
                }

                // Validar edad mínima de 16 años
                LocalDate today = LocalDate.now();
                LocalDate minBirthDate = today.minusYears(16);

                if (ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now()) < 16) {
                    Toast.makeText(getApplicationContext(), "Debes tener al menos 16 años para registrarte.", Toast.LENGTH_SHORT).show();
                    finishButton.setEnabled(true);
                    return;
                }

                SubirImagenYCompletarRegistro(name, last_name, nickname, fechaNacimiento, city);
            }
        });

        // Lanzador para seleccionar imagen
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri sourceUri = result.getData().getData();
                        Utils.launchImageCrop(this, sourceUri, getCacheDir(), cropImageLauncher);
                    }
                }
        );

        // Lanzador para recortar imagen
        cropImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri resultUri = UCrop.getOutput(result.getData());
                        if (resultUri != null) {
                            imageUri = resultUri;
                            ib_pfp.setImageURI(imageUri); // Establece la imagen recortada
                        }
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        Throwable cropError = UCrop.getError(result.getData());
                        Toast.makeText(this, "Error al recortar la imagen: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    private void seleccionarImagen() {
        Utils.launchImagePicker(this, selectImageLauncher);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Imagen seleccionada
            imageUri = data.getData();

            // Llama a la utilidad para iniciar el recorte
            Utils.startImageCropping(this, imageUri, getCacheDir(), this);

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            // Imagen recortada con éxito
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                imageUri = resultUri;
                ib_pfp.setImageURI(imageUri); // Muestra la imagen recortada
            }
        } else if (requestCode == UCrop.RESULT_ERROR) {
            // Manejo de errores
            Utils.handleCropError(data, this);
        }
    }

    // Se encarga de recoger las provincias mediante petición la api y cargarlas en el spinner del layout
    private void cargarProvincias(Spinner spinner) {
        ApiService apiService = ApiClient.getApiService(this);
        apiService.getProvincias().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Provincia>> call, Response<List<Provincia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provincias = response.body();
                    Log.d("API", "Provincias recibidas: " + provincias.size());
                    ArrayAdapter<Provincia> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_spinner, provincias);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }else {
                    Log.e("API", "Respuesta no exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Provincia>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al cargar provincias", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SubirImagenYCompletarRegistro(String name, String last_name, String nickname,
                                               LocalDate fecha_nacimiento, String city){
         if (imageUri == null) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
        // Obtén la ruta real del Uri
        String realPath = getRealPathFromUri(this, imageUri);

        // Convierte la imagen a Multipart
        File file = new File(realPath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        ApiService apiService = ApiClient.getApiService(this);

        // Llama a la API para subir la imagen
        apiService.subirImagen(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String urlImagen = response.body();

                    // Usa la URL de la imagen para crear el equipo
                    FinalizarRegistro(name, last_name, nickname, fecha_nacimiento, city, urlImagen);
                } else {
                    System.out.println(response.body());
                    finishButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                t.printStackTrace();
                finishButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al leer la imagen.", Toast.LENGTH_SHORT).show();
        }
    }

    private void FinalizarRegistro(String name, String last_name, String nickname, LocalDate fecha_nacimiento,String city, String imagePath) {
        ApiService apiService = ApiClient.getApiService(this);

        // Crear un objeto Usuario con los datos finales
        Usuario usuario = new Usuario(name, last_name, nickname, userEmail, userPassword, fecha_nacimiento.toString(), city, imagePath);
        usuario.setActivo(true);
        if (admin) usuario.setAdmin(true);
        Gson gson = new Gson();
        String jsonUsuario = gson.toJson(usuario);
        Log.d("API", "JSON enviado: " + jsonUsuario);

        Call<Usuario> call = apiService.createUser(usuario);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Usuario> call, @NonNull Response<Usuario> response) {
                if (response.isSuccessful()) {
                    mostrarSnackbar(findViewById(android.R.id.content), "¡Registro completado!");
                    int id_user = response.body().getIdUsuario().intValue();

                    if (admin){
                        Toast.makeText(getApplicationContext(),"Usuario administrador creado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else{

                    // Creamos los objetos de SharedPreferences para guardar la información
                    SharedPreferences preferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    // Guardamos la información relevante del usuario que será usada más adelante
                    editor.putInt("id_user",id_user);
                    editor.putString("username", nickname);
                    editor.putString("password", userPassword);

                    // Aplicamos los cambios que hemos guardado
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                    Toast.makeText(getApplicationContext(),
                            "No se pudo completar el registro.", Toast.LENGTH_SHORT).show();
                    finishButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Usuario> call, @NonNull Throwable t) {
                Log.e("API", "Error en la solicitud: " + t.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Error al conectar con el servidor.", Toast.LENGTH_SHORT).show();
                finishButton.setEnabled(true);
            }
        });
    }
}
