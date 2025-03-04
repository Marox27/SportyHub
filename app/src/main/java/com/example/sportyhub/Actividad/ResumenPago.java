package com.example.sportyhub.Actividad;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sportyhub.MainActivity;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

public class ResumenPago extends AppCompatActivity {

    private TextView txtEstadoPago, txtOrderId, txtCantidad;
    private Button btnVolver;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pago);

        txtEstadoPago = findViewById(R.id.txtEstadoPago);
        txtOrderId = findViewById(R.id.txtOrderId);
        txtCantidad = findViewById(R.id.txtCantidad);
        btnVolver = findViewById(R.id.btnVolver);

        // Obtener datos del Intent
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("order_id");
        double cantidad = intent.getDoubleExtra("cantidad", 0.0);
        boolean pagoExitoso = intent.getBooleanExtra("pago_exitoso", false);
        usuario = intent.getParcelableExtra("usuario", Usuario.class);
        Log.d("app", usuario.toString());

        // Mostrar los datos en la UI
        if (pagoExitoso) {
            txtEstadoPago.setText("Pago Completado");
            txtEstadoPago.setTextColor(Color.GREEN);
        } else {
            txtEstadoPago.setText("Pago Fallido o Cancelado");
            txtEstadoPago.setTextColor(Color.RED);
        }

        txtOrderId.setText("ID del Pedido: " + orderId);
        txtCantidad.setText("Cantidad Pagada: " + cantidad + "€");

        // Botón para volver a la pantalla anterior
        btnVolver.setOnClickListener(v -> {
            Intent volverIntent = new Intent(getApplicationContext(), MainActivity.class);
            volverIntent.putExtra("usuario", usuario);
            volverIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(volverIntent);
            finish();
        });
    }
}

