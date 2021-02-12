package com.example.tableverse.servicios;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.restrictions.RestrictionsReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tableverse.objetos.ReservaJuego;
import com.example.tableverse.objetos.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ServicioNotificacion extends IntentService {
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    Usuario pojo_usuario;
    public ServicioNotificacion() {
        super("Servicio Notificaciones Tableverse");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        ref.child("tienda").child("reservas_juegos").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ReservaJuego pojo_reserva = snapshot.getValue(ReservaJuego.class);
                pojo_reserva.setId(snapshot.getKey());


                ref.child("tienda").child("clientes").orderByKey()
                        .equalTo(pojo_reserva.getId_cliente())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pojo_usuario = snapshot.getValue(Usuario.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                SharedPreferences sp = getSharedPreferences("LOGIN", MODE_PRIVATE);


                if(pojo_reserva.isPreparado() &&
                        sp.getString("id", "").equals(pojo_usuario.getId()) &&
                        pojo_usuario.getEstado() == Usuario.PEDIDO_PREPARADO){
                    ref.child("tienda").child("clientes").child(pojo_usuario.getId()).child("estado").setValue(Usuario.NOTIFICADO);
                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
