package com.example.tableverse.servicios;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ServicioNotificacion extends IntentService {

    public ServicioNotificacion() {
        super("Servicio Notificaciones Tableverse");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    }
}
