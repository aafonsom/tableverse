package com.example.tableverse;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.example.tableverse.servicios.ServicioNotificacion;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActividad extends AppCompatActivity {
    private NavController navController;
    private TabLayout tabLayout;
    private DatabaseReference ref;
    private StorageReference sto;
    private boolean doubleBackToExitPressedOnce = false;
    public static final String CHANNEL_ID = "tableverse_channel";

    public NavController getNavController(){ return navController; }
    public TabLayout getTabLayout(){ return tabLayout; }
    public DatabaseReference getRef(){ return ref; }
    public StorageReference getSto() {return sto; }
    private boolean alternar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("MODO", MODE_PRIVATE);
        alternar = sp.getBoolean("modo", false);
        alternarModoNoche(alternar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tabLayout = findViewById(R.id.tab_login);
        Intent servicio = new Intent(this, ServicioNotificacion.class);
        startService(servicio);
        createNotificationChannel();


        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();

        navController = Navigation.findNavController(this, R.id.nav_host_login);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                seleccionarTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void seleccionarTab(){
        if(tabLayout.getSelectedTabPosition() == 0){
            navController.navigate(R.id.login);
        }else{
            navController.navigate(R.id.registro);
        }
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones TableVerse";
            String description = "Canal de notificaciones de la aplicación tableverse";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    static public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));
        return id;
    }


    public void alternarModoNoche(boolean modoNoche){
        if(modoNoche){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Por favor, pulsa de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

}