package com.fr.virtualtimeclock_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText emailEditText;
    private EditText passwordEditText;

    private Button logInButton;

    private FirebaseAuth mAuth;

    private MediaPlayer on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cette directive permet d'enlever la barre de notifications pour afficher l'application en plein écran
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //Hide ActionBar
        getSupportActionBar().hide();

        //On définit le contenu de la vue APRES les instructions précédentes pour éviter un crash
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        logInButton = findViewById(R.id.logInButton);

        mAuth  = FirebaseAuth.getInstance();

        on = MediaPlayer.create(this, R.raw.sound_on);

    }

    //Fonction exécuter lors de la connexion de l'utilisateur
    public void signIn(String email, String password) {
        Log.d(TAG, "log In: " + email);
        // Début de la connexion via email et mdp
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Connexion réussi, mise à jour des information de l'utilisateur
                            Log.d(TAG, "signInWithEmail : success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent MissionManagerActivityIntent  = new Intent(LoginActivity.this,
                                    MissionManagerActivity.class);
                            startActivity(MissionManagerActivityIntent);

                        } else {
                            // Connexion échouer
                            Log.w(TAG, "signInWithEmail : failure", task.getException());
                        }
                    }
                });
    }

    public void mediaPlayer(MediaPlayer m){
        m.start();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        //Bouton qui envoie l'email et le mdp saisite dans la fonction signIn pour essayer d'établir une connexion
        if (i == R.id.logInButton) {
            mediaPlayer(on);
            signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
        }
    }

}