package mz.maputobustracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mz.maputobustracker.adapter.CustomArrayAdapterH;
import mz.maputobustracker.domain.Historico;
import mz.maputobustracker.domain.Rota;
import mz.maputobustracker.domain.Utente;
import mz.maputobustracker.domain.util.LibraryClass;

/**
 * Created by Hawkingg on 28/08/2016.
 */
public class HistoricoActivity extends AppCompatActivity {

    private GoogleApiClient client;
    private ArrayList<Rota> rotas;
    private ProgressDialog progressDialog;
    private Utente ut;
    private ArrayList<Historico> list;
    private ListView lista;
    private Historico historico;
    private Rota rotaSelecionada;
    protected ProgressBar progressBar;
    private boolean notificado = false;
    private boolean firstTime = true;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        lista = (ListView) findViewById(R.id.lstHistorico);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Buscando Histórico de itinerarios do Utente, Por favor aguarde...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        progressDialog.setCancelable(false);
        Intent intent = getIntent();
        ut = (Utente) intent.getSerializableExtra("utilizador");
        rotaSelecionada = (Rota) intent.getSerializableExtra("RotaSelecionada");
        list = new ArrayList<>();
        LibraryClass.getFirebase().child("Historicos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are" + snapshot.getChildrenCount() + " Historicos");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if(postSnapshot.getValue(Historico.class) != null){
                        historico = postSnapshot.getValue(Historico.class);
                        if(historico.getCod_utilizador().equals(ut.getId()) && rotaSelecionada.getId().equals(historico.getCod_rota())) {
                            list.add(historico);
                            System.out.println("----------Post:" +historico.getCod_utilizador());
                        }
                    }
                }
                if(list.isEmpty()) {
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(HistoricoActivity.this);
                    builder.setMessage("Sem Históricos de Itinerarios para a rota selecionada")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    HistoricoActivity.this.finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    System.out.println("----------Post List:" + list.get(0).getCod_utilizador());
                    lista = (ListView) findViewById(R.id.lstHistorico);
                    if (lista != null) {
                        Collections.sort(list);
                        lista.setAdapter(new CustomArrayAdapterH(HistoricoActivity.this, android.R.layout.simple_list_item_1, list));
                        progressDialog.dismiss();
                    }
                    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                                long id) {
                            historico = (Historico) parent.getItemAtPosition(position);
                            AlertDialog.Builder builder = new AlertDialog.Builder(HistoricoActivity.this);
                            builder.setMessage("Deseja Navegar na Rota Selecionada?")
                                    .setCancelable(true)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Intent i = new Intent(HistoricoActivity.this, MapsActivity.class);
                                            i.putExtra("RotaSelecionada",rotaSelecionada);
                                            i.putExtra("selectedItinerario",historico.getItinerario());
                                            i.putExtra("utilizador",ut);
                                            Date finaldate = new Date();
                                            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyyhhmmss");
                                            final String date = DATE_FORMAT.format(finaldate);
                                            DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                            final String datenc = DATE_FORMAT.format(finaldate);
                                            historico.setDataUltima(datenc);
                                            Map<String, Object> newDate = new HashMap<String, Object>();
                                            newDate.put("dataUltima", datenc);
                                            LibraryClass.getFirebase().child("Historicos").child(historico.getId()).updateChildren(newDate);
                                            startActivity(i);
                                            HistoricoActivity.this.finish();
                                        }
                                    })
                                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    };
                    lista.setOnItemClickListener(listener);
                }
        }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("A busca por dados na nuvem falhou: " + firebaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if( keyCode== KeyEvent.KEYCODE_BACK)
        {
            progressDialog.cancel();
            this.finish();
        }

        return super.onKeyDown(keyCode, event);

    }
}
