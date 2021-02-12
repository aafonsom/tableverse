package com.example.tableverse.objetos;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tableverse.LoginActividad;
import com.example.tableverse.R;

public class Notificaciones {

    public Notificaciones() {
    }

    public void crearNotificacion(Bitmap icon, String id, String title,
                                                        String contenido, String textoLargo,
                                                        PendingIntent pendingIntent, Context context){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(contenido)
                .setLargeIcon(icon)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textoLargo))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(LoginActividad.createID(), builder.build());

    }



}
