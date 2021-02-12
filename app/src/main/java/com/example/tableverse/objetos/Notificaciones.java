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

import java.io.Serializable;

public class Notificaciones {

    public Notificaciones() {
    }

    public void crearNotificacion(Serializable pojo, String contenido, String expandido,
                                  String titulo,  int icono_big, Class destino,
                                  Context context){
        Intent intent = new Intent(context, destino);
        intent.putExtra("USUARIO",pojo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bitmap imagen = BitmapFactory.decodeResource(context.getResources(),
                icono_big);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                LoginActividad.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setLargeIcon(imagen)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(expandido))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(LoginActividad.createID(), builder.build());

    }



}
