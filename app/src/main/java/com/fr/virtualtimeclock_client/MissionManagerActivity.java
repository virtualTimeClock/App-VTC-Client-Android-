package com.fr.virtualtimeclock_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Script;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MissionManagerActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("missions");

    private MissionCell cell;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Cette directive permet d'enlever la barre de notifications pour afficher l'application en plein écran
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Hide ActionBar
        getSupportActionBar().setTitle("");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_manager);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query  = notebookRef.orderBy("debut", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Mission> options =  new FirestoreRecyclerOptions.Builder<Mission>()
                .setQuery(query, Mission.class)
                .build();

        cell = new MissionCell(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cell);

        //Détecte le clic sur la mission
        cell.setOnClickListener(new MissionCell.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                //String path = documentSnapshot.getReference().getPath();
                //Toast.makeText(MissionManagerActivity.this, "Position: " + position+ " ID: "+id, Toast.LENGTH_SHORT).show();
                Mission mission = documentSnapshot.toObject(Mission.class);
                Intent MissionSheetActivity = new Intent(MissionManagerActivity.this, MissionSheetActivity.class);
                assert mission != null;
                MissionSheetActivity.putExtra("Identifiant_mission", documentSnapshot.getId());
                MissionSheetActivity.putExtra("Titre", mission.getTitre());
                MissionSheetActivity.putExtra("Lieu", mission.getLieu());
                MissionSheetActivity.putExtra("Description", mission.getDescription());

                startActivity(MissionSheetActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mission_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                userLogout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void userLogout() {
        mAuth.getInstance().signOut();
        Intent intent = new Intent(MissionManagerActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cell.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cell.stopListening();
    }
}