package com.fr.virtualtimeclock_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.GeoPoint;

import java.util.jar.Manifest;

import static java.util.jar.Manifest.*;

public class MissionSheetActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView titre_mission;
    private TextView lieu_mission;
    private TextView description_mission;

    private Double latitude;
    private Double longitude;
    private int    rayon;

    private String   identifiant_mission;

    private Button report_button;

    private CircleImageView pointing_image;

    private Mission mission;

    private MediaPlayer off;

    //ALERTE
    protected LocationManager locationManager;
    protected ProximityBroadcastReceiver proximityBroadcastReceiver;
    protected static final String PROXIMITY_ALERT_NAME = "com.gmaillot.position.ProximityAlert";
    protected PendingIntent pendingIntent;
    protected IntentFilter intentFilter;
    protected LocationListener locationListener;
    protected String provider = LocationManager.GPS_PROVIDER;

    protected static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    protected final long EXPIRATION = -1;

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
                identifiant_mission  = intent.getExtras().getString("Identifiant_mission");

            }
            if (intent.hasExtra("Latitude")){
                latitude = intent.getExtras().getDouble("Latitude");
            }
            if (intent.hasExtra("Longitude")){
                latitude = intent.getExtras().getDouble("Longitude");
            }
            if (intent.hasExtra("Rayon")){
                rayon = intent.getExtras().getInt("Rayon");
            }
        }

        pointing_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        off = MediaPlayer.create(this, R.raw.sound_off);

        //ALERTE

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        proximityBroadcastReceiver = new ProximityBroadcastReceiver();

        Intent intent1 = new Intent(PROXIMITY_ALERT_NAME);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        //On prépare la réception des broadcast ( pour l'alerte de proximité ici)
        intentFilter = new IntentFilter(PROXIMITY_ALERT_NAME);
        registerReceiver(proximityBroadcastReceiver, intentFilter);

        if( locationManager != null ){
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }

        ///region DEMANDE DE PERMISSION
        //On check si on a la permission de géolocaliser
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            // => On a pas la permission, donc on la demande
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            // MY_PERMISSIONS_REQUEST_LOCATION est une constante entière définie dans l'application.
            // La méthode callback donne résultat de la requête

        } else {
            // => On a la permission

            //On ajoute une alerte de proximité
            setProximityAlert();

            //On met en place la demande de mise à jour des positions
            locationManager.requestLocationUpdates(provider, 1000, 100, locationListener);
        }
        //endregion

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
            Intent intent = new Intent(MissionSheetActivity.this, BottomNavigationActivityRepport.class);
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


    //region MISE EN PLACE DE L'ALERTE DE PROXIMITÉ
    protected void setProximityAlert(){

        try{
            locationManager.addProximityAlert(latitude, longitude, rayon, EXPIRATION, pendingIntent);
        } catch(SecurityException e){}

    }
    //endregion
}