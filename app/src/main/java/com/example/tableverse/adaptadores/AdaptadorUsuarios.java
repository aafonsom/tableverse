package com.example.tableverse.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tableverse.R;
import com.example.tableverse.objetos.Usuario;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.Vh> {

    private List<Usuario> lista_usuarios = new ArrayList<>();
    private Context context;

    public AdaptadorUsuarios(List<Usuario> lista_usuarios, Context context) {
        this.lista_usuarios = lista_usuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(context);
        View v = li.inflate(R.layout.template_ver_usuarios, parent, false);

        return new Vh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        Usuario usuario = lista_usuarios.get(position);

        holder.nombre.setText(usuario.getNombre());

        Glide.with(context).load(usuario.getUrl_imagen())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(R.drawable.person_morada).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return lista_usuarios.size();
    }

    public class Vh extends RecyclerView.ViewHolder {
        private ImageView iv;
        private TextView nombre;

        public Vh(@NonNull View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.temp_ver_iv);
            nombre = itemView.findViewById(R.id.temp_ver_nombre);
        }
    }
}
