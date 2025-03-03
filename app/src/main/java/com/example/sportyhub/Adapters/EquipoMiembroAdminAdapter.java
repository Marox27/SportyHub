package com.example.sportyhub.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Modelos.EquipoMiembro;
import com.example.sportyhub.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipoMiembroAdminAdapter extends RecyclerView.Adapter<EquipoMiembroAdminAdapter.MiembroViewHolder> {

    private List<EquipoMiembro> listaMiembros;
    private int idEquipo;

    public EquipoMiembroAdminAdapter(List<EquipoMiembro> listaMiembros, int idEquipo) {
        this.listaMiembros = listaMiembros;
        this.idEquipo = idEquipo;
    }

    @NonNull
    @Override
    public MiembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_miembro_admin, parent, false);
        return new MiembroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiembroViewHolder holder, int position) {
        EquipoMiembro miembro = listaMiembros.get(position);
        holder.textViewNombre.setText(miembro.getUsuario().getNickname());

        Glide.with(holder.itemView.getContext())
                .load(miembro.getUsuario().getPfp())
                .placeholder(R.drawable.default_pfp)
                .error(R.drawable.error_placeholder)
                .into(holder.imageViewAvatar);

        holder.menuOpciones.setOnClickListener(v -> mostrarPopupMenu(v, miembro, position));
    }

    @Override
    public int getItemCount() {
        return listaMiembros.size();
    }

    private void mostrarPopupMenu(View view, EquipoMiembro miembro, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.menu_miembro);
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.opcion_eliminar) {
                if (miembro.getUsuario().getIdUsuario() != miembro.getEquipo().getCreador()) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("¿Estás seguro?")
                            .setMessage("Estás seguro de echar a este usuario del equipo.")
                            .setPositiveButton("Echar del equipo", (dialog2, which2) ->
                                    eliminarMiembro(miembro, position, view.getContext()))
                            .setNegativeButton("Cancelar", (dialog2, which2) -> {
                                dialog2.dismiss();});
                    return true;
                }else {
                    Toast.makeText(view.getContext(), "No puedes echarte a ti mismo", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });
        popupMenu.show();
    }

    private void eliminarMiembro(EquipoMiembro miembro, int position, Context context) {
        ApiService apiService = ApiClient.getApiService(context);
        Call<Void> call = apiService.eliminarMiembro(miembro.getIdMiembro());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listaMiembros.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Miembro eliminado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error al eliminar miembro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class MiembroViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;
        ImageView imageViewAvatar, menuOpciones;

        public MiembroViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.nombreMiembro);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            menuOpciones = itemView.findViewById(R.id.menuOpciones);
        }
    }
}

