package com.example.tableverse;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.example.tableverse.admin.CrearJuego;
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

public class AdminActividad extends AppCompatActivity {
    private FloatingActionButton fab;
    private NavController navController;
    private DatabaseReference ref;
    private StorageReference sto;

    //Dudo que el getFab sea necesario, eliminar si no le encuentro utilidad
    /*public FloatingActionButton getFab(){ return fab; }*/
    public NavController getNavController(){ return navController; }
    public DatabaseReference getRef(){ return ref; }
    public StorageReference getSto(){ return sto; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_actividad);
        fab = findViewById(R.id.fab_admin);
        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.listaJuegosAdmin, R.id.listaEventosAdmin, R.id.listaPedidos)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void modoFab(int modo){
        //Añadir aqui navegacion, ir a crear juego, a crear evento, desaparecer en pedidos

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
                break;
            case 3:
                fab.setVisibility(View.GONE);
                break;
        }
    }






}