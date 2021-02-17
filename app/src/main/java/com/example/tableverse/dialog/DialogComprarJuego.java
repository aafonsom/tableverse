package com.example.tableverse.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.AppUtilities;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.ReservaEvento;
import com.example.tableverse.objetos.ReservaJuego;
import com.example.tableverse.objetos.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.Iterator;


public class DialogComprarJuego extends DialogFragment {

    private UsuarioActividad usuarioActividad;
    private Juego juego;
    private TextView tv_precio, tv_nombre, tv_stock;
    private Button b_comprar;
    private ImageView iv_foto;
    private StorageReference sto;
    private DatabaseReference ref;
    private Usuario usuario;


    public DialogComprarJuego(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogComprarJuego();
    }

    private AlertDialog crearDialogComprarJuego() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialog_comprar_juego, null);
        builder.setView(v);

        tv_nombre = v.findViewById(R.id.tv_nombre);
        tv_stock = v.findViewById(R.id.tv_stock);
        tv_precio = v.findViewById(R.id.tv_precio);
        iv_foto = v.findViewById(R.id.iv_juego);
        b_comprar = v.findViewById(R.id.b_comprar);

        usuarioActividad = (UsuarioActividad) getActivity();
        sto = usuarioActividad.getSto();
        ref = usuarioActividad.getRef();
        juego = usuarioActividad.getLista_juegos().get(usuarioActividad.getPosition());
        usuario = usuarioActividad.getUsuario();
        setView();

        b_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprarJuego();
            }
        });


        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof UsuarioActividad){
            this.usuarioActividad = (UsuarioActividad) context;
        }
    }

    private void setView() {
        tv_nombre.setText(juego.getNombre());

        if(usuarioActividad.getPos_ratio_elegido() == -1){
            tv_precio.setText("Precio: " + juego.getPrecio()
                    + usuarioActividad.getDivisas()[usuarioActividad.getPos_ratio_elegido()+1]);
        }else{
            DecimalFormat df = new DecimalFormat("#.##");
            tv_precio.setText("Precio: " + df.format(juego.getPrecio() *
                    usuarioActividad.getRatios()[usuarioActividad.getPos_ratio_elegido()])
                    + usuarioActividad.getDivisas()[usuarioActividad.getPos_ratio_elegido()+1]);
        }

        int stock = juego.getStock();
        if(stock < 10){
            tv_stock.setText("Solo quedan " + stock + " unidades! No pierdas tu oportunidad!");
            tv_stock.setTextColor(Color.RED);
        }else{
            tv_stock.setText("Quedan " + stock + " unidades");
        }

        sto.child("tienda").child("juegos").child(juego.getId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(juego.getUrl_juego())
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.stat_notify_error).into(iv_foto);
            }
        });
    }

    private void comprarJuego(){
        ref.child("tienda").child("reservas_juegos").orderByChild("id_cliente")
                .equalTo(usuario.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReservaJuego reservaExiste = null;
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();

                while (iterator.hasNext() && reservaExiste == null){
                    ReservaJuego rj = iterator.next().getValue(ReservaJuego.class);
                    if(rj.getId_juego().equals(juego.getId())){
                        reservaExiste = rj;
                    }
                }

                if(reservaExiste != null){
                    Toast.makeText(usuarioActividad, "Ya has reservado este juego, se está preparando", Toast.LENGTH_SHORT).show();
                }else{
                    ref.child("tienda").child("clientes").orderByKey()
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot hijo: snapshot.getChildren()){
                                Usuario pojo_usuario = hijo.getValue(Usuario.class);

                                if(pojo_usuario.getTipo().equals("admin")){
                                    pojo_usuario.setId(hijo.getKey());

                                    ref.child("tienda").child("clientes")
                                            .child(pojo_usuario.getId()).child("estado")
                                            .setValue(Usuario.PEDIDO_ESPERA);



                                }

                            }
                            AppUtilities appUtilities = new AppUtilities();
                            String fecha = appUtilities.getDate();
                            ReservaJuego nuevaReserva = new ReservaJuego(juego.getId(), usuario.getId(), juego.getNombre(), fecha);
                            String id = ref.child("tienda").child("reservas_juegos").push().getKey();
                            ref.child("tienda").child("reservas_juegos").child(id).setValue(nuevaReserva);
                            juego.setStock(juego.getStock() - 1);
                            ref.child("tienda").child("juegos").child(juego.getId()).setValue(juego);
                            dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    Toast.makeText(usuarioActividad, "Se ha reservado el juego con éxito", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}