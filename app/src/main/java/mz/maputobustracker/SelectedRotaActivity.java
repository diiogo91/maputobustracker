package mz.maputobustracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import mz.maputobustracker.domain.Rota;
import mz.maputobustracker.domain.Utente;

/**
 * Created by Hawkingg on 26/06/2016.
 */
public class SelectedRotaActivity extends AppCompatActivity {

    private Button bntItinerario;
    private Rota selectedRota;
    private Utente ut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectedrota);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        selectedRota = (Rota) intent.getSerializableExtra("RotaSelecionada");
        ut = (Utente) intent.getSerializableExtra("utilizador");
        System.out.println("-----------------------------"+ut.getId());
        System.out.println("ID"+ut.getId());
        System.out.println("Selected Rota: "+selectedRota.getName());
    }

    public void callItinerario(View view)
    {
        Intent i = new Intent(SelectedRotaActivity.this, ChooseItinerarioActivity.class);
        i.putExtra("RotaSelecionada",selectedRota);
        i.putExtra("utilizador",ut);
        startActivity(i);
    }

    public void callNearestPoint(View view)
    {
        Intent i = new Intent(SelectedRotaActivity.this, MapsActivity.class);
        i.putExtra("RotaSelecionada",selectedRota);
        i.putExtra("isPontoProximo",true);
        i.putExtra("utilizador",ut);
        startActivity(i);
    }

    public void callHistorico(View view)
    {
        Intent i = new Intent(SelectedRotaActivity.this, HistoricoActivity.class);
        i.putExtra("RotaSelecionada",selectedRota);
        i.putExtra("utilizador",ut);
        startActivity(i);
    }
    public void callViagens(View view)
    {
        Intent i = new Intent(SelectedRotaActivity.this, viagensInfoActivity.class);
        i.putExtra("RotaSelecionada",selectedRota);
        i.putExtra("utilizador",ut);
        startActivity(i);
    }
    
    }
