package com.example.sportyhub.Adapters;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Modelos.Deporte;
import com.example.sportyhub.R;
import com.example.sportyhub.UserConfig.DeporteDetailsActivity;

import java.util.List;

public class DeportesAdapter extends RecyclerView.Adapter<DeportesAdapter.DeporteViewHolder> {
    private List<Deporte> deportesList;
    private Context context;

    public DeportesAdapter(List<Deporte> deportesList, Context context) {
        this.deportesList = deportesList;
        this.context = context;
    }

    @NonNull
    @Override
    public DeporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_deporte, parent, false);
        return new DeporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeporteViewHolder holder, int position) {
        Deporte deporte = deportesList.get(position);
        holder.textViewNombre.setText(deporte.getNombre());
        holder.imageViewDeporte.setImageResource(deporte.getImagenResId());

        holder.imageViewDeporte.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // Asignar la imagen según el deporte
        switch (deporte.getIdDeporte()) {
            case 1:
                holder.imageViewDeporte.setImageResource(R.drawable.fut7);
                break;
            case 2:
                holder.imageViewDeporte.setImageResource(R.drawable.futbol);
                break;
            case 3:
                holder.imageViewDeporte.setImageResource(R.drawable.futsal);
                break;
            case 4:
                holder.imageViewDeporte.setImageResource(R.drawable.tenis);
                break;
            case 5:
                holder.imageViewDeporte.setImageResource(R.drawable.padel);
                break;
            case 6:
                holder.imageViewDeporte.setImageResource(R.drawable.baloncesto);
                break;
            case 7:
                holder.imageViewDeporte.setImageResource(R.drawable.beisbol);
                break;
            default:
                holder.imageViewDeporte.setImageResource(R.drawable.activities);
                break;
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DeporteDetailsActivity.class);
            intent.putExtra("deporte", deporte);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return deportesList.size();
    }

    public static class DeporteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;
        ImageView imageViewDeporte;

        public DeporteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombreDeporte);
            imageViewDeporte = itemView.findViewById(R.id.imageViewDeporte);
        }
    }
}

