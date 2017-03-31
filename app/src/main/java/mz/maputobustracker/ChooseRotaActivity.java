package mz.maputobustracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mz.maputobustracker.domain.Rota;
import mz.maputobustracker.domain.Utente;
import mz.maputobustracker.domain.util.LibraryClass;

/**
 * Created by Hawkingg on 13/07/2016.
 */
public class ChooseRotaActivity extends AppCompatActivity {


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ArrayList<Rota> rotas;
    private ProgressDialog progressDialog;
    private Utente ut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choserota);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final ListView lv = (ListView) findViewById(R.id.lstRotas);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Buscando Rotas disponiveis, Por favor aguarde...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        Intent intent = getIntent();
        ut = (Utente) intent.getSerializableExtra("utilizador");

        LibraryClass.getFirebase().child("Rotas").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot snapshot) {
                rotas = new ArrayList<Rota>();
                System.out.println("There are " + snapshot.getChildrenCount() + " Rotas");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Rota post = postSnapshot.getValue(Rota.class);
                    System.out.println(post.getName() + " - " + post.getDistancia());
                    rotas.add(post);
                }
                if (lv != null) {
                    lv.setAdapter(new ArrayAdapter<Rota>(ChooseRotaActivity.this, android.R.layout.simple_list_item_1, rotas));
                }
                AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        setTitle(parent.getItemAtPosition(position).toString());
                        Rota selectedRota = (Rota) parent.getItemAtPosition(position);
                        Intent i = new Intent(ChooseRotaActivity.this, SelectedRotaActivity.class);
                        i.putExtra("RotaSelecionada",selectedRota);
                        i.putExtra("utilizador",ut);
                        progressDialog.cancel();
                        startActivity(i);
                        ChooseRotaActivity.this.finish();
                    }
                };
                lv.setOnItemClickListener(listener);
                progressDialog.hide();
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    }
