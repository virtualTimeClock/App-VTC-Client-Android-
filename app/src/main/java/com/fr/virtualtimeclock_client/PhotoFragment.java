package com.fr.virtualtimeclock_client;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fr.virtualtimeclock_client.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    }
}
