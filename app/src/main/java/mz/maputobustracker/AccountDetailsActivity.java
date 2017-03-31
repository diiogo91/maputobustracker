package mz.maputobustracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

import mz.maputobustracker.domain.Utente;


/**
 * Created by Hawkingg on 26/06/2016.
 */
public class AccountDetailsActivity extends CommonActivity implements ValueEventListener {

    private TextView txtNome;
    private TextView txtEmail;
    private TextView txtID;
    private Utente ut;
    private ProgressDialog progressDialog;
    private Uri mCropImageUri;
    private Uri imageUri;
    private ImageView fotoUtilizador;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private double progress =0;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acountsdetails);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(toolbar);
        txtNome = (TextView) findViewById(R.id.txtNomeAc);
        txtEmail= (TextView) findViewById(R.id.txtEmailAc);
        fotoUtilizador = (ImageView) findViewById(R.id.fotoUtilizador);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processando Informação, Por favor aguarde...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        progressDialog.setCancelable(false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://bustracker-c0589.appspot.com");
        initViews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        ut = new Utente();
        ut.setId( userFirebase.getUid());
        ut.contextDataDB( this );
    }
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Utente u = dataSnapshot.getValue( Utente.class );
        txtNome.setText( u.getName());
        txtEmail.setText(u.getEmail());
        downloadPhoto();
    }

    @SuppressLint("NewApi")
    public void onSelectImageClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(this);
        }
    }
    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
               // startCropImageActivity(imageUri);
            }
            if (!requirePermissions) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(180,180)
                        .start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uploadPhoto(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public  void downloadPhoto()
    {

        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageReference.child("FotosPerfil").child(ut.getId() + ".jpg").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    fotoUtilizador.setImageBitmap(bitmap);
                    progressDialog.hide();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.hide();
                }
            });
        } catch (IOException e ) {}


    }
    public  void uploadPhoto(final Uri resultUri)
    {

        progressDialog.setMessage("Actualizando " + ((int) progress) + "%...");
        progressDialog.show();
        StorageReference riversRef = storageReference.child("FotosPerfil").child(ut.getId() + ".jpg");
        riversRef.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //if the upload is successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();
                        fotoUtilizador.setImageURI(resultUri);
                        //and displaying a success toast
                        showSnackbar("Foto Actualizada com Sucesso ");
                       // Toast.makeText(getApplicationContext(), "Foto Actualizada com Sucesso ", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //if the upload is not successfull
                        //hiding the progress dialog
                        progressDialog.dismiss();

                        //and displaying error message
                        showSnackbar("Infelizmente não foi possivel actualizar a sua foto de perfil");
                        fotoUtilizador.setImageResource(R.mipmap.rsz_user);
                        //Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //calculating progress percentage
                        progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //displaying percentage in progress dialog
                         Runnable changeText = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage("Actualizando " + ((int) progress) + "%...");
                            }
                        };
                        Log.e("Tuts+", "Bytes uploaded: " + taskSnapshot.getBytesTransferred());
                    }
                });
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                showSnackbar("Cancelando, permissões não foram concedidas");
                //Toast.makeText(this, "Cancelando, permissões não foram concedidas", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
                }
            } else {
               showSnackbar("Cancelando, permissões não foram concedidas");
            //Toast.makeText(this, "Cancelando, permissões não foram concedidas", Toast.LENGTH_LONG).show();
            }
        }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    protected void initViews(){

        progressBar = (ProgressBar) findViewById(R.id.acount_progressbar);
    }
    @Override
    protected void initUser() {

    }
}
