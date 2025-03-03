package com.example.sportyhub.UserConfig;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportyhub.Modelos.Deporte;
import com.example.sportyhub.R;

public class DeporteDetailsActivity extends AppCompatActivity {
    private TextView textViewNombre, textViewNormativa, textViewJugadores;
    private ImageView imageViewDeporte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deporte_detalles);

        textViewNombre = findViewById(R.id.textViewNombre);
        textViewNormativa = findViewById(R.id.textViewNormativa);
        textViewJugadores = findViewById(R.id.textViewJugadores);
        imageViewDeporte = findViewById(R.id.imageViewDeporte);

        Deporte deporte = getIntent().getSerializableExtra("deporte", Deporte.class);
        if (deporte != null) {
            textViewNombre.setText(deporte.getNombre());
            textViewNormativa.setText(deporte.getNormativa());
            textViewJugadores.setText("Jugadores Totales: " + deporte.getMinJugadores() + " - " + deporte.getMaxJugadores());
            asignarImagen(imageViewDeporte, deporte);
        }
    }

    private void asignarImagen(ImageView imageViewDeporte, Deporte deporte) {
        imageViewDeporte.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // Asignar la imagen según el deporte
        switch (deporte.getIdDeporte()) {
            case 1:
                imageViewDeporte.setImageResource(R.drawable.fut7);
                break;
            case 2:
                imageViewDeporte.setImageResource(R.drawable.futbol);
                break;
            case 3:
                imageViewDeporte.setImageResource(R.drawable.futsal);
                break;
            case 4:
                imageViewDeporte.setImageResource(R.drawable.tenis);
                break;
            case 5:
                imageViewDeporte.setImageResource(R.drawable.padel);
                break;
            case 6:
                imageViewDeporte.setImageResource(R.drawable.baloncesto);
                break;
            case 7:
                imageViewDeporte.setImageResource(R.drawable.beisbol);
                break;
            default:
                imageViewDeporte.setImageResource(R.drawable.activities);
                break;
        }
    }
}

