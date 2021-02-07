package com.example.tableverse.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.admin.ListaJuegosAdmin;
import com.example.tableverse.objetos.Juego;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


public class DialogModJuego extends DialogFragment {
    private AdminActividad adminActividad;
    private Juego juego;
    private EditText et_nombre, et_categoria, et_precio, et_stock;
    private ImageView iv_foto;
    private Button modificar;
    private Uri foto_url;
    private StorageReference sto;
    private DatabaseReference ref;

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

        sto = adminActividad.getSto();
        ref = adminActividad.getRef();

        int pos = adminActividad.getPosition();
        juego = adminActividad.getLista_juegos().get(pos);
        foto_url = null;

        setView();

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
        final double precio;
        final int stock;

        nombre = et_nombre.getText().toString().trim();
        categoria = et_categoria.getText().toString().trim();
        precio = Double.parseDouble(et_precio.getText().toString().trim());
        stock = Integer.parseInt(et_stock.getText().toString().trim());

        if(validar(nombre, categoria, precio, stock)){
            if(nombre.equals(juego.getNombre())){
                realizarModificaciones(nombre, categoria, precio, stock);
            }else{
                ref.child("tienda").child("juegos").orderByChild("nombre").equalTo(nombre)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                Toast.makeText(adminActividad, "Ya existe un juego con ese nombre", Toast.LENGTH_SHORT).show();
                            }else{
                                realizarModificaciones(nombre, categoria, precio, stock);
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

        ref.child("tienda").child("juegos").child(juego.getId()).setValue(juego);
        if(foto_url != null){
            sto.child("tienda").child("juegos").child(juego.getId()).putFile(foto_url);
        }
        Toast.makeText(adminActividad, "Datos modificados con éxito", Toast.LENGTH_SHORT).show();

        dismiss();

    }

    private boolean hayCambios(String nombre, String categoria, double precio, int stock){
        boolean cambios = true;

        if(nombre.equals(juego.getNombre()) && categoria.equals(juego.getCategoria())
                && precio == juego.getPrecio() && stock == juego.getStock()){
            Toast.makeText(getContext(), "No se ha realizado ningun cambio", Toast.LENGTH_SHORT).show();
            cambios = false;
        }

        return cambios;
    }

    private boolean validar(String nombre, String categoria, double precio, int stock){
        boolean esValido = hayCambios(nombre, categoria, precio, stock);
        //TODO: mejorar la validación
        if(esValido){
            if(nombre.equals("") || categoria.equals("") || precio == 0.0 || stock == 0){
                esValido = false;
            }
        }
        return esValido;
    }


}