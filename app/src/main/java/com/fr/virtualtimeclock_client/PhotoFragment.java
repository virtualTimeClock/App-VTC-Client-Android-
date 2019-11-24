package com.fr.virtualtimeclock_client;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fr.virtualtimeclock_client.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int STORAGE_REQUEST_CODE = 2;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";

    private static final String TAG = "imageCreation";

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private String imgURL = "";

    private ImageView pictureTaken;

    private File photoFile;
    private Uri filepath;

    private String identifiant_mission;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_photo , container, false);

        // Je réccupère l'intent concerné
        Intent intent = getIntent();
        if(intent != null) {
            if (intent.hasExtra("Identifiant_mission")) {
                identifiant_mission = intent.getExtras().getString("Identifiant_mission");
            }
        }

        pictureTaken = findViewById(R.id.pictureTaken);
        storage  = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Vérification des permissions pour la caméra et le stockage
        if(Build.VERSION.SDK_INT >= 23) requestPermissions(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);

        loadProfilePicture();
        dispatchPictureTakerAction();

    }

    // Récupère la photo de profile sur la base de données
    // - uri : url de téléchargement de la photo sur la base de données
    private void loadProfilePicture(){
        StorageReference profilePic = storageReference.child("missionsRapportsImages/" + identifiant_mission).getParent().child(identifiant_mission);
        System.out.println("IDENTIFIANT MISSION: " + identifiant_mission);
        profilePic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imgURL = String.valueOf(uri);
                Glide.with(getApplicationContext())
                        .load(imgURL)
                        .into(pictureTaken);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Toast.makeText(PictureRepportActivity.this, getString(R.string.load_img_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Ouverture de l'appareil photo
    private void dispatchPictureTakerAction() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){ // S'assurer qu'il y a une application caméra pour lancer l'Intent
            //Création du fichier ou la photo va être sauvegarder
            photoFile = createPhotoFile();
            if(photoFile != null){
                filepath = FileProvider.getUriForFile(PictureRepportActivity.this,"com.fr.virtualtimeclock_gerant.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, filepath);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    // Création de la photos au format jpg avec comme nom la date ou la photo a été prise
    // Sauvegarde des photos dans le répertoire cache de l'application
    private File createPhotoFile(){
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+ name +"_VTC_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d(TAG,"ImageFile : " + e.toString());
        }
        return image;
    }

    // Envoi de l'image sur la base de données avec un affichage d'une barre de chargement
    private void uploadImage(Uri filepathCrop){
        if(filepathCrop != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference reference = storageReference.child("missionsRapportsImages/"+ identifiant_mission);
            reference.putFile(filepathCrop)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(PictureRepportActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                            loadProfilePicture();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(PictureRepportActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }

    // Récupération de la photo prise pour ensuite éxecuter la fonction startCrop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK  && requestCode == CAMERA_REQUEST_CODE ) {

            if (data != null) {
                filepath = data.getData();
            }
            startCrop(filepath);
        }else {
            Uri filepathCrop = null;
            if (data != null) {
                filepathCrop = UCrop.getOutput(data);
            }
            uploadImage(filepathCrop);
        }
    }

    // Fonction qui redimensionne la photo au format du cadre de la photo de profil
    private void startCrop(@NonNull Uri uri){
        String destinationFileName = SAMPLE_CROPPED_IMG_NAME;
        destinationFileName +=".jpg";

        UCrop uCrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(500,700);
        uCrop.withOptions(getCropOptions());
        uCrop.start(PictureRepportActivity.this);
    }

    // Option nécessaire au redimensionnement
    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(100);

        //UI
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        options.setToolbarTitle("Crop Image");

        return options;
    }


}
