package com.example.tableverse.inicio;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tableverse.AppUtilities;
import com.example.tableverse.LoginActividad;
import com.example.tableverse.R;
import com.example.tableverse.objetos.Usuario;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Registro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Registro extends Fragment {

    private static final int SELECCIONAR_FOTO = 1;
    private EditText et_nombre, et_pass, et_passrepit, et_email;
    private TextInputLayout til_pass;
    private ImageView fotoElegida;
    private DatabaseReference ref;
    private StorageReference sto;
    private Uri fotoUsuario_url;
    private NavController navController;

    private final static int SELECT_IMAGE = 2;

    private String mCurrentPhotoPath;

    private final static int REQUEST_TAKE_PHOTO = 3;
    private final static int REQUEST_IMAGE_CAPTURE = 3;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Registro() {
        // Required empty public constructor
    }

    public static Registro newInstance(String param1, String param2) {
        Registro fragment = new Registro();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_nombre = view.findViewById(R.id.et_nombre_usuario);
        et_pass = view.findViewById(R.id.et_registro_pass);
        et_passrepit = view.findViewById(R.id.et_registro_repitpass);
        et_email = view.findViewById(R.id.et_email);
        fotoElegida = view.findViewById(R.id.iv_foto_perfil);
        til_pass = view.findViewById(R.id.til_pass);



        LoginActividad loginActividad = (LoginActividad)getActivity();
        navController = loginActividad.getNavController();
        ref = loginActividad.getRef();
        sto = loginActividad.getSto();
        Button b = view.findViewById(R.id.b_registro);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarse(view);
            }
        });
        fotoElegida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarFotoPerfil(view);
            }
        });

        fotoUsuario_url = null;
    }

    public void registrarse(View v){
        final String nombre, pass, repit_pass, email;
        nombre = et_nombre.getText().toString().trim();
        pass = et_pass.getText().toString().trim();
        repit_pass = et_passrepit.getText().toString().trim();
        email = et_email.getText().toString().trim();
        boolean validacion = validar(nombre, pass, repit_pass, email);

        if(validacion){
            ref.child("tienda").child("clientes").orderByChild("nombre").equalTo(nombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()){
                        et_nombre.setError("Ese nombre de usuario ya existe");
                    }else{

                        Usuario nuevo = new Usuario(nombre, email, pass);
                        String id = ref.child("tienda").child("clientes").push().getKey();
                        ref.child("tienda").child("clientes").child(id).setValue(nuevo);
                        if(fotoUsuario_url != null){
                            sto.child("tienda").child("usuarios").child(id).putFile(fotoUsuario_url);
                        }

                        LoginActividad loginActividad = (LoginActividad)getActivity();
                        TabLayout.Tab tab = loginActividad.getTabLayout().getTabAt(0);
                        loginActividad.getTabLayout().selectTab(tab);
                        Toast.makeText(getContext(), "Te has registrado correctamente", Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.login);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(getContext(), "No se han validado los datos", Toast.LENGTH_SHORT).show();
        }


    }

    public boolean validar(String nombre, String pass, String repit_pass, String email){
        boolean validacion = true;

        if(nombre.equals("")){
            et_nombre.setError("El nombre no puede estar vacío");
            validacion = false;
        }

        if(pass.equals("")){
            til_pass.setError("La contraseña no puede estar vacía");
            validacion = false;
        }else{
            AppUtilities utilities = new AppUtilities();
            if(!pass.equals(repit_pass)){
                til_pass.setError("Las contraseñas deben ser iguales");
                validacion = false;
            }else if(!utilities.isValidPass(pass)){
                validacion = false;
                til_pass.setError("Debe contener por lo menos un dígito y tener una longitud entre 6 y 15 caracteres");
            }
        }

        if(email.equals("")){
            et_email.setError("El email es obligatorio");
            validacion = false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("No es un email válido");
            validacion = false;
        }

        return validacion;
    }


    public void seleccionarFotoPerfil(View v){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},1000);
        }
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.icono_redondo)
                .setTitle(R.string.confirmar_pedido)
                .setMessage(R.string.pedido_pregunta_confirmacion)
                .setPositiveButton(R.string.galeria, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,SELECCIONAR_FOTO);
                    }

                })
                .setNegativeButton(R.string.camara, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
                            File photoFile = null;

                            try{
                                photoFile = createImageFile();
                            }catch (IOException io){
                                io.printStackTrace();
                            }

                            if (photoFile != null){
                                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.tableverse.fileprovider", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                            }
                        }
                    }
                })
                .show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==SELECCIONAR_FOTO){
            fotoUsuario_url=data.getData();
            fotoElegida.setImageURI(fotoUsuario_url);
            Toast.makeText(getContext(), "Foto de perfil seleccionada con éxito", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Fallo al seleccionar la foto de perfil", Toast.LENGTH_SHORT).show();
        }

        if(requestCode == SELECT_IMAGE && resultCode == RESULT_OK){
            fotoUsuario_url = data.getData();
            fotoElegida.setImageURI(fotoUsuario_url);
            Toast.makeText(getContext(), "Image selected", Toast.LENGTH_LONG).show();
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            fotoElegida.setImageBitmap(bitmap);
            fotoUsuario_url = Uri.fromFile(new File(mCurrentPhotoPath));
        }
    }



    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void takePicture (){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
            File photoFile = null;

            try{
                photoFile = createImageFile();
            }catch (IOException io){
                io.printStackTrace();
            }

            if (photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(getContext(), "com.example.gamestoreapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


}