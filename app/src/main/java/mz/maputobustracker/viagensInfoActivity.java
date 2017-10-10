package mz.maputobustracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import mz.maputobustracker.adapter.CustomArrayAdapter;
import mz.maputobustracker.domain.Ponto;
import mz.maputobustracker.domain.Rota;
import mz.maputobustracker.domain.Utente;
import mz.maputobustracker.domain.Viagem;
import mz.maputobustracker.domain.util.LibraryClass;

/**
 * Created by Hawkingg on 05/09/2016.
 */
public class viagensInfoActivity extends AppCompatActivity {

    private GoogleApiClient client;
    private ArrayList<Rota> rotas;
    private ProgressDialog progressDialog;
    private Utente ut;
    private ArrayList<Viagem> list;
    private ArrayList<String> formateList;
    private ListView lista;
    private Rota rotaSelecionada;
    protected ProgressBar progressBar;
    private boolean notificado = false;
    private Snackbar snackbar;
    private boolean notificadoP = false;
    private TextView txtNumero;
    private TextView txtNumeroIndisponiveis;
    private boolean firstTime = true;
    private boolean filtred = false;
    private Spinner spPontos;
    private Button btnFiltrar;
    private Button btnReset;
    private Ponto pntOgr;
    private ArrayList<Ponto> pontos;
    private ArrayList<Viagem> indisponiveis = new  ArrayList<Viagem>();
    private Rota selectedRota;
    private Ponto filtredPoint;
    private int countIndisponiveis =0;
    CustomArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viageminfo);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        selectedRota = (Rota) intent.getSerializableExtra("RotaSelecionada");
        ut= (Utente) intent.getSerializableExtra("utilizador");
        lista = (ListView) findViewById(R.id.lstViagens);
        txtNumero = (TextView) findViewById(R.id.txtNumerosAutocarros);
        txtNumeroIndisponiveis = (TextView) findViewById(R.id.txtNumeroIndisponiveis);

        spPontos = (Spinner) findViewById(R.id.spPontos);
        pontos = new ArrayList<>();
        pntOgr = new Ponto();
        pntOgr.setNome("Escolha uma opção");
        pontos.add(pntOgr);
        btnFiltrar = (Button) findViewById(R.id.btnFiltrar);
        btnReset = (Button) findViewById(R.id.btnReset);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Buscando Viagens dos autocarros, Por favor aguarde...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        progressDialog.setCancelable(false);
        rotaSelecionada = (Rota) intent.getSerializableExtra("RotaSelecionada");
        progressBar = (ProgressBar) findViewById(R.id.maps_viageminfo);
        formateList = new ArrayList<>();
        adapter = new CustomArrayAdapter(viagensInfoActivity.this, android.R.layout.simple_list_item_1, formateList);
        lista.setAdapter(adapter);
        LibraryClass.getFirebase().child("Paragens").child(selectedRota.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                pontos = new ArrayList<Ponto>();
                pontos.add(pntOgr);
                System.out.println("There are " + snapshot.getChildrenCount() + " Paragens");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Ponto post = postSnapshot.getValue(Ponto.class);
                    System.out.println(post.getNome() + " - " + post.getId());
                    pontos.add(post);
                }
                if (spPontos != null && !pontos.isEmpty()) {
                    spPontos.setEnabled(true);
                    btnFiltrar.setEnabled(true);
                    btnReset.setEnabled(true);
                    spPontos.setAdapter(new ArrayAdapter<Ponto>(viagensInfoActivity.this, android.R.layout.simple_list_item_1, pontos));
                }
                else
                {
                    spPontos.setEnabled(false);
                    btnFiltrar.setEnabled(false);
                    btnReset.setEnabled(false);
                    progressBar = (ProgressBar) findViewById(R.id.chooseProgress);
                    snackbar=Snackbar.make(progressBar,"Infelizmente o itinerário da rota selecionada está indisponível. Pf volte a tentar mais tarde",Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                    snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    snackbar.show();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        LibraryClass.getFirebase().child("Viagens").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                formateList = new ArrayList<>();
                System.out.println("There are" + dataSnapshot.getChildrenCount() + " Viagens");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getValue(Viagem.class) != null) {
                        Viagem post = postSnapshot.getValue(Viagem.class);
                        if (post.getCod_rota().equalsIgnoreCase(rotaSelecionada.getId())) {
                            list.add(post);
                            if(post.getDisponibilidade() == false)
                            {
                                countIndisponiveis=countIndisponiveis+1;
                                indisponiveis.add(post);
                            }
                            else{
                                for (Viagem viagem : list) {
                                    if(viagem.getId().equals(post.getId())){
                                        countIndisponiveis=countIndisponiveis-1;
                                        if(countIndisponiveis < 0)
                                        {
                                            countIndisponiveis=0;
                                        }
                                        indisponiveis.remove(viagem);
                                    }
                                }
                                }
                        }
                        System.out.println("----------Post:" + post.toString());
                    }
                }
                if(countIndisponiveis > 0 )
                {
                    txtNumeroIndisponiveis.setBackgroundColor(Color.RED);
                }else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txtNumeroIndisponiveis.setBackgroundColor(getColor(R.color.amarelo));
                    }
                    else
                    {
                        txtNumeroIndisponiveis.setBackgroundColor(getResources().getColor(R.color.amarelo));
                    }
                }
                txtNumeroIndisponiveis.setText("Nº de Autocarros Indisponíveis na rota: "+countIndisponiveis);
                if (filtred == false) {
                    if (list.isEmpty()) {
                        progressDialog.dismiss();
                        lista.setAdapter(null);
                        notificado = false;
                        firstTime = true;
                        countIndisponiveis =0;
                        txtNumero.setVisibility(View.INVISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            txtNumeroIndisponiveis.setBackgroundColor(getColor(R.color.amarelo));
                        }
                        else
                        {
                            txtNumeroIndisponiveis.setBackgroundColor(getResources().getColor(R.color.amarelo));
                        }
                        snackbar=Snackbar.make(progressBar, "De momento não existem autocarros em viagem na rota selecionada", Snackbar.LENGTH_INDEFINITE);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                        snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                        snackbar.show();
                        txtNumeroIndisponiveis.setText("Nº de Autocarros Indisponíveis na rota: "+countIndisponiveis);
                    } else {
                        lista = (ListView) findViewById(R.id.lstViagens);
                        Collections.sort(list);
                        for (Viagem vg : list) {
                            String text = "";
                            String chegou = "";
                            if (vg.getChegouDestino() == true) {
                                chegou = "Sim";
                                text =
                                        "Descrição: " + vg.getDescricao() + "\n"
                                                + "Data & Hora :"+vg.getDataHora()+"\n"
                                                + "Origem: " + vg.getAnteriorParagem() + "\n"
                                                + "Destino:" + vg.getProximaParagem() + "\n"
                                                +  vg.getKmapercorrer()+"\n"
                                                +  vg.getKmpercorridos()+"\n"
                                                + "Chegou ao destino: " + chegou + "\n"
                                                + "Latitude: " + vg.getLatitude() + "\n"
                                                + "Longitude: " + vg.getLongitude() + "\n"
                                                + "Velocidade: " + vg.getVelocidade() + "\n"
                                                + "Disponibilidade: "+vg.getInfoDisponibilidade() +"\n";
                            } else {
                                chegou = "Não";
                                text =
                                        "Descrição: " + vg.getDescricao() + "\n"
                                                + "Data & Hora :"+vg.getDataHora()+"\n"
                                                + "Origem: " + vg.getAnteriorParagem() + "\n"
                                                + "Destino:" + vg.getProximaParagem() + "\n"
                                                +  vg.getKmapercorrer()+"\n"
                                                +  vg.getKmpercorridos()+"\n"
                                                + "Chegou ao destino: " + chegou + "\n"
                                                + "Latitude: " + vg.getLatitude() + "\n"
                                                + "Longitude: " + vg.getLongitude() + "\n"
                                                + "Velocidade: " + vg.getVelocidade() + "\n"
                                                + "Disponibilidade: "+vg.getInfoDisponibilidade() +"\n"
                                                + "Tempo Estimado de chegada: " + vg.getTempoEstChgada();
                            }
                            formateList.add(text);
                        }
                        if (lista != null) {
                            adapter.clear();
                            adapter.addAll(formateList);
                            adapter.notifyDataSetChanged();
                            txtNumero.setText("Nº de Autocarros na rota:"+formateList.size());
                            txtNumero.setVisibility(View.VISIBLE);
                            if (notificado == false && firstTime == false) {
                                adapter = new CustomArrayAdapter(viagensInfoActivity.this, android.R.layout.simple_list_item_1, formateList);
                                lista.setAdapter(adapter);
                                snackbar=Snackbar.make(progressBar, "Informação actualizada em tempo real", Snackbar.LENGTH_INDEFINITE);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
                                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                                snackbar.show();
                                notificado = true;
                            }
                            firstTime = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            progressDialog.dismiss();
                        }
                    }
                }
                else
                {
                        progressDialog.setMessage("Filtrando Viagens dos autocarros, Por favor aguarde...");
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        ArrayList<Viagem> filtredLis = new ArrayList<Viagem>();
                        for(Viagem vg: list)
                        {
                            if(vg.getCod_proximaParagem().equals(filtredPoint.getId()))
                            {
                                filtredLis.add(vg);
                            }
                        }
                        System.out.println("COUNT>"+filtredLis.size());
                        System.out.println("STATUS BOOLENA "+filtredLis.isEmpty());
                        if (filtredLis.isEmpty()) {
                            progressDialog.dismiss();
                            lista.setAdapter(null);
                            if(notificadoP ==false) {
                                txtNumero.setVisibility(View.INVISIBLE);
                                countIndisponiveis =0;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    txtNumeroIndisponiveis.setBackgroundColor(getColor(R.color.amarelo));
                                }
                                else
                                {
                                    txtNumeroIndisponiveis.setBackgroundColor(getResources().getColor(R.color.amarelo));
                                }
                                txtNumeroIndisponiveis.setText("Nº de Autocarros Indisponíveis na rota: "+countIndisponiveis);
                                snackbar=Snackbar.make(progressBar, "De momento não existem autocarros em direção a paragem selecionada", Snackbar.LENGTH_INDEFINITE);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                                snackbar.show();
                                notificadoP =true;
                            }
                        } else {
                            if(notificadoP==true) {
                                adapter = new CustomArrayAdapter(viagensInfoActivity.this, android.R.layout.simple_list_item_1, formateList);
                                lista.setAdapter(adapter);
                                snackbar=Snackbar.make(progressBar, "Informação actualizada em tempo real", Snackbar.LENGTH_INDEFINITE);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
                                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                                snackbar.show();
                                notificadoP=false;
                            }
                            lista = (ListView) findViewById(R.id.lstViagens);
                            Collections.sort(list);
                            formateList = new ArrayList<>();
                            for (Viagem vg : filtredLis) {
                                String text = "";
                                String chegou = "";
                                if (vg.getChegouDestino() == true) {
                                    chegou = "Sim";
                                    text =
                                            "Descrição: " + vg.getDescricao() + "\n"
                                                    + "Data & Hora :"+vg.getDataHora()+"\n"
                                                    + "Origem: " + vg.getAnteriorParagem() + "\n"
                                                    + "Destino:" + vg.getProximaParagem() + "\n"
                                                    + "Chegou ao destino: " + chegou + "\n"
                                                    + "Latitude: " + vg.getLatitude() + "\n"
                                                    + "Longitude: " + vg.getLongitude() + "\n"
                                                    + "Velocidade: " + vg.getVelocidade() + "\n"
                                                    + "Disponibilidade: "+vg.getInfoDisponibilidade() +"\n";
                                } else {
                                    chegou = "Não";
                                    text =
                                            "Descrição: " + vg.getDescricao() + "\n"
                                                    + "Data & Hora :"+vg.getDataHora()+"\n"
                                                    + "Origem: " + vg.getAnteriorParagem() + "\n"
                                                    + "Destino:" + vg.getProximaParagem() + "\n"
                                                    + "Chegou ao destino: " + chegou + "\n"
                                                    + "Latitude: " + vg.getLatitude() + "\n"
                                                    + "Longitude: " + vg.getLongitude() + "\n"
                                                    + "Velocidade: " + vg.getVelocidade() + "\n"
                                                    + "Disponibilidade: "+vg.getInfoDisponibilidade() +"\n"
                                                    + "Tempo Estimado de chegada: " + vg.getTempoEstChgada();
                                }
                                System.out.println(text);
                                formateList.add(text);
                            }
                            if (lista != null) {
                                adapter.clear();
                                adapter.addAll(formateList);
                                adapter.notifyDataSetChanged();

                                txtNumero.setText("Nº de Autocarros a caminho da paragem:"+formateList.size());
                                txtNumero.setVisibility(View.VISIBLE);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                progressDialog.dismiss();
                            }
                        }
                    }
                }
                @Override
                public void onCancelled (DatabaseError databaseError){
                }
        });
    }
    public void filtarViagem(View view)
    {
        filtredPoint = (Ponto) spPontos.getSelectedItem();
        if (filtredPoint != pntOgr) {
            filtred = true;
            progressDialog.setMessage("Filtrando Viagens dos autocarros, Por favor aguarde...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            progressDialog.setCancelable(false);
            ArrayList<Viagem> filtredLis = new ArrayList<Viagem>();
            for(Viagem vg: list)
            {
                if(vg.getCod_proximaParagem().equals(filtredPoint.getId()))
                {
                    filtredLis.add(vg);
                }
            }
            System.out.println("COUNT>"+filtredLis.size());
            System.out.println("STATUS BOOLENA "+filtredLis.isEmpty());
            if (filtredLis.isEmpty()) {
                progressDialog.dismiss();
                lista.setAdapter(null);
                if(notificadoP ==false) {
                    txtNumero.setVisibility(View.INVISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txtNumeroIndisponiveis.setBackgroundColor(getColor(R.color.amarelo));
                    }
                    else
                    {
                        txtNumeroIndisponiveis.setBackgroundColor(getResources().getColor(R.color.amarelo));
                    }
                    txtNumeroIndisponiveis.setText("Nº de Autocarros Indisponíveis na rota: "+countIndisponiveis);
                    countIndisponiveis =0;
                    snackbar=Snackbar.make(progressBar, "De momento não existem autocarros em direção a paragem selecionada", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                    snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    snackbar.show();
                    notificadoP =true;
                }
            } else {
                if(notificadoP==true) {
                    adapter = new CustomArrayAdapter(viagensInfoActivity.this, android.R.layout.simple_list_item_1, formateList);
                    lista.setAdapter(adapter);
                    snackbar=Snackbar.make(progressBar, "Informação actualizada em tempo real", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
                    snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    snackbar.show();
                    notificadoP=false;
                }
                lista = (ListView) findViewById(R.id.lstViagens);
                Collections.sort(list);
                formateList = new ArrayList<>();
                for (Viagem vg : filtredLis) {
                    String text = "";
                    String chegou = "";
                    if (vg.getChegouDestino() == true) {
                        chegou = "Sim";
                        text =
                                "Descrição: " + vg.getDescricao() + "\n"
                                        + "Data & Hora :"+vg.getDataHora()+"\n"
                                        + "Origem: " + vg.getAnteriorParagem() + "\n"
                                        + "Destino:" + vg.getProximaParagem() + "\n"
                                        + "Chegou ao destino: " + chegou + "\n"
                                        + "Latitude: " + vg.getLatitude() + "\n"
                                        + "Longitude: " + vg.getLongitude() + "\n"
                                        + "Velocidade: " + vg.getVelocidade() + "\n"
                                        + "Disponibilidade: "+vg.getInfoDisponibilidade() +"\n";
                    } else {
                        chegou = "Não";
                        text =
                                "Descrição: " + vg.getDescricao() + "\n"
                                        + "Data & Hora :"+vg.getDataHora()+"\n"
                                        + "Origem: " + vg.getAnteriorParagem() + "\n"
                                        + "Destino:" + vg.getProximaParagem() + "\n"
                                        + "Chegou ao destino: " + chegou + "\n"
                                        + "Latitude: " + vg.getLatitude() + "\n"
                                        + "Longitude: " + vg.getLongitude() + "\n"
                                        + "Velocidade: " + vg.getVelocidade() + "\n"
                                        + "Disponibilidade: "+vg.getInfoDisponibilidade() +"\n"
                                        + "Tempo Estimado de chegada: " + vg.getTempoEstChgada();
                    }
                    System.out.println(text);
                    formateList.add(text);
                }
                if (lista != null) {
                    adapter.clear();
                    adapter.addAll(formateList);
                    adapter.notifyDataSetChanged();
                    // fire the event
                    txtNumero.setText("Nº de Autocarros a caminho da paragem:"+formateList.size());
                    txtNumero.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    progressDialog.dismiss();
                }
            }
        }
        else
        {
            snackbar= Snackbar.make(progressBar, "Por favor especifique a paragem para a qual deseja filtar", Snackbar.LENGTH_INDEFINITE);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
            snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
            snackbar.show();
        }
        notificadoP =false;
    }
    public void resetarViagem(View view)
    {
        lista.setAdapter(null);
        notificado = false;
        firstTime = false;
        notificadoP=false;
        filtred=false;
        if (spPontos != null && !pontos.isEmpty()) {
            spPontos.setAdapter(new ArrayAdapter<Ponto>(viagensInfoActivity.this, android.R.layout.simple_list_item_1, pontos));
        }
        progressDialog.setMessage("Buscando Viagens dos autocarros, Por favor aguarde...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        progressDialog.setCancelable(false);
        if (list.isEmpty()) {
            progressDialog.dismiss();
            lista.setAdapter(null);
            notificado = false;
            firstTime = true;
            txtNumero.setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                txtNumeroIndisponiveis.setBackgroundColor(getColor(R.color.amarelo));
            }
            else
            {
                txtNumeroIndisponiveis.setBackgroundColor(getResources().getColor(R.color.amarelo));
            }
            txtNumeroIndisponiveis.setText("Nº de Autocarros Indisponíveis na rota: "+countIndisponiveis);
            snackbar=Snackbar.make(progressBar, "De momento não existem autocarros em viagem na rota selecionada", Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
            snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
            snackbar.show();
        } else {
            lista = (ListView) findViewById(R.id.lstViagens);
            Collections.sort(list);
            if (lista != null) {
                lista.setAdapter(adapter);
                adapter.clear();
                adapter.addAll(formateList);
                adapter.notifyDataSetChanged();

                txtNumero.setText("Nº de Autocarros na rota:"+formateList.size());
                txtNumero.setVisibility(View.VISIBLE);
                if (notificado == false && firstTime == false) {
                    adapter = new CustomArrayAdapter(viagensInfoActivity.this, android.R.layout.simple_list_item_1, formateList);
                    lista.setAdapter(adapter);
                    snackbar=Snackbar.make(progressBar, "Informação actualizada em tempo real", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
                    snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                    snackbar.show();
                    notificado = true;
                }
                firstTime = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                progressDialog.dismiss();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if( keyCode== KeyEvent.KEYCODE_BACK)
        {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
