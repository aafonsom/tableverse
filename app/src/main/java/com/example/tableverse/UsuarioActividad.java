package com.example.tableverse;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.tableverse.adaptadores.AdaptadorEventos;
import com.example.tableverse.adaptadores.AdaptadorJuegos;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.Usuario;
import com.example.tableverse.objetos.VolleySingleton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsuarioActividad extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView iv_nav_header;
    private TextView tv_nav_nombre, tv_nav_email;
    private DatabaseReference ref;
    private StorageReference sto;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Usuario usuario;
    private List<Juego> lista_juegos = new ArrayList<>();
    private List<Evento> lista_eventos = new ArrayList<>();
    private NavController navController;
    public AdaptadorJuegos adaptadorJuegos;
    private AdaptadorEventos adaptadorEventos;
    private SearchView searchView;
    private int position = 0;
    private char[] divisas = {'€', '$', '£'};
    private long lastTime;
    private long  ON_DAY_MS = 86400000;
    private double[] ratios = {-1, -1, -1, -1};
    private int pos_ratio_elegido = -1;

    public SharedPreferences getSp() { return sp; }
    public SharedPreferences.Editor getEditor() { return editor; }
    public double[] getRatios() { return ratios; }
    public int getPos_ratio_elegido() { return pos_ratio_elegido; }
    public void setPos_ratio_elegido(int pos_ratio_elegido) { this.pos_ratio_elegido = pos_ratio_elegido; }
    public char[] getDivisas() { return divisas; }
    public Usuario getUsuario() { return usuario; }
    public List<Juego> getLista_juegos() { return lista_juegos; }
    public List<Evento> getLista_eventos() { return lista_eventos; }
    public AdaptadorJuegos getAdaptadorJuegos() { return adaptadorJuegos; }
    public AdaptadorEventos getAdaptadorEventos() { return adaptadorEventos; }
    public DatabaseReference getRef(){ return ref; }
    public StorageReference getSto(){ return sto; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public NavController getNavController(){ return navController; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_actividad);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        iv_nav_header = headerLayout.findViewById(R.id.iv_nav_header);
        tv_nav_email = headerLayout.findViewById(R.id.tv_nav_email);
        tv_nav_nombre = headerLayout.findViewById(R.id.tv_nav_nombre);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.eventosApuntado, R.id.eventosDisponibles, R.id.listaJuegosUsuario, R.id.historialPedidos)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_usuario);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();

        sp = getSharedPreferences("LOGIN", MODE_PRIVATE);
        editor = sp.edit();
        String id = sp.getString("id", "");
        if(id.equals("")){
            Intent intent = new Intent(this, LoginActividad.class);
            startActivity(intent);
        }else{
            cargarDatosUsuario(id);
        }

        sp = getSharedPreferences("sp_api_moneda", this.MODE_PRIVATE);
        editor = sp.edit();
        lastTime = sp.getLong("lastTime", -1);
        ratios[0] = sp.getFloat("USD", -1);
        ratios[1] = sp.getFloat("GBP", -1);
        pos_ratio_elegido = sp.getInt("pos", -1);
        /*ratios[2] = sp.getFloat("CNY", -1);
        ratios[3] = sp.getFloat("RUB", -1);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.usuario_actividad, menu);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.busqueda_juegos).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adaptadorJuegos.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adaptadorJuegos.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences sp = getApplicationContext().getSharedPreferences("LOGIN", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("id");
                editor.commit();
                Intent intent = new Intent(this, LoginActividad.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                navController.navigate(R.id.configuracionUsuario);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_usuario);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setViewNav(){
        tv_nav_nombre.setText(usuario.getNombre());
        tv_nav_email.setText(usuario.getCorreo());

        Glide.with(this).load(usuario.getUrl_imagen())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(R.drawable.person_morada).into(iv_nav_header);
    }

    private void cargarDatosUsuario(String id){
        ref.child("tienda").child("clientes").orderByKey().equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                    usuario = dataSnapshot.getValue(Usuario.class);
                    usuario.setId(dataSnapshot.getKey());


                    CargarFoto cf = new CargarFoto();
                    cf.start();
                    setViewNav();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private class CargarFoto extends Thread{

        public CargarFoto() {
        }

        @Override
        public void run(){
            sto.child("tienda").child("usuarios").child(usuario.getId()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    usuario.setUrl_imagen(uri.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setViewNav();
                        }
                    });
                }
            });
        }
    }

    public void refreshCache(){
        String url = "https://api.exchangeratesapi.io/latest";
        JsonObjectRequest json_req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject rates = response.getJSONObject("rates");
                            ratios[0] = rates.getDouble("USD");
                            editor.putFloat("USD", (float)ratios[0]);
                            ratios[1] = rates.getDouble("GBP");
                            editor.putFloat("GBP", (float)ratios[1]);
                            /*ratios[2] = rates.getDouble("CNY");
                            editor.putFloat("CNY", (float)ratios[2]);
                            ratios[3] = rates.getDouble("RUB");
                            editor.putFloat("RUB", (float)ratios[3]);*/

                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                // Si hay algún poblema al hacer la conexión
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(getApplicationContext(), "Error al hacer la petición",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(json_req);
    }

    @Override
    protected void onStart() {
        super.onStart();
        long now = System.currentTimeMillis();
        if(lastTime == -1 || isMoreThanOneDay(now)){
            editor.putLong("lastTime", now);
            editor.commit();
            refreshCache();
        }
    }

    public boolean isMoreThanOneDay(long now){
        boolean isMore = false;
        if(lastTime != -1 && now-lastTime>ON_DAY_MS){
            isMore = true;
        }
        return isMore;
    }


}