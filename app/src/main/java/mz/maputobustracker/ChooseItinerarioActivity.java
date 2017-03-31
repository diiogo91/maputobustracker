package mz.maputobustracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mz.maputobustracker.domain.Historico;
import mz.maputobustracker.domain.Ponto;
import mz.maputobustracker.domain.Rota;
import mz.maputobustracker.domain.Utente;
import mz.maputobustracker.domain.util.LibraryClass;

/**
 * Created by Hawkingg on 23/08/2016.
 */
public class ChooseItinerarioActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ArrayList<Ponto> paragens;
    private Rota selectedRota;
    private Spinner sporigem;
    private FirebaseAuth mAuth;
    private Spinner spdestino;
    private ProgressDialog progressDialog;
    private Historico.Itinerario selectedItinerario = new Historico.Itinerario();
    private Utente ut;
    private Historico historico;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ArrayList<Ponto> paragensOg;
    private boolean firstTime =true;
    private ArrayList<Ponto> fixOrigemPontosDest;
    private ArrayList<Ponto> fixOrigemPontosOrig;
    private Ponto origem;
    public boolean selecionouOrigem =false;
    public boolean selecionouDestino=false;
    private Ponto destino ;
    private Ponto pntOgr;
    private Snackbar snackbar;
    private ProgressBar progressBar;
    private TextView txtNumero;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerario);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        selectedRota = (Rota) intent.getSerializableExtra("RotaSelecionada");
        ut= (Utente) intent.getSerializableExtra("utilizador");
        System.out.println("-----------------------------"+ut.getId());
        System.out.println("ID"+ut.getId());
        System.out.println("Selected Rota: " + selectedRota.getName());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Buscando Itinerarios disponiveis, Por favor aguarde...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        progressDialog.setCancelable(false);
        final ListView lv = (ListView) findViewById(R.id.lstPontosParagens);
        sporigem = (Spinner) findViewById(R.id.spinner);
        spdestino = (Spinner) findViewById(R.id.spinner2);
        final Button btnObterRota =(Button) findViewById(R.id.btnRedefinir);
        spdestino.setEnabled(false);
        paragens = new ArrayList<Ponto>();
        paragensOg = new ArrayList<Ponto>();
        fixOrigemPontosDest = new ArrayList<Ponto>();
        fixOrigemPontosOrig = new ArrayList<Ponto>();
        pntOgr = new Ponto();
        pntOgr.setNome("Escolha uma opção");
        fixOrigemPontosOrig.add(pntOgr);
        fixOrigemPontosDest.add(pntOgr);
        System.out.println("ID ROTA> "+selectedRota.getId());
        LibraryClass.getFirebase().child("Paragens").child(selectedRota.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                paragens = new ArrayList<Ponto>();
                paragens.add(pntOgr);
                System.out.println("There are " + snapshot.getChildrenCount() + " Paragens");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Ponto post = postSnapshot.getValue(Ponto.class);
                    System.out.println(post.getNome() + " - " + post.getId());
                    paragens.add(post);
                    paragensOg.add(post);
                    fixOrigemPontosDest.add(post);
                    fixOrigemPontosOrig.add(post);
                }
                if (lv != null && !paragens.isEmpty()) {
                    lv.setAdapter(new ArrayAdapter<Ponto>(ChooseItinerarioActivity.this, android.R.layout.simple_list_item_1, paragensOg));
                    fixOrigemPontosDest.remove(1);
                    fixOrigemPontosOrig.remove(fixOrigemPontosOrig.size()-1);
                    sporigem.setAdapter(new ArrayAdapter<Ponto>(ChooseItinerarioActivity.this, android.R.layout.simple_list_item_1, fixOrigemPontosOrig));
                    spdestino.setAdapter(new ArrayAdapter<Ponto>(ChooseItinerarioActivity.this, android.R.layout.simple_list_item_1, fixOrigemPontosDest));
                    progressDialog.dismiss();
                }
                else
                {
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        sporigem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selecionouOrigem = false;
                selecionouDestino = false;
                if (firstTime == true) {
                    firstTime = false;
                } else {
                    origem = (Ponto) sporigem.getSelectedItem();
                    selecionouOrigem =true;
                    Double distPointMax1 = CalculationByDistance(paragensOg.get(0).getLatlng(), origem.getLatlng());
                    ArrayList<Ponto> filtredListaParagens1 = new ArrayList<>();
                    filtredListaParagens1.add(pntOgr);
                    for (Ponto pont : paragensOg) {
                        Double refParagDst = CalculationByDistance(paragensOg.get(0).getLatlng(), pont.getLatlng());
                        if (refParagDst > distPointMax1) {
                            filtredListaParagens1.add(pont);
                        }
                    }
                    if (filtredListaParagens1.size()<=1) {
                        progressBar = (ProgressBar) findViewById(R.id.chooseProgress);
                        snackbar=Snackbar.make(progressBar,"Escolha um ponto de origem que não seja o último da lista",Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                        snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                        snackbar.show();
                    } else {
                        paragens = filtredListaParagens1;
                        sporigem.setEnabled(false);
                        spdestino.setEnabled(true);
                        firstTime = true;
                        spdestino.setSelection(1, false);
                        spdestino.setAdapter(new ArrayAdapter<Ponto>(ChooseItinerarioActivity.this, android.R.layout.simple_list_item_1, paragens));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        spdestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selecionouDestino = false;
                if (firstTime == true) {
                    firstTime =false;
                } else {
                    destino = (Ponto) spdestino.getSelectedItem();
                    selecionouDestino =true;
                    spdestino.setEnabled(false);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);
        return Radius * c;
    }
    public void resetChoose(View view)
    {
        firstTime = true;
        paragens=paragensOg;
        sporigem.setEnabled(true);
        spdestino.setEnabled(false);
        spdestino.setAdapter(new ArrayAdapter<Ponto>(ChooseItinerarioActivity.this, android.R.layout.simple_list_item_1, fixOrigemPontosDest));
        sporigem.setAdapter(new ArrayAdapter<Ponto>(ChooseItinerarioActivity.this, android.R.layout.simple_list_item_1, fixOrigemPontosOrig));
    }
    public void callMapWithRoute(View view)
    {
        Ponto or= (Ponto) sporigem.getSelectedItem();
        Ponto ds =(Ponto) spdestino.getSelectedItem();
        if(or.getNome().equalsIgnoreCase(pntOgr.getNome()) && ds.getNome().equalsIgnoreCase(pntOgr.getNome()))
        {
            progressBar = (ProgressBar) findViewById(R.id.chooseProgress);
            snackbar=Snackbar.make(progressBar,"Por favor, especifique um ponto de origem e destino",Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
            snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
            snackbar.show();
        }
       else if(or.getNome().equalsIgnoreCase(pntOgr.getNome()))
        {
            progressBar = (ProgressBar) findViewById(R.id.chooseProgress);
            snackbar=Snackbar.make(progressBar,"Por favor, especifique um ponto de origem",Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
            snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
            snackbar.show();
        }
        else if(ds.getNome().equalsIgnoreCase(pntOgr.getNome()))
        {
            progressBar = (ProgressBar) findViewById(R.id.chooseProgress);
            snackbar=Snackbar.make(progressBar,"Por favor, especifique um ponto de destino",Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
            snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
            snackbar.show();
        }
        else{
        Intent i = new Intent(ChooseItinerarioActivity.this, MapsActivity.class);
        selectedRota.setLat_origem(origem.getLatitude());
        selectedRota.setLong_origem(origem.getLongitude());
        selectedRota.setLat_destino(destino.getLatitude());
        selectedRota.setLong_destino(destino.getLongitude());
        selectedItinerario.setId(ut.getId() + selectedRota.getId());
        selectedItinerario.setCod_rota(selectedRota.getId());
        Historico.Itinerario.Ponto porigem = new Historico.Itinerario.Ponto();
        Historico.Itinerario.Ponto pdestino = new Historico.Itinerario.Ponto();
        porigem.setId(origem.getId());
        porigem.setDescricao(origem.getDescricao());
        porigem.setLatitude(origem.getLatitude());
        porigem.setLongitude(origem.getLongitude());
        porigem.setNome(origem.getNome());

        pdestino.setId(destino.getId());
        pdestino.setDescricao(destino.getDescricao());
        pdestino.setLatitude(destino.getLatitude());
        pdestino.setLongitude(destino.getLongitude());
        pdestino.setNome(destino.getNome());

        selectedItinerario.setPontoOrigem(porigem);
        selectedItinerario.setPontoDestino(pdestino);

        historico = new Historico();
        Date finaldate = new Date();
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyyhhmmss");
        final String date = DATE_FORMAT.format(finaldate);
        DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        final String datenc = DATE_FORMAT.format(finaldate);
        historico.setId(ut.getId() + date);
        historico.setCod_rota(selectedRota.getId());
        historico.setCod_utilizador(ut.getId());
        historico.setItinerario(selectedItinerario);
        historico.setData(datenc);
        historico.setDataUltima(datenc);
        final ArrayList<Historico> list = new ArrayList<>();
        LibraryClass.getFirebase().child("Historicos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are" + snapshot.getChildrenCount() + " Historicos");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (postSnapshot.getValue(Historico.class) != null) {
                        Historico post = postSnapshot.getValue(Historico.class);
                        if (post.getId().contains(ut.getId())) {
                            list.add(post);
                        }
                    }
                }
                boolean encontrou = false;
                for (Historico h : list) {
                    System.out.println("Itinerario" + h.itinerario.getId());
                    if (h.getItinerario().getPontoOrigem().getLatitude().equals(selectedItinerario.getPontoOrigem().getLatitude()) &&
                            h.getItinerario().getPontoOrigem().getLongitude().equals(selectedItinerario.getPontoOrigem().getLongitude()) &&
                            h.getItinerario().getPontoDestino().getLatitude().equals(selectedItinerario.getPontoDestino().getLatitude()) &&
                            h.getItinerario().getPontoDestino().getLongitude().equals(selectedItinerario.getPontoDestino().getLongitude())) {
                        encontrou = true;
                        historico.setDataUltima(datenc);
                        Map<String, Object> newDate = new HashMap<String, Object>();
                        newDate.put("dataUltima", datenc);
                        LibraryClass.getFirebase().child("Historicos").child(h.getId()).updateChildren(newDate);
                    }
                }
                if (encontrou == false) {
                    LibraryClass.getFirebase().child("Historicos").child(ut.getId() + date).setValue(historico);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("A busca por dados falhou: " + firebaseError.getMessage());
            }
        });

        i.putExtra("RotaSelecionada", selectedRota);
        i.putExtra("selectedItinerario", selectedItinerario);
        i.putExtra("utilizador", ut);
        startActivity(i);
    }
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ChooseItinerario Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mz.maputobustracker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ChooseItinerario Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://mz.maputobustracker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
    public class MySpinner extends Spinner {
        OnItemSelectedListener listener;

        public MySpinner(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void setSelection(int position) {
            super.setSelection(position);
            if (listener != null)
                listener.onItemSelected(null, null, position, 0);
        }

        public void setOnItemSelectedEvenIfUnchangedListener(
                OnItemSelectedListener listener) {
            this.listener = listener;
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
