package com.fr.virtualtimeclock_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class RepportActivity extends AppCompatActivity {

    private String identifiant_mission;

    //private TextView text_miseAJour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repport);

        //text_miseAJour  = findViewById(R.id.text_miseAJour);

        Calendar rightNow = Calendar.getInstance();
        //System.out.println(rightNow.get(Calendar.DAY_OF_MONTH)+"/"+rightNow.get(Calendar.MONTH+1)+"/"+rightNow.get(Calendar.YEAR));

        String date_rightNow = rightNow.get(Calendar.DAY_OF_MONTH)+"/"+
                (rightNow.get(Calendar.MONTH) + 1) +"/"+rightNow.get(Calendar.YEAR);

        //text_miseAJour.setText("Mise à jour le: " + date_rightNow);

        // Je réccupère l'intent concerné
        Intent intent = getIntent();

        if(intent != null) {
            if (intent.hasExtra("Identifiant_mission")) {
                identifiant_mission = intent.getExtras().getString("Identifiant_mission");
            }
        }

        intent = new Intent(RepportActivity.this, PictureRepportActivity.class);
        intent.putExtra("Identifiant_mission", identifiant_mission);
        startActivity(intent);

    }
}
