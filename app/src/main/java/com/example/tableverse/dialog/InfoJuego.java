package com.example.tableverse.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;


public class InfoJuego extends DialogFragment {
    private UsuarioActividad activity;
    private DatabaseReference ref;
    private StorageReference sto;
    private Juego juego;
    private String reserva;
    private ImageView foto;
    private TextView nombre, categoria, precio;


    public InfoJuego(String reserva){
        this.reserva = reserva;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearInfoJuegos();
    }

    private AlertDialog crearInfoJuegos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_info_juego, null);
        builder.setView(v);

        foto = v.findViewById(R.id.infojuego_imagen);
        nombre = v.findViewById(R.id.infojuego_nombre);
        categoria = v.findViewById(R.id.infojuego_categoria);
        precio = v.findViewById(R.id.infojuego_dinero);

        activity = (UsuarioActividad) getActivity();

        ref = activity.getRef();
        sto = activity.getSto();


        cargarJuego();

        return builder.create();

    }

    public void setView(){
        nombre.setText(juego.getNombre());
        categoria.setText(juego.getCategoria());

        sto.child("tienda").child("juegos").child(juego.getId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                juego.setUrl_juego(uri.toString());
                Glide.with(getContext()).load(juego.getUrl_juego())
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(R.drawable.icono_redondo).into(foto);
            }
        });

        foto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if(activity.getPos_ratio_elegido() == -1){
            precio.setText(Double.toString(juego.getPrecio())
                    + activity.getDivisas()[activity.getPos_ratio_elegido()+1]);
        }else{
            DecimalFormat df = new DecimalFormat("#.##");
            precio.setText(df.format(juego.getPrecio() *
                    activity.getRatios()[activity.getPos_ratio_elegido()])
                    + activity.getDivisas()[activity.getPos_ratio_elegido()+1]);
        }

        if(activity.isTema()){
            categoria.setTextColor(Color.WHITE);
            nombre.setTextColor(Color.WHITE);
        }

    }

    private void cargarJuego() {
        ref.child("tienda").child("juegos").orderByKey().equalTo(reserva)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot hijo = snapshot.getChildren().iterator().next();
                        juego = hijo.getValue(Juego.class);
                        juego.setId(hijo.getKey());
                        setView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


}