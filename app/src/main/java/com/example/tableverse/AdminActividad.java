package com.example.tableverse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.example.tableverse.admin.CrearJuego;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.Juego;
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
    private int position = 0;

    public List<Juego> getLista_juegos(){ return lista_juegos; }
    public List<Evento> getLista_eventos(){ return lista_eventos; }
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.listaJuegosAdmin, R.id.listaEventosAdmin, R.id.listaPedidos)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }

    public void modoFab(int modo){

        switch (modo){
            case 1:
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navController.navigate(R.id.crearJuego);
                    }
                });
                break;
            case 2:
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navController.navigate(R.id.crearEvento);
                    }
                });
                break;
            case 3:
                fab.setVisibility(View.GONE);
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






}