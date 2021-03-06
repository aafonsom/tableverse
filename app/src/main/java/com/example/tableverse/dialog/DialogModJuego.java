package com.example.tableverse.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.objetos.Juego;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;


public class DialogModJuego extends DialogFragment {
    private AdminActividad adminActividad;
    private Juego juego;
    private EditText et_nombre, et_categoria, et_precio, et_stock;
    private Switch sw_disponibilidad;
    private ImageView iv_foto;
    private Button modificar;
    private Uri foto_url;
    private StorageReference sto;
    private DatabaseReference ref;
    private static final int SELECCIONAR_FOTO = 1;


    public DialogModJuego(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogModJuego();
    }

    private AlertDialog crearDialogModJuego() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialog_mod_juego, null);
        builder.setView(v);

        et_nombre = v.findViewById(R.id.et_nombre);
        et_categoria = v.findViewById(R.id.et_categoria);
        et_precio = v.findViewById(R.id.et_precio);
        et_stock = v.findViewById(R.id.et_stock);
        iv_foto = v.findViewById(R.id.iv_foto_juego);
        modificar = v.findViewById(R.id.b_modificar);
        sw_disponibilidad = v.findViewById(R.id.sw_disponibilidad);

        sto = adminActividad.getSto();
        ref = adminActividad.getRef();

        int pos = adminActividad.getPosition();
        juego = adminActividad.getLista_juegos().get(pos);
        foto_url = null;

        setView();
        iv_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarFoto(view);
            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarOnClick();
            }
        });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof AdminActividad){
            this.adminActividad = (AdminActividad) context;
        }
    }

    private void setView(){
        et_nombre.setText(juego.getNombre());
        et_categoria.setText(juego.getCategoria());
        et_precio.setText(Double.toString(juego.getPrecio()));
        et_stock.setText(Integer.toString(juego.getStock()));
        sw_disponibilidad.setChecked(juego.isDisponible());

        sto.child("tienda").child("juegos").child(juego.getId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(juego.getUrl_juego())
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.stat_notify_error).into(iv_foto);
            }
        });

    }

    private void modificarOnClick(){
        final String nombre, categoria;
        final String precio;
        final String stock;

        nombre = et_nombre.getText().toString().trim();
        categoria = et_categoria.getText().toString().trim();
        precio = et_precio.getText().toString().trim();
        stock = et_stock.getText().toString().trim();

        if(validar(nombre, categoria, precio, stock)){
            if(nombre.equals(juego.getNombre())){
                realizarModificaciones(nombre, categoria, Double.parseDouble(precio), Integer.parseInt(stock));
            }else{
                ref.child("tienda").child("juegos").orderByChild("nombre").equalTo(nombre)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                et_nombre.setError("Ya existe un juego con este nombre");
                            }else{
                                realizarModificaciones(nombre, categoria, Double.parseDouble(precio), Integer.parseInt(stock));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        }
    }

    private void realizarModificaciones(String nombre, String categoria, double precio, int stock) {
        juego.setNombre(nombre);
        juego.setCategoria(categoria);
        juego.setPrecio(precio);
        juego.setStock(stock);
        juego.setDisponible(sw_disponibilidad.isChecked());
        ref.child("tienda").child("juegos").child(juego.getId()).setValue(juego);
        if(foto_url != null){
            sto.child("tienda").child("juegos").child(juego.getId()).putFile(foto_url);
        }
        Toast.makeText(adminActividad, "Datos modificados con éxito", Toast.LENGTH_SHORT).show();
        dismiss();
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean hayCambios(String nombre, String categoria, double precio, int stock){
        boolean cambios = true;

        if(nombre.equals(juego.getNombre()) && categoria.equals(juego.getCategoria())
                && precio == juego.getPrecio() && stock == juego.getStock() &&
                sw_disponibilidad.isChecked() == juego.isDisponible() && foto_url == null){
            Toast.makeText(getContext(), "No se ha realizado ningun cambio", Toast.LENGTH_SHORT).show();
            cambios = false;
        }

        return cambios;
    }

    private boolean validar(String nombre, String categoria, String precio, String stock){
        boolean esValido = true;

        if(nombre.equals("")){
            et_nombre.setError("El nombre no puede estar vacío");
            esValido = false;
        }

        if(categoria.isEmpty()){
            et_categoria.setError("La categoría no puede estar vacía");
            esValido = false;
        }

        if(precio.isEmpty()){
            et_precio.setError("El precio no puede estar vacío");
            esValido = false;
        }else{
            double pre = Double.parseDouble(precio);
            if(pre == 0){
                et_precio.setError("El precio no puede ser 0");
                esValido = false;
            }
        }

        if(stock.isEmpty()){
            et_stock.setError("El stock no puede estar vacío");
            esValido = false;
        }

        if(esValido){
            boolean comprobacion = hayCambios(nombre, categoria, Double.parseDouble(precio), Integer.parseInt(stock));
            if(!comprobacion){
                esValido = false;
            }
        }
        return esValido;
    }

    public void seleccionarFoto(View v){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,SELECCIONAR_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==SELECCIONAR_FOTO){
            foto_url=data.getData();
            iv_foto.setImageURI(foto_url);
            iv_foto.setImageTintMode(null);
            Toast.makeText(getContext(), "Foto de perfil seleccionada con éxito", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Fallo al seleccionar la foto de perfil", Toast.LENGTH_SHORT).show();
        }
    }


}