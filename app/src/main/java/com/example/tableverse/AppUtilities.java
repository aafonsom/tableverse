package com.example.tableverse;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppUtilities {
    DatabaseReference ref;
    StorageReference sto;
    public AppUtilities(){
        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();
    }

    public boolean isValidPass(String pass){
        boolean isValidValue = false;
        int tam = pass.length();
        if(tam>5 && tam<15 && pass.matches(".*\\d.*")){
            isValidValue = true;
        }
        return isValidValue;
    }

    public boolean isValidId(String id){
        final boolean[] res = {false};

        ref.child("tienda").child("usuarios").orderByKey()
                .equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                 res[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return res[0];
    }

    public String getDate(){
        Calendar hoy = Calendar.getInstance();
        SimpleDateFormat formateador = new SimpleDateFormat("YYYY-MM-dd");
        String fecha_hoy = formateador.format(hoy.getTime());
        return fecha_hoy;
    }

}
