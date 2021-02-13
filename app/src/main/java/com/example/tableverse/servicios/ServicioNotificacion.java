package com.example.tableverse.servicios;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.restrictions.RestrictionsReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tableverse.R;
import com.example.tableverse.inicio.Login;
import com.example.tableverse.objetos.Notificaciones;
import com.example.tableverse.objetos.ReservaJuego;
import com.example.tableverse.objetos.Usuario;
import com.example.tableverse.usuario.HistorialPedidos;
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
                final ReservaJuego pojo_reserva = snapshot.getValue(ReservaJuego.class);
                pojo_reserva.setId(snapshot.getKey());


                ref.child("tienda").child("clientes").orderByKey()
                        .equalTo(pojo_reserva.getId_cliente())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot hijo = snapshot.getChildren().iterator().next();
                        pojo_usuario = hijo.getValue(Usuario.class);
                        pojo_usuario.setId(hijo.getKey());
                        SharedPreferences sp = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        String id = sp.getString("id", "");

                        if(pojo_reserva.isPreparado()  &&
                                pojo_usuario.getEstado() == Usuario.PEDIDO_PREPARADO
                                && pojo_usuario.getId().equals(id)){
                            ref.child("tienda").child("clientes").child(pojo_usuario.getId())
                                    .child("estado").setValue(Usuario.NOTIFICADO);
                            Notificaciones nueva = new Notificaciones();
                            nueva.crearNotificacion(pojo_usuario, "Se ha procesado tu pedido",
                                    "Tu pedido ya está listo para recoger en tienda, " +
                                            "acercate cuando puedas", "Pedido procesado",
                                    R.mipmap.ic_launcher, Login.class,
                                    getApplicationContext());

                        }else if(!pojo_reserva.isPreparado() &&  pojo_usuario.getTipo().equals("admin")
                                && pojo_usuario.getEstado() == Usuario.PEDIDO_ESPERA){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




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
