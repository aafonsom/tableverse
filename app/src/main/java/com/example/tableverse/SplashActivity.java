package com.example.tableverse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.tableverse.objetos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private Usuario usuario;
    private DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ref = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sp = getSharedPreferences("LOGIN", MODE_PRIVATE);

        String id = sp.getString("id", "");
        if(!id.equals("")){
            cargarUsuario(id);
        }else{
            Intent intent = new Intent(this, LoginActividad.class);
            startActivity(intent);
        }
    }

    private void cargarUsuario(String id){
        ref.child("tienda").child("clientes").orderByKey()
                .equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                    usuario = dataSnapshot.getValue(Usuario.class);
                    usuario.setId(snapshot.getKey());
                    loguearse();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loguearse(){
        Intent intent;
        if(usuario.getTipo().equals("admin")){
            intent = new Intent(this, AdminActividad.class);
        }else{
            intent = new Intent(this, UsuarioActividad.class);
        }
        startActivity(intent);
    }


}