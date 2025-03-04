package com.example.sportyhub.UserConfig;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Login;
import com.example.sportyhub.MainActivity;
import com.example.sportyhub.Modelos.Provincia;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfile extends AppCompatActivity {
    ImageView ivUserProfile;
    TextView tvCorreo;
    EditText etFirstName, etLastName;
    Spinner spProvincia;
    TextView tvDeleteAccount;
    Button btnSaveChanges;


    Uri imageUri;
    List<Provincia> provinciaList;
    private static final int PICK_IMAGE_REQUEST = 1;

    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        Usuario usuario = getIntent().getParcelableExtra("usuario", Usuario.class);

        // ImageView
        ivUserProfile = findViewById(R.id.ivUserProfile);

        // Ponemos la imagen del usuario
        Glide.with(getApplicationContext())
                .load(usuario.getPfp())
                .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                .into(ivUserProfile);
        ivUserProfile.setScaleType(ImageView.ScaleType.FIT_CENTER);


        // EditText's
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        spProvincia = findViewById(R.id.spCity);
        tvCorreo = findViewById(R.id.tvCorreoUsuario);

        etFirstName.setText(usuario.getNombre());
        etLastName.setText(usuario.getApellidos());
        tvCorreo.setText(usuario.getEmail());
        cargarProvincias(spProvincia);

        // TextView
        tvDeleteAccount = findViewById(R.id.tvDeleteAccount);

        // Button
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        LaunchSelectImage();
        LaunchImageCropper();

        // Acción del botón eliminar cambios
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Confirmación de Cambios")
                        .setMessage("¿Seguro que desea guardar los cambios aplicados?")
                        .setPositiveButton("Confirmar cambios", (dialog2, which2) ->
                                guardarCambiosUsuario(usuario))
                        .setNegativeButton("Cancelar", (dialog2, which2) ->
                                dialog2.dismiss());
                alertDialog.show();
            }
        });

        // Acción del botón eliminar cuenta
        tvDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("¡Cuidado esta acción no puede deshacerse!")
                        .setMessage("La eliminación de tu cuenta supondrá que perderás todo y no podrás " +
                                "volver a entrar desde esta. ¿Estás seguro de eliminarla?")
                        .setPositiveButton("Eliminar mi cuenta", (dialog2, which2) ->
                                confirmarEliminacionDeCuenta(view.getContext(), usuario))
                        .setNegativeButton("Cancelar", (dialog2, which2) ->
                            dialog2.dismiss());
                alertDialog.show();
            }
        });



    }

    // Guardar los cambios de la información modificada por el usaurio
    private void guardarCambiosUsuario(Usuario usuario) {
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        // Editamos la información con los datos introducidos
        usuario.setNombre(etFirstName.getText().toString());
        usuario.setApellidos(etLastName.getText().toString());
        Provincia provincia = (Provincia) spProvincia.getSelectedItem();
        usuario.setCiudad(provincia.getNombre());
        usuario.setPfp(imageUri.toString());

        Call<Boolean> call = apiService.updateUsuarioInfo(usuario);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body()){
                        Toast.makeText(getApplicationContext(), "Cambios guardados"
                                , Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("usuario", usuario);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(getApplicationContext(), "¡No se ha podido guardar los cambios!"
                                , Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("API", "Error al guardar los cambios de la cuenta: " + t.getMessage());
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        selectImageLauncher.launch(intent); // Usa el lanzador en lugar de startActivityForResult
    }

    private void LaunchSelectImage(){
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri sourceUri = result.getData().getData(); // Imagen seleccionada
                        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "recortada.jpg")); // Archivo de destino

                        // Configuración de uCrop
                        UCrop.Options options = new UCrop.Options();
                        options.setToolbarColor(ContextCompat.getColor(this, R.color.mainOrange));
                        options.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
                        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));

                        Intent uCropIntent = UCrop.of(sourceUri, destinationUri)
                                .withAspectRatio(1, 1) // Configura el aspecto de la imagen
                                .withMaxResultSize(500, 500) // Configura el tamaño máximo
                                .withOptions(options)
                                .getIntent(this);

                        cropImageLauncher.launch(uCropIntent);
                    }
                }
        );
    }


    private void LaunchImageCropper(){
        // Lanzador para manejar el resultado del recorte
        cropImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri resultUri = UCrop.getOutput(result.getData()); // Imagen recortada
                        if (resultUri != null) {
                            imageUri = resultUri;
                            ivUserProfile.setImageURI(imageUri); // Establece la imagen en el ImageView
                        }
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        Throwable cropError = UCrop.getError(result.getData());
                        Toast.makeText(this, "Error al recortar la imagen: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "recortada.jpg"));

            // Opciones para personalizar la herramienta de recorte
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(ContextCompat.getColor(this, R.color.mainOrange));
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));

            UCrop.of(imageUri, destinationUri)
                    .withAspectRatio(1, 1) // Cambia según sea necesario
                    .withMaxResultSize(500, 500) // Cambia según sea necesario
                    .withOptions(options)
                    .start(this);

            // Muestra la imagen seleccionada en tu ImageButton o ImageView
            ivUserProfile.setImageURI(imageUri);// Iniciar uCrop
            UCrop.of(imageUri, destinationUri)
                    .withAspectRatio(1, 1) // Aspecto cuadrado (opcional, ajusta según lo que necesites)
                    .withMaxResultSize(500, 500) // Tamaño máximo (opcional)
                    .start(this);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data); // URI de la imagen recortada
            imageUri = resultUri; // Actualiza el URI global con la imagen recortada
            ivUserProfile.setImageURI(imageUri); // Establece la imagen recortada en tu ImageView
        } else if (requestCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Error al recortar la imagen: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminacionDeCuenta(Context context, Usuario usuario){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Último aviso");
        builder.setMessage("Debido a que es una decisión bastante drástica, deberás de tomar " +
                "una la siguiente medida de seguridad.\n\n" +
                "Para confirmar la eliminación de la cuenta, escribe 'CONFIRMAR' en el campo de texto.");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Confirmar Eliminación", (dialog2, which2) -> {
            String userInput = input.getText().toString().trim();
            if ("CONFIRMAR".equals(userInput)) {
                eliminarCuenta(usuario);
            } else {
                Toast.makeText(context, "Texto incorrecto. Se canceló la eliminación.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog2, which2) -> {
            dialog2.dismiss();
        });

        builder.show();
    }

    private void eliminarCuenta(Usuario usuario){
        ApiService apiService = ApiClient.getApiService(getApplicationContext());
        Call<Boolean> call = apiService.eliminarUsuario(usuario.getIdUsuario().intValue());

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body()){
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "¡No se ha podido eliminar la cuenta!"
                        , Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e("API", "Error al eliminar la cuenta: " + t.getMessage());
                }
            });
    }

    // Se encarga de recoger las provincias mediante petición la api y cargarlas en el spinner del layout
    private void cargarProvincias(Spinner spinner) {
        ApiService apiService = ApiClient.getApiService(this);
            apiService.getProvincias().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Provincia>> call, Response<List<Provincia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinciaList = response.body();
                    ArrayAdapter<Provincia> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_spinner, provinciaList);
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

}
