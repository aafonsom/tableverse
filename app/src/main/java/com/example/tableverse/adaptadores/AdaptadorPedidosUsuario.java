package com.example.tableverse.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.dialog.InfoJuego;
import com.example.tableverse.dialog.InfoUsuario;
import com.example.tableverse.objetos.ReservaJuego;

import java.util.List;

public class AdaptadorPedidosUsuario extends RecyclerView.Adapter<AdaptadorPedidosUsuario.Vh> {
    private List<ReservaJuego> lista_reservas;
    private Context context;
    private Activity activity;

    public AdaptadorPedidosUsuario(List<ReservaJuego> lista_reservas, Context context, Activity activity) {
        this.lista_reservas = lista_reservas;
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.template_historial_pedidos, parent, false);

        return new Vh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        final ReservaJuego reserva = lista_reservas.get(position);

        holder.nombre.setText(reserva.getNombre_juego());
        Glide.with(context).load(reserva.getUrl_juego())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(R.drawable.persona_placeholder).into(holder.foto);

        if(((UsuarioActividad)activity).isTema()){
            holder.nombre.setTextColor(Color.WHITE);
            holder.info.setTextColor(Color.parseColor("#9b73f7"));
        }

        if(reserva.getFecha_procesado().isEmpty()){
            holder.fecha_procesado.setText("El pedido todavía no está preparado");
        }else{
            String partes[] = reserva.getFecha_procesado().split("-");
            holder.fecha_procesado.setText("Preparado el: " + partes[2] + "-" + partes[1] + "-"+ partes[0]);
        }

        String partes[] = reserva.getFecha_pedido().split("-");
        holder.fecha_pedido.setText("Realizado el: " + partes[2] + "-" + partes[1] + "-"+ partes[0]);

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoJuego info = new InfoJuego(reserva.getId_juego());
                info.show(((UsuarioActividad)activity).getSupportFragmentManager(), "Información de juego");
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.lista_reservas.size();
    }

    public class Vh extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nombre, info, fecha_pedido, fecha_procesado;

        public Vh(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.template_imagen_ped);
            nombre = itemView.findViewById(R.id.template_tv_nombre_pedidos);
            info = itemView.findViewById(R.id.template_info);
            fecha_pedido = itemView.findViewById(R.id.template_fecha_pedido);
            fecha_procesado = itemView.findViewById(R.id.template_fecha_preparado);
        }
    }
}
