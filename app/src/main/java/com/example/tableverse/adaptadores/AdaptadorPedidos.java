package com.example.tableverse.adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.Vh>{

    private List<ReservaJuego> lista_reservas;
    private List<ReservaJuego> lista_filtrada;
    private Context context;

    public AdaptadorPedidos(List<ReservaJuego> lista_reservas, Context context) {
        this.lista_reservas = lista_reservas;
        this.lista_filtrada = lista_reservas;
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
        final ReservaJuego reserva = lista_filtrada.get(position);

        holder.nombre.setText(reserva.getNombre_juego());
        Glide.with(context).load(reserva.getUrl_juego())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(R.drawable.person_morada).into(holder.foto);

        if(reserva.isPreparado()){
            holder.confirm.setVisibility(View.INVISIBLE);
        }else{
            holder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.confirmar_pedido)
                        .setMessage(R.string.pedido_pregunta_confirmacion)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                reserva.setPreparado(true);

                                ref.child("tienda").child("reservas_juegos").child(reserva.getId())
                                        .setValue(reserva);

                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return lista_filtrada.size();
    }

    public void filtroTipo(String filtro){
        lista_filtrada = new ArrayList<>(lista_filtrada);
        List<ReservaJuego> lista = new ArrayList<>();

        if(filtro.equals("Preparados")){
            for(ReservaJuego reservaJuego: lista_reservas){
                if(reservaJuego.isPreparado()){
                    lista.add(reservaJuego);
                }
            }
        }else{
            for(ReservaJuego reservaJuego: lista_reservas){
                if(!reservaJuego.isPreparado()){
                    lista.add(reservaJuego);
                }
            }
        }

        lista_filtrada.clear();
        lista_filtrada.addAll(lista);
        notifyDataSetChanged();

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
