package mz.maputobustracker;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import mz.maputobustracker.domain.Utente;


public class UpdateActivity extends AppCompatActivity implements ValueEventListener, DatabaseReference.CompletionListener {

    private Toolbar toolbar;
    private Utente utente;
    private AutoCompleteTextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle( getResources().getString(R.string.update_profile) );
        name = (AutoCompleteTextView) findViewById(R.id.name);
        utente = new Utente();
        utente.setId( FirebaseAuth.getInstance().getCurrentUser().getUid() );
        utente.contextDataDB( this );
    }

    public void update( View view ){
        utente.setId(FirebaseAuth.getInstance().getCurrentUser().getUid() );
        utente.setName( name.getText().toString() );
        utente.updateDB( UpdateActivity.this );
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Utente u = dataSnapshot.getValue( Utente.class );
        name.setText( u.getName() );
    }

    @Override
    public void onCancelled(DatabaseError firebaseError) {
        FirebaseCrash.report( firebaseError.toException() );
    }

    @Override
    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {

        if( firebaseError != null ){
            FirebaseCrash.report( firebaseError.toException() );
            Toast.makeText( this, "Falhou: "+firebaseError.getMessage(), Toast.LENGTH_LONG ).show();
        }
        else{
            Toast.makeText( this, "Atualização realizada com sucesso.", Toast.LENGTH_SHORT ).show();
        }
    }
}