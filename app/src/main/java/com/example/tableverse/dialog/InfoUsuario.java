package com.example.tableverse.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.adaptadores.AdaptadorUsuarios;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.ReservaJuego;
import com.example.tableverse.objetos.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class InfoUsuario extends DialogFragment {

    private Activity activity;
    private DatabaseReference ref;
    private StorageReference sto;
    private Usuario usuario;
    private Juego juego;
    private String reserva;
    private ImageView foto;
    private TextView nombre, email;
    private boolean cliente;
    private String[] llamadas;

    public InfoUsuario(String reserva, boolean cliente){
        this.reserva = reserva;
        this.cliente = cliente;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearInfoUsuarios();
    }

    private AlertDialog crearInfoUsuarios() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_info_usuario, null);
        builder.setView(v);

        foto = v.findViewById(R.id.iv_foto_usuario);
        nombre = v.findViewById(R.id.tv_nombre);
        email = v.findViewById(R.id.tv_email);

        activity = getActivity();
        if(activity instanceof AdminActividad){
            ref = ((AdminActividad)activity).getRef();
            sto = ((AdminActividad)activity).getSto();
        }else{
            ref = ((UsuarioActividad)activity).getRef();
            sto = ((UsuarioActividad)activity).getSto();
        }

        if(cliente){
            llamadas = new String[]{"clientes", "usuarios"};
        }else{
            llamadas = new String[]{"juegos", "juegos"};
        }

        cargarUsuario();

        return builder.create();

    }

    private void setViewUsuario(){
        nombre.setText(usuario.getNombre());
        email.setText(usuario.getCorreo());

        sto.child("tienda").child(llamadas[1]).child(usuario.getId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                usuario.setUrl_imagen(uri.toString());
                Glide.with(getContext()).load(usuario.getUrl_imagen())
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(R.drawable.person_morada).into(foto);
            }
        });
    }

    private void setViewJuego(){
        nombre.setText(juego.getNombre());
        email.setText(juego.getCategoria());

        sto.child("tienda").child(llamadas[1]).child(juego.getId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                juego.setUrl_juego(uri.toString());
                Glide.with(getContext()).load(juego.getUrl_juego())
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(R.drawable.person_morada).into(foto);
            }
        });
    }

    private void cargarUsuario() {
        ref.child("tienda").child(llamadas[0]).orderByKey().equalTo(reserva)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot hijo = snapshot.getChildren().iterator().next();
                        if(cliente){
                            usuario = hijo.getValue(Usuario.class);
                            usuario.setId(hijo.getKey());

                            setViewUsuario();
                        }else{
                            juego = hijo.getValue(Juego.class);
                            juego.setId(hijo.getKey());

                            setViewJuego();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}