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
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;


public class DialogComprarJuego extends DialogFragment {

    private UsuarioActividad usuarioActividad;
    private Juego juego;
    private TextView tv_precio, tv_nombre, tv_stock;
    private Button b_comprar;
    private ImageView iv_foto, cerrar;
    private StorageReference sto;



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
        cerrar = v.findViewById(R.id.iv_close);
        b_comprar = v.findViewById(R.id.b_comprar);
        Toolbar toolbar = v.findViewById(R.id.tb_comprar_juego);
        toolbar.setTitle(getTag());


        usuarioActividad = (UsuarioActividad) getActivity();
        sto = usuarioActividad.getSto();
        juego = usuarioActividad.getLista_juegos().get(usuarioActividad.getPosition());
        setView();

        b_comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder.create();

    }

    private void setView() {
        tv_nombre.setText(juego.getNombre());
        tv_precio.setText(juego.getPrecio() + "€");
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof UsuarioActividad){
            this.usuarioActividad = (UsuarioActividad) context;
        }
    }

}