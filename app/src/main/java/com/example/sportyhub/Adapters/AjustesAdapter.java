package com.example.sportyhub.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Modelos.AjusteItem;
import com.example.sportyhub.R;
import com.example.sportyhub.UserConfig.Ajustes;

import java.util.List;

public class AjustesAdapter extends RecyclerView.Adapter<AjustesAdapter.ViewHolder> {
    private List<AjusteItem> ajustes;
    private Context context;

    public AjustesAdapter(List<AjusteItem> ajustes, Context context) {
        this.ajustes = ajustes;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ajustes_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AjusteItem item = ajustes.get(position);
        holder.icono.setImageResource(item.getIcono());
        holder.titulo.setText(item.getTitulo());
        holder.itemView.setOnClickListener(v -> {
            ((Ajustes) context).abrirActividad(item.getTitulo());
        });
    }


    @Override
    public int getItemCount() {
        return ajustes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icono;
        TextView titulo;

        public ViewHolder(View itemView) {
            super(itemView);
            icono = itemView.findViewById(R.id.iconoAjuste);
            titulo = itemView.findViewById(R.id.tituloAjuste);
        }
    }
}

