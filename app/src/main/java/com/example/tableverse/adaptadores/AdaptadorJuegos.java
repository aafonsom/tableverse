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
import com.example.tableverse.objetos.Juego;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorJuegos extends RecyclerView.Adapter<AdaptadorJuegos.Vh> {

    private List<Juego> lista_juegos;
    private List<Juego> lista_filtrada;
    private Context context;

    public AdaptadorJuegos(List<Juego> lista_juegos, Context context) {
        this.lista_juegos = lista_juegos;
        this.lista_filtrada = new ArrayList<>(this.lista_juegos);
        this.context = context;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.template_juegos, parent, false);

        return new Vh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        Juego juego = lista_juegos.get(position);

        holder.titulo.setText(juego.getNombre());
        holder.categoria.setText("Categoria: " + juego.getCategoria());
        holder.precio.setText("Precio: " + juego.getPrecio());
        Glide.with(context).load(juego.getUrl_juego())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.stat_notify_error).into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return lista_juegos.size();
    }

    public class Vh extends RecyclerView.ViewHolder {
        public ImageView foto;
        public TextView titulo, categoria, precio;
        public Vh(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.template_juego_imagen);
            titulo = itemView.findViewById(R.id.template_juego_titulo);
            categoria = itemView.findViewById(R.id.template_categoria);
            precio = itemView.findViewById(R.id.template_precio);
        }
    }
}
