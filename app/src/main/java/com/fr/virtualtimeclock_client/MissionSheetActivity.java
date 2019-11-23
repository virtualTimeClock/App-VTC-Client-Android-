package com.fr.virtualtimeclock_client;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MissionSheetActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView titre_mission;
    private TextView lieu_mission;
    private TextView description_mission;
    private String   identifiant_mission;

    private Button report_button;

    private CircleImageView pointing_image;

    private MediaPlayer off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_sheet);

        //Change la fleche retour par une croix et le nom
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        //setTitle(getString(R.string.addEmployee));
        setTitle("Fiche Mission");

        this.titre_mission       = findViewById(R.id.titre_mission);
        this.lieu_mission        = findViewById(R.id.lieu_mission);
        this.description_mission = findViewById(R.id.description_mission);
        this.report_button       = findViewById(R.id.report_button);
        this.pointing_image      = findViewById(R.id.pointing_image);

        // Je réccupère l'intent concerné
        Intent intent = getIntent();

        if(intent != null){
            if (intent.hasExtra("Titre")){
                String text = intent.getExtras().getString("Titre");
                 text = textMultiline(text);
                titre_mission.setText(text);
            }
            if (intent.hasExtra("Lieu")){
                String text = intent.getExtras().getString("Lieu");
                lieu_mission.setText(text);
            }
            if (intent.hasExtra("Description")){
                String text = intent.getExtras().getString("Description");
                description_mission.setText(text);
            }
            if (intent.hasExtra("Identifiant_mission")){
                String text = intent.getExtras().getString("Identifiant_mission");
                identifiant_mission = text;
            }
        }

        pointing_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MissionSheetActivity.this, "Clique", Toast.LENGTH_SHORT).show();
            }
        });

        off = MediaPlayer.create(this, R.raw.sound_off);
    }

    public String textMultiline(String line){
        System.out.println("Entrée");
        if(line.length() <= 26)return line;
        else {
            int taille = 26;
            int nombreDecoupe = Math.round(line.length()/26);
            if(line.length()%26 > 0) nombreDecoupe += 1;
            String line_space = "";

            for (int i  = 1 ; i <= nombreDecoupe; i++){
                if((line.substring(taille-26)).length() > 26 ) {
                    String decoupe = line.substring(taille - 26, taille);
                    decoupe += '\n';
                    line_space += decoupe;
                    taille += 26;
                    System.out.println(taille);
                } else{
                    String decoupe = line.substring(taille - 26);
                    line_space += decoupe;
                }
            }
            return line_space;
        }
    }

    public void mediaPlayer(MediaPlayer m){
        m.start();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.report_button) {
            //Toast.makeText(MissionSheetActivity.this, "Clique", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MissionSheetActivity.this, RepportActivity.class);
            intent.putExtra("Identifiant_mission", identifiant_mission);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mediaPlayer(off);
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}