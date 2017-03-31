package mz.maputobustracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import mz.maputobustracker.domain.Utente;


public class RemoveUserActivity extends AppCompatActivity
        implements ValueEventListener, DatabaseReference.CompletionListener {

    private Toolbar toolbar;
    private Utente utente;
    private EditText password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        toolbar.setTitle( getResources().getString(R.string.remove_user) );
        password = (EditText) findViewById(R.id.password);

        utente = new Utente();
        utente.setId( mAuth.getCurrentUser().getUid() );
        utente.contextDataDB( this );
    }

    public void update( View view ){
        utente.setPassword( password.getText().toString() );

        reauthenticate();
    }

    private void reauthenticate(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if( firebaseUser == null ){
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(
                utente.getEmail(),
                utente.getPassword()
        );

        firebaseUser.reauthenticate( credential )
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if( task.isSuccessful() ){
                        deleteUser();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseCrash.report( e );
                    Toast.makeText(
                            RemoveUserActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
    }

    private void deleteUser(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if( firebaseUser == null ){
            return;
        }

        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if( !task.isSuccessful() ){
                    return;
                }

                utente.removeDB( RemoveUserActivity.this );
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseCrash.report( e );
                Toast.makeText(
                        RemoveUserActivity.this,
                        e.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Utente u = dataSnapshot.getValue( Utente.class );
        utente.setEmail( u.getEmail() );
    }

    @Override
    public void onCancelled(DatabaseError firebaseError) {
        FirebaseCrash.report( firebaseError.toException() );
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if( databaseError != null ){
            FirebaseCrash.report( databaseError.toException() );
        }

        Toast.makeText(
                RemoveUserActivity.this,
                "Conta removida com sucesso",
                Toast.LENGTH_SHORT
        ).show();
        finish();
    }
}