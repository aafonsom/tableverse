package com.example.tableverse;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
        String fecha_hoy = formateador.format(hoy.getTime());
        return fecha_hoy;
    }

    public boolean esPosterior(String fecha){
        boolean res = false;

        Calendar hoy = Calendar.getInstance();
        Calendar fechaEvento = transfomarStringACalendar(fecha);


        if(hoy.before(fechaEvento) || esElMismoDia(fechaEvento)){
            res = true;
        }

        return res;
    }

    public Calendar transfomarStringACalendar(String fecha){
        Calendar fechaEvento = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            fechaEvento.setTime(sdf.parse(fecha));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fechaEvento;
    }

    public boolean esAforoValido(String aforoString){
        boolean validado = true;

        int aforo = Integer.parseInt(aforoString);
        if(aforo <= 0){
            validado = false;
        }

        return validado;
    }

    public boolean esElMismoDia(Calendar fechaEvento){
        boolean esMismoDia = false;
        Calendar hoy = Calendar.getInstance();

        if(hoy.get(Calendar.YEAR)  == fechaEvento.get(Calendar.YEAR) &&
            hoy.get(Calendar.MONTH) == fechaEvento.get(Calendar.MONTH) &&
            hoy.get(Calendar.DAY_OF_MONTH) == fechaEvento.get(Calendar.DAY_OF_MONTH)){
            esMismoDia = true;
        }

        return esMismoDia;
    }



}
