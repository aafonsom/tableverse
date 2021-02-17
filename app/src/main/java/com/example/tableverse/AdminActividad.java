package com.example.tableverse;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.ReservaJuego;
import com.example.tableverse.objetos.Usuario;
import com.example.tableverse.servicios.ServicioNotificacion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class AdminActividad extends AppCompatActivity {
    private FloatingActionButton fab;
    private NavController navController;
    private DatabaseReference ref;
    private StorageReference sto;
    private BottomNavigationView navView;
    private List<Juego> lista_juegos = new ArrayList<>();
    private List<Evento> lista_eventos = new ArrayList<>();
    private List<ReservaJuego> lista_pedidos = new ArrayList<>();
    private List<Usuario> lista_usuarios = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    private int position = 0;
    private Toolbar toolbar;

    public Toolbar getToolbar() { return toolbar; }
    public List<Usuario> getLista_usuarios() { return lista_usuarios; }
    public List<Juego> getLista_juegos(){ return lista_juegos; }
    public List<Evento> getLista_eventos(){ return lista_eventos; }
    public List<ReservaJuego> getLista_pedidos() { return lista_pedidos; }
    public NavController getNavController(){ return navController; }
    public DatabaseReference getRef(){ return ref; }
    public StorageReference getSto(){ return sto; }
    public int getPosition(){ return position; }
    public void setPosition(int position){ this.position = position; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_actividad);
        fab = findViewById(R.id.fab_admin);
        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();
        Intent servicio = new Intent(this, ServicioNotificacion.class);
        startService(servicio);

        toolbar = findViewById(R.id.tb_admin);

        navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.listaJuegosAdmin, R.id.listaEventosAdmin, R.id.listaPedidos)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    public void modoFab(int modo){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(fab, "alpha",0, 1);
        objectAnimator.setDuration(1000);
        objectAnimator.setStartDelay(0);
        switch (modo){
            case 1:
                fab.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.icono_add_juego);
                objectAnimator.start();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navController.navigate(R.id.crearJuego);
                    }
                });
                fab.setClickable(true);
                break;
            case 2:
                fab.setVisibility(View.VISIBLE);
                fab.setImageResource(R.drawable.calendar_add_event);
                objectAnimator.start();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navController.navigate(R.id.crearEvento);
                    }
                });
                fab.setClickable(true);
                break;
            case 3:
                objectAnimator = ObjectAnimator.ofFloat(fab, "alpha",1, 0);
                objectAnimator.setDuration(1500);
                objectAnimator.start();
                fab.setClickable(false);
                break;
        }
    }

    public void modoNavView(int modo){
        switch (modo){
            case 1:
                navView.setVisibility(View.VISIBLE);
                break;
            case 2:
                navView.setVisibility(View.GONE);
                break;
        }
    }

    public void logout(View v){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("LOGIN", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("id");
        editor.commit();
        Intent intent = new Intent(this, LoginActividad.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(navController.getCurrentDestination().getId() == R.id.crearJuego ||
                navController.getCurrentDestination().getId() == R.id.crearEvento ||
                navController.getCurrentDestination().getId() == R.id.verUsuarios){
            navController.popBackStack();
        }else{
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



}