package mz.maputobustracker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

import mz.maputobustracker.adapter.UserRecyclerAdapter;
import mz.maputobustracker.domain.Ponto;
import mz.maputobustracker.domain.Utente;
import mz.maputobustracker.domain.util.LibraryClass;


public class MainActivity extends AppCompatActivity implements ValueEventListener{

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private UserRecyclerAdapter adapter;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Utente ut;
    private Utente u;
    private Intent i;
    private Intent map;
    private Button btnRota;
    private Button btnMaps;
    private TextView txtNomeUt;
    private ArrayList<Ponto> listaParagens;
    private ProgressBar progress;
    private ProgressDialog progressDialog;
    protected ProgressBar progressBar;
    private Snackbar snackbar;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE ,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        txtNomeUt = (TextView) findViewById(R.id.txtNomeUt);
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(authStateListener);
        databaseReference = LibraryClass.getFirebase();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando Maputo Bus Tracker, Por favor aguarde...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressBar = (ProgressBar) findViewById(R.id.progressMain);

        btnRota = (Button) findViewById(R.id.btnRota);
        btnMaps = (Button) findViewById(R.id.btnMaps);
        btnMaps.setEnabled(false);
        btnRota.setEnabled(false);
        progressDialog.setOnDismissListener(new ProgressDialog.OnDismissListener(){
            @Override
            public void onDismiss(DialogInterface dialog) {
                permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[5]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[6]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[7]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[8]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[9]) != PackageManager.PERMISSION_GRANTED

                        ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[1])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[2])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[3])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[4])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[5])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[6])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[7])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[8])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[9])) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Aplicação necessita de Permissões");
                        builder.setMessage("Esta aplicação necessita de multiplas permissões para o seu normal funcionamento");
                        builder.setPositiveButton("Garantir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                snackbar= Snackbar.make(progressBar, "Para usufruir das funcionalidades da aplicação queira por favor atribuir permissões a mesma.", Snackbar.LENGTH_INDEFINITE);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                                snackbar.show();
                            }
                        });
                        builder.show();
                    } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                        // Redirect to Settings after showing Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Aplicação necessita de Permissões");
                        builder.setMessage("Esta aplicação necessita de multiplas permissões para o seu normal funcionamento");
                        builder.setPositiveButton("Garantir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sentToSettings = true;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                Toast.makeText(getBaseContext(), "Garanta as permissões a aplicação nas configurações do seu Smartphone", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                snackbar= Snackbar.make(progressBar, "Para usufruir das funcionalidades da aplicação queira por favor atribuir permissões a mesma.", Snackbar.LENGTH_INDEFINITE);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                                snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                                snackbar.show();
                            }
                        });
                        builder.show();
                    } else {
                        //just request the permission
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }

                    Toast.makeText(getBaseContext(), "Permissões Requeridas", Toast.LENGTH_LONG).show();

                    SharedPreferences.Editor editor = permissionStatus.edit();
                    editor.putBoolean(permissionsRequired[0], true);
                    editor.commit();
                } else {
                    //You already have the permission, just go ahead.
                    btnMaps.setEnabled(true);
                    btnRota.setEnabled(true);
                }
            }
    });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT){
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                proceedAfterPermission();
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[4])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[5])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[6])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[7])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[8])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,permissionsRequired[9])){
                Toast.makeText(getBaseContext(),"Permissões Requeridas",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Aplicação necessita de Permissões");
                builder.setMessage("Esta aplicação necessita de multiplas permissões para o seu normal funcionamento");
                builder.setPositiveButton("Garantir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        snackbar= Snackbar.make(progressBar, "Para usufruir das funcionalidades da aplicação queira por favor atribuir permissões a mesma.", Snackbar.LENGTH_INDEFINITE);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.parseColor("#f44336")); // snackbar background color
                        snackbar.setActionTextColor(Color.parseColor("#FFFFEE19")); // snackbar action text color
                        snackbar.show();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(),"Impossivel obter permissões",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        Toast.makeText(getBaseContext(), "Todas as permissões foram garantidas", Toast.LENGTH_LONG).show();
        btnMaps.setEnabled(true);
        btnRota.setEnabled(true);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
    private void init(){
        FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        ut = new Utente();
        ut.setId(userFirebase.getUid());
        ut.contextDataDB( this );
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( authStateListener != null ){
            mAuth.removeAuthStateListener( authStateListener );
        }
    }
public void callRota(View view)
{
startActivity(i);
}
    public void callMaps(View view)
    {
        map = new Intent(MainActivity.this,MapsActivity.class);
        map.putExtra("utilizador",u);
        map.putExtra("vemdoMenuPrincipal",true);
        startActivity(map);
    }
    public void callAcerca(View view)
    {
        map = new Intent(MainActivity.this,acercaActivity.class);
        map.putExtra("utilizador",u);
        map.putExtra("vemdoMenuPrincipal",true);
        startActivity(map);
    }

    // MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Utente utente = new Utente();

        if( utente.isSocialNetworkLogged( this ) ){
            getMenuInflater().inflate(R.menu.menu_social_network_logged, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_update){
            startActivity(new Intent(this, UpdateActivity.class));
        }
        else if(id == R.id.action_update_login){
            startActivity(new Intent(this, UpdateLoginActivity.class));
        }
        else if(id == R.id.action_update_password){
            startActivity(new Intent(this, UpdatePasswordActivity.class));
        }
        else if(id == R.id.action_link_accounts){
            startActivity(new Intent(this, LinkAccountsActivity.class));
        }
        else if(id == R.id.action_remove_user){
            startActivity(new Intent(this, RemoveUserActivity.class));
        }
        else if(id == R.id.action_details_user){
            startActivity(new Intent(this, AccountDetailsActivity.class));
        }

        else if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        u = dataSnapshot.getValue( Utente.class );
        txtNomeUt.setText( u.getName());
        progressDialog.dismiss();
        i = new Intent(MainActivity.this,ChooseRotaActivity.class);
        i.putExtra("utilizador",u);
        System.out.println("-----------------------------"+u.getId());
        System.out.println("ID"+u.getId());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}