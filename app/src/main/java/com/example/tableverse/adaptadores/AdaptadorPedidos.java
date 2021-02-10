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
import com.example.tableverse.objetos.ReservaJuego;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.Vh>{

    private List<ReservaJuego> lista_reservas;
    private Context context;

    public AdaptadorPedidos(List<ReservaJuego> lista_reservas, Context context) {
        this.lista_reservas = lista_reservas;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorPedidos.Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.template_pedidos, parent, false);

        return new Vh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPedidos.Vh holder, int position) {
        ReservaJuego reserva = lista_reservas.get(position);

        holder.nombre.setText(reserva.getNombre_juego());
        Glide.with(context).load(reserva.getUrl_juego())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(R.drawable.person_morada).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return lista_reservas.size();
    }

    public class Vh extends RecyclerView.ViewHolder {

        public ImageView foto;
        public TextView nombre, info;
        public FloatingActionButton confirm;
        public Vh(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.template_imagen_ped);
            nombre = itemView.findViewById(R.id.template_tv_nombre_pedidos);
            info = itemView.findViewById(R.id.template_info);
            confirm = itemView.findViewById(R.id.template_confirm);

        }
    }
}
