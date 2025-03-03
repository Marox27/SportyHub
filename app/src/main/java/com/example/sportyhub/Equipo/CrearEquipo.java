package com.example.sportyhub.Equipo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.MainActivity;
import com.example.sportyhub.Modelos.Deporte;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.EquipoMiembro;
import com.example.sportyhub.Modelos.Municipio;
import com.example.sportyhub.Modelos.Provincia;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CrearEquipo extends AppCompatActivity{
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String [] Privacidad = {"PÚBLICA","POR INVITACIÓN", "PRIVADA"};
    Uri imageUri;

    ImageButton cambiarImagen;
    Button crearEquipo;
    EditText nombreEquipo, detallesText;
    Spinner deporteText, provinciaText, municipioText; //privacidadText;


    List<Deporte> deportes;
    List<Provincia> provincias;
    List<Municipio> municipios;

    Usuario usuario = null;
    EquipoMiembro equipoMiembro = null;

    int id_deporte = 1;
    int id_user;

    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipo_crear_equipo);
        SharedPreferences preferences = getSharedPreferences("Credenciales", Context.MODE_PRIVATE);

        // Guardamos la información relevante del usuario que será usada más adelante
        id_user = preferences.getInt("id_user", -1);
        //usuario = getIntent().getParcelableExtra("usuario", Usuario.class);
        cargarUsuario();

        crearEquipo = findViewById(R.id.buttonCrearEquipo);
        nombreEquipo = findViewById(R.id.editTextNombreEquipo);
        deporteText = findViewById(R.id.spinnerDeporte);
        provinciaText = findViewById(R.id.spinnerProvincia);
        municipioText = findViewById(R.id.spinnerMunicipio);
        //privacidadText = findViewById(R.id.spinnerPrivacidad);
        detallesText = findViewById(R.id.editTextDescripcion);
        cambiarImagen = findViewById(R.id.imageViewEquipo);

        // Iniciamos la tarea de fondo que se encargará de cargar los deportes en el spinner.
        cargarDeportes(deporteText);
        cargarProvincias(provinciaText);
        Log.e("APP", "El id user es: " + id_user);
        //cargarUsuario(id_user);

        // Listener para el Spinner de provincias
        provinciaText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int provinciaSeleccionada = provincias.get(position).getId();

                // Obtener los municipios basados en la provincia seleccionada
                cargarMunicipios(municipioText, provinciaSeleccionada);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar caso en el que no se seleccione nada
            }
        });

        //Listener para manejar la selección del elemento
        deporteText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Acciones cuando se selecciona un elemento
                String opcionSeleccionada = deportes.get(position).getNombre();
                id_deporte = deportes.get(position).getIdDeporte();
            }

            // En caso de que no se seleccione ninguna opción.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Se debe seleccionar un deporte.", Toast.LENGTH_SHORT).show();
            }
        });


        // ArrayAdapter<String> privacidad_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_spinner, Privacidad);
        // privacidad_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Asignar el ArrayAdapter al Spinner
        //privacidadText.setAdapter(privacidad_adapter);

        cambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        crearEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearEquipo.setEnabled(false);
                String nombre, provincia, municipio, privacidad, detalles;

                // Asignando valores
                nombre = nombreEquipo.getText().toString();
                provincia = provinciaText.getSelectedItem().toString();
                municipio = municipioText.getSelectedItem().toString();
                //privacidad = privacidadText.getSelectedItem().toString();
                privacidad = "PÚBLICA";
                detalles = detallesText.getText().toString();



                // Se crea el equipo.
                subirImagenYCrearEquipo(nombre, id_deporte, provincia, municipio, privacidad, detalles, id_user);
            }
        });


        // Lanzador para seleccionar una imagen
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

        // Lanzador para manejar el resultado del recorte
        cropImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri resultUri = UCrop.getOutput(result.getData()); // Imagen recortada
                        if (resultUri != null) {
                            imageUri = resultUri;
                            cambiarImagen.setImageURI(imageUri); // Establece la imagen en el ImageView
                        }
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        Throwable cropError = UCrop.getError(result.getData());
                        Toast.makeText(this, "Error al recortar la imagen: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        selectImageLauncher.launch(intent); // Usa el lanzador en lugar de startActivityForResult
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
            cambiarImagen.setImageURI(imageUri);// Iniciar uCrop
            UCrop.of(imageUri, destinationUri)
                    .withAspectRatio(1, 1) // Aspecto cuadrado (opcional, ajusta según lo que necesites)
                    .withMaxResultSize(500, 500) // Tamaño máximo (opcional)
                    .start(this);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data); // URI de la imagen recortada
            imageUri = resultUri; // Actualiza el URI global con la imagen recortada
            cambiarImagen.setImageURI(imageUri); // Establece la imagen recortada en tu ImageView
        } else if (requestCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Error al recortar la imagen: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /*
    private void subirImagenYCrearEquipo(String nombre, int deporte, String provincia, String municipio,
                                         String privacidad, String detalles, int user) {
        if (imageUri == null) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Convierte Uri a InputStream para enviar al servidor
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = inputStream.readAllBytes();

            ApiService apiService = ApiClient.getApiService(this);

            // Llama a la API para subir la imagen
            apiService.subirImagen(imageBytes).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String urlImagen = response.body();

                        // Usa la URL de la imagen para crear el equipo
                        crearEquipoConImagen(nombre, deporte, provincia, municipio, privacidad, detalles, user, urlImagen);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al leer la imagen.", Toast.LENGTH_SHORT).show();
        }
    }

     */

    private String getRealPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
    }


    private void subirImagenYCrearEquipo(String nombre, int deporte, String provincia, String municipio,
                                         String privacidad, String detalles, int user) {
        if (imageUri == null) {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            crearEquipo.setEnabled(true);
            return;
        }

        try {
            // Obtén la ruta real del Uri
            String realPath = getRealPathFromUri(imageUri);

            // Convierte la imagen a Multipart
            File file = new File(realPath);
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            ApiService apiService = ApiClient.getApiService(this);

            // Llama a la API para subir la imagen
            apiService.subirImagen(body).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String urlImagen = response.body();

                        // Usa la URL de la imagen para crear el equipo
                        crearEquipoConImagen(nombre, deporte, provincia, municipio, privacidad, detalles, user, urlImagen);
                    } else {
                        System.out.println(response.body());
                        crearEquipo.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                    crearEquipo.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Error al subir la imagen.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            crearEquipo.setEnabled(true);
            Toast.makeText(this, "Error al leer la imagen.", Toast.LENGTH_SHORT).show();
        }
    }


    private void crearEquipoConImagen(String nombre, int deporte, String provincia, String municipio,
                                      String privacidad, String detalles, int user, String urlImagen) {
        ApiService apiService = ApiClient.getApiService(this);

        Equipo equipoRequest = new Equipo(user, deporte, nombre, provincia, municipio, privacidad, detalles, urlImagen);

        apiService.crearEquipo(equipoRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Equipo> call, Response<Equipo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Equipo equipoResponse = response.body();

                    // Crear y asignar el creador a los miembros del equipo
                    usuario.setIdUsuario((long) id_user);
                    Log.d("API", equipoResponse.toString());

                    equipoMiembro = new EquipoMiembro(equipoResponse, usuario, EquipoMiembro.Rol.ADMIN);

                    Toast.makeText(getApplicationContext(), "¡Equipo creado con éxito!", Toast.LENGTH_SHORT).show();

                    // Creada el equipo volver a MainActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("usuario", usuario);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    crearEquipo.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Error al crear el equipo.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Equipo> call, Throwable t) {
                t.printStackTrace();
                crearEquipo.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Error al crear el equipo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    // Crear un miembro en un equipo
    public void crearMiembro() {
        ApiService apiService = ApiClient.getApiService(this);
        apiService.crearMiembro(equipoMiembro).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<EquipoMiembro> call, Response<EquipoMiembro> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("API", "El miembro ha sido creado con éxito");
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al crear el miembro.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<EquipoMiembro> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error al crear el miembro.", Toast.LENGTH_SHORT).show();
                Log.e("API", "Error al crear el usuario", t);
            }
        });
    }
    */


    /*==============================================================================================
    * FUNCIONES DE CARGA DE DATOS
    * ============================================================================================*/
    private void cargarUsuario() {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Usuario> call = apiService.getUser(id_user);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    if(response.body() != null){
                        Log.e("API", "Datos del usuario cargados");
                        usuario = response.body();
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

    // Función que carga los deportes existentes en la bd en el spinner mediante petición a la api.
    private void cargarDeportes(Spinner spinner) {
        ApiService apiService = ApiClient.getApiService(this);
        apiService.obtenerDeportes().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Deporte>> call, Response<List<Deporte>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deportes = response.body();
                    Log.d("API", "Deportes recibidos: " + deportes.size());

                    // Crear un ArrayAdapter personalizado
                    ArrayAdapter<Deporte> adapter = new ArrayAdapter<>(CrearEquipo.this,
                            R.layout.item_spinner, deportes) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            // Solo mostramos el nombre del deporte en el Spinner
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setText(deportes.get(position).getNombre());
                            return textView;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            // Configuraramos el diseño de cada opción desplegable
                            TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                            textView.setText(deportes.get(position).getNombre());
                            return textView;
                        }
                    };
                    // IMPORTANTE: Configurar el adaptador en el hilo principal
                    runOnUiThread(() -> spinner.setAdapter(adapter));
                }else{
                    Log.e("API", "Respuesta no exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Deporte>> call, Throwable t) {
                t.printStackTrace();
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


    // Habiendo seleccionado una provincia en el spinner, esta método se encarga de enviar una petición que
    // devuelva los municipios relacionados con la provincia seleccionada y los carga en el spinner del lyt.
    private void cargarMunicipios(Spinner spinner, int idProvincia) {
        ApiService apiService = ApiClient.getApiService(this);
        apiService.getMunicipios(idProvincia).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Municipio>> call, Response<List<Municipio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    municipios = response.body();
                    Log.d("API", "Municipios recibidos: " + municipios.size());

                    ArrayAdapter<Municipio> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_spinner, municipios);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }else{
                    Log.e("API", "Respuesta no exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Municipio>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al cargar municipios", Toast.LENGTH_SHORT).show();
            }
        });
    }

}