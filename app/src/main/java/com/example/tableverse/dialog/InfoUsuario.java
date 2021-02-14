package com.example.tableverse.dialog;

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
import com.example.tableverse.adaptadores.AdaptadorUsuarios;
import com.example.tableverse.objetos.Evento;
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

    private AdminActividad adminActividad;
    private DatabaseReference ref;
    private StorageReference sto;
    private Usuario usuario;
    private String reserva;
    private ImageView foto;
    private TextView nombre, email;

    public InfoUsuario(String reserva){
        this.reserva = reserva;
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

        adminActividad = (AdminActividad) getActivity();
        ref = adminActividad.getRef();
        sto = adminActividad.getSto();


        cargarUsuario();

        return builder.create();

    }

    private void setView(){
        nombre.setText(usuario.getNombre());
        email.setText(usuario.getCorreo());

        sto.child("tienda").child("usuarios").child(usuario.getId())
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

    private void cargarUsuario() {
        ref.child("tienda").child("clientes").orderByKey().equalTo(reserva)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot hijo = snapshot.getChildren().iterator().next();
                        usuario = hijo.getValue(Usuario.class);
                        usuario.setId(hijo.getKey());

                        setView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}