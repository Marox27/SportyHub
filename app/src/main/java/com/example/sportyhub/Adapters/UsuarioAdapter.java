package com.example.sportyhub.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private Context context;

    private OnItemClickListener mListener; // Declaración del mListener

    public UsuarioAdapter(List<Usuario> listaUsuarios, Context context) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        if (listaUsuarios == null || listaUsuarios.isEmpty()) {
            // Mostrar mensaje para lista vacía
            holder.textViewUsernameUsuario.setText("No hay usuarios existentes");
            holder.textViewNombreUsuario.setVisibility(View.GONE);
            holder.imageViewUsuario.setVisibility(View.GONE);
        } else {
            // Configurar el diseño de la tarjeta normalmente
            Usuario usuario = listaUsuarios.get(position);

            holder.textViewUsernameUsuario.setText(usuario.getNickname());

            holder.textViewNombreUsuario.setText(usuario.getNombre() + " " + usuario.getApellidos());


            Glide.with(holder.itemView.getContext())
                    .load(usuario.getPfp())
                    .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                    .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                    .into(holder.imageViewUsuario);
            holder.imageViewUsuario.setScaleType(ImageView.ScaleType.FIT_CENTER);

        }
    }



    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Método para asignar el listener desde fuera del adaptador
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return (listaUsuarios == null || listaUsuarios.isEmpty()) ? 1 : listaUsuarios.size();
    }



    public class UsuarioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView imageViewUsuario;
        TextView textViewUsernameUsuario, textViewNombreUsuario;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUsuario = itemView.findViewById(R.id.imageViewAvatarUsuario);
            textViewUsernameUsuario = itemView.findViewById(R.id.textViewUsernameUsuario);
            textViewNombreUsuario = itemView.findViewById(R.id.textViewNombreUsuario);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Obtener la posición de la tarjeta que se hizo clic
            int position = getBindingAdapterPosition();
            // Verificar si hay un listener y llamarlo
            if (mListener != null && position != RecyclerView.NO_POSITION) {
                mListener.onItemClick(position);
            }
        }
    }

    public void actualizarLista(List<Usuario> listaUsuarios) {
        this.listaUsuarios.clear();
        this.listaUsuarios.addAll(listaUsuarios);
        notifyDataSetChanged();
    }

}