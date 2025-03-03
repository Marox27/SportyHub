package com.example.sportyhub.Utilidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Modelos.Municipio;
import com.example.sportyhub.Modelos.Pago;
import com.example.sportyhub.Modelos.Provincia;
import com.example.sportyhub.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {
    Context context;
    List<Provincia> lista_provincias;

    List<Municipio> lista_municipios;

    public Utils(Context context) {
        this.context = context;
    }


    // Habiendo seleccionado una provincia en el spinner, esta método se encarga de enviar una petición que
    // devuelva los municipios relacionados con la provincia seleccionada y los carga en el spinner del lyt.
    public void cargarMunicipios(int idProvincia) {
        ApiService apiService = ApiClient.getApiService(context);
        apiService.getMunicipios(idProvincia).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Municipio>> call, Response<List<Municipio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    lista_municipios = response.body();
                    Log.d("API", "Municipios recibidos: " + lista_municipios.size());
                }else{
                    Log.e("API", "Respuesta no exitosa: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Municipio>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Error al cargar municipios", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Configura y lanza el selector de imágenes.
     *
     * @param activity       La actividad que llama.
     * @param launcher       El lanzador de actividad.
     */
    public static void launchImagePicker(Activity activity, ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    /**
     * Configura y lanza el recorte de imagen.
     *
     * @param context         El contexto.
     * @param sourceUri       URI de la imagen original.
     * @param cacheDir        Directorio de caché para guardar la imagen recortada.
     * @param cropLauncher    Lanzador de actividad para el recorte.
     */
    public static void launchImageCrop(
            Context context,
            Uri sourceUri,
            File cacheDir,
            ActivityResultLauncher<Intent> cropLauncher) {

        Uri destinationUri = Uri.fromFile(new File(cacheDir, "recortada.jpg"));

        // Configuración de uCrop
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(context, R.color.mainOrange));
        options.setStatusBarColor(ContextCompat.getColor(context, R.color.black));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(context, R.color.colorAccent));
        options.setFreeStyleCropEnabled(true);

        Intent uCropIntent = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1) // Cambiar según sea necesario
                .withMaxResultSize(500, 500) // Cambiar según sea necesario
                .withOptions(options)
                .getIntent(context);

        cropLauncher.launch(uCropIntent);
    }

    /**
     * Obtiene el path real de un URI.
     *
     * @param context El contexto.
     * @param uri     El URI de la imagen.
     * @return El path real como cadena.
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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

    // Configuración de UCrop.Options
    public static UCrop.Options getUCropOptions(Context context) {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(context, R.color.mainOrange));
        options.setStatusBarColor(ContextCompat.getColor(context, R.color.black));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(context, R.color.colorAccent));
        return options;
    }

    // Iniciar el recorte de la imagen
    public static void startImageCropping(Context context, Uri sourceUri, File cacheDir, Activity activity) {
        Uri destinationUri = Uri.fromFile(new File(cacheDir, "recortada.jpg"));

        // Configurar UCrop
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1) // Aspecto cuadrado, ajusta si es necesario
                .withMaxResultSize(500, 500) // Tamaño máximo
                .withOptions(getUCropOptions(context))
                .start(activity); // Inicia UCrop
    }

    // Manejar errores de recorte
    public static void handleCropError(Intent data, Context context) {
        Throwable cropError = UCrop.getError(data);
        if (cropError != null) {
            Toast.makeText(context, "Error al recortar la imagen: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void openFileChooser(ActivityResultLauncher<Intent> selectImageLauncher) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        selectImageLauncher.launch(intent); // Usa el lanzador en lugar de startActivityForResult
    }


    public void verificarEstadoDePago(String order_id){
        ApiService apiService = ApiClient.getApiService(getContext());
        String pagoId = "ID_DEL_PAGO"; // Debes obtener el ID real del pago

        Call<Pago> call = apiService.verificarEstadoPago(pagoId);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Pago> call, Response<Pago> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pago pago = response.body();
                    Log.e("API", "Estado del pago: " + pago.isLiberado());
                } else {
                    Log.e("API", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Pago> call, Throwable t) {
                Log.e("API", "Error al verificar el estado del pago: " + t.getMessage());
            }
        });

    }





    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Provincia> getLista_provincias() {
        return lista_provincias;
    }

    public void setLista_provincias(List<Provincia> lista_provincias) {
        this.lista_provincias = lista_provincias;
    }

    public List<Municipio> getLista_municipios() {
        return lista_municipios;
    }

    public void setLista_municipios(List<Municipio> lista_municipios) {
        this.lista_municipios = lista_municipios;
    }
}
