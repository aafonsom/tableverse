package com.example.tableverse.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.adaptadores.AdaptadorUsuarios;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.ReservaEvento;
import com.example.tableverse.objetos.ReservaJuego;
import com.example.tableverse.objetos.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class DialogUsuariosApuntados extends DialogFragment {

    private AdminActividad adminActividad;
    private RecyclerView rv_usuarios;
    private DatabaseReference ref;
    private StorageReference sto;
    private List<Usuario> lista_usuarios;
    private AdaptadorUsuarios adaptadorUsuarios;
    private LinearLayoutManager llm;
    private Evento evento;
    private AlertDialog.Builder builder;


    public DialogUsuariosApuntados(){

    }

    private AlertDialog crearDialogUsuariosApuntados() {
        builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialog_usuarios_apuntados, null);
        builder.setView(v);

        rv_usuarios = v.findViewById(R.id.rv_usuarios);

        adminActividad = (AdminActividad) getActivity();
        ref = adminActividad.getRef();
        sto = adminActividad.getSto();
        lista_usuarios = adminActividad.getLista_usuarios();
        int pos = adminActividad.getPosition();
        evento = adminActividad.getLista_eventos().get(pos);

        cargarUsuarios();

        adaptadorUsuarios = new AdaptadorUsuarios(lista_usuarios, adminActividad.getApplicationContext());
        llm = new LinearLayoutManager(adminActividad.getApplicationContext());
        rv_usuarios.setLayoutManager(llm);
        rv_usuarios.setAdapter(adaptadorUsuarios);

        return builder.create();

    }




    private void cargarUsuarios() {
        ref.child("tienda").child("reservas_eventos").orderByChild("id_evento")
                .equalTo(evento.getId()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_usuarios.clear();
                for(DataSnapshot hijo: snapshot.getChildren()){
                    ReservaEvento pojo_reserva = hijo.getValue(ReservaEvento.class);

                    ref.child("tienda").child("clientes").orderByKey()
                            .equalTo(pojo_reserva.getId_cliente())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    final Usuario pojo_cliente = snapshot.getChildren().iterator().next().getValue(Usuario.class);
                                    pojo_cliente.setId(snapshot.getKey());
                                    lista_usuarios.add(pojo_cliente);

                                    adaptadorUsuarios.notifyDataSetChanged();

                                    sto.child("tienda").child("usuarios").child(pojo_cliente.getId())
                                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            pojo_cliente.setUrl_imagen(uri.toString());
                                            adaptadorUsuarios.notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            adaptadorUsuarios.notifyDataSetChanged();
                                        }
                                    });;

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof AdminActividad){
            this.adminActividad = (AdminActividad) context;
        }
    }
}