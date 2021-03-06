package com.example.tableverse.admin;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.adaptadores.AdaptadorUsuarios;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.ReservaEvento;
import com.example.tableverse.objetos.Usuario;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerUsuarios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerUsuarios extends Fragment {

    private AdminActividad adminActividad;
    private RecyclerView rv_usuarios;
    private DatabaseReference ref;
    private StorageReference sto;
    private List<Usuario> lista_usuarios;
    private AdaptadorUsuarios adaptadorUsuarios;
    private LinearLayoutManager llm;
    private Evento evento;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VerUsuarios() {
        // Required empty public constructor
    }


    public static VerUsuarios newInstance(String param1, String param2) {
        VerUsuarios fragment = new VerUsuarios();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ver_usuarios, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        rv_usuarios = v.findViewById(R.id.rv_usuarios);
        PieChart pieChart = v.findViewById(R.id.piechart_admin);

        adminActividad = (AdminActividad) getActivity();
        adminActividad.getToolbar().setNavigationIcon(R.drawable.arrow_back_white_24dp);
        ref = adminActividad.getRef();
        sto = adminActividad.getSto();
        lista_usuarios = adminActividad.getLista_usuarios();
        lista_usuarios.clear();
        adminActividad.modoFab(3);
        int pos = adminActividad.getPosition();
        evento = adminActividad.getLista_eventos().get(pos);
        adminActividad.getToolbar().setTitle(evento.getNombre());

        cargarUsuarios();
        setPieChart(pieChart);

        adaptadorUsuarios = new AdaptadorUsuarios(lista_usuarios, adminActividad.getApplicationContext());
        llm = new LinearLayoutManager(adminActividad.getApplicationContext());
        rv_usuarios.setLayoutManager(llm);
        rv_usuarios.setAdapter(adaptadorUsuarios);

    }

    private void setPieChart(PieChart pc){
        PieData pd;
        ArrayList<PieEntry> pieList = new ArrayList<>();
        PieDataSet pieDataSet;

        pc.setUsePercentValues(true);

        pieList.add(new PieEntry(evento.getOcupado(),"Apuntados"));
        pieList.add(new PieEntry(evento.getAforoMax() - evento.getOcupado(), "libres"));
        pieDataSet = new PieDataSet(pieList, "");
        pd = new PieData(pieDataSet);
        pc.setData(pd);

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pc.getDescription().setText("");
        Legend l = pc.getLegend();
        l.setTextSize(18f);
        pd.setValueTextSize(18f);
    }

    private void cargarUsuarios() {
        ref.child("tienda").child("reservas_eventos").orderByChild("id_evento")
                .equalTo(evento.getId()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_usuarios.clear();
                for(DataSnapshot hijo: snapshot.getChildren()){
                    ReservaEvento pojo_reserva = hijo.getValue(ReservaEvento.class);

                    ref.child("tienda").child("clientes").orderByKey()
                        .equalTo(pojo_reserva.getId_cliente())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                DataSnapshot hijo = snapshot.getChildren().iterator().next();
                                final Usuario pojo_cliente = hijo.getValue(Usuario.class);
                                pojo_cliente.setId(hijo.getKey());
                                lista_usuarios.add(pojo_cliente);

                                sto.child("tienda").child("usuarios").child(pojo_cliente.getId())
                                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        pojo_cliente.setUrl_imagen(uri.toString());
                                        adaptadorUsuarios.notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        adaptadorUsuarios.notifyDataSetChanged();
                                    }
                                });;

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}