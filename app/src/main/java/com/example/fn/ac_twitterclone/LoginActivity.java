package com.example.fn.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    // Ui Components
    // globalen Button anlegen
    private Button btnLoginActivity, btnSignUpLoginActivity;
    // globalen EditText anlegen
    private EditText edtLoginEmail, edtLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // den Title für die Activity setzen
        setTitle("Twitter - Log In");

        // initialisieren
        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);

        btnLoginActivity = findViewById(R.id.btnLoginActivity);
        btnSignUpLoginActivity = findViewById(R.id.btnSignUpLoginActivity);

        // OnClickListener erstellen mit Verweis implents der Klasse
        btnLoginActivity.setOnClickListener(this);
        btnSignUpLoginActivity.setOnClickListener(this);

        // Abfrage ob ein User eingeloggt ist
        if (ParseUser.getCurrentUser() != null) {
            // den bereits angemeldeten User ausloggen
            ParseUser.getCurrentUser().logOut();
        }
    }

    @Override
    public void onClick(View view) {
        // Switch-Case für Auswahl/ Funktion Button
        switch (view.getId()) {

            case R.id.btnLoginActivity:
                // bestehenden User von der DB auf dem Server abfragen
                // ParseObjekt erstellen
                ParseUser.logInInBackground(edtLoginEmail.getText().toString(),
                        edtLoginPassword.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                // Abfrage ob kein Error
                                if (user != null && e == null) {

                                    FancyToast.makeText(LoginActivity.this,
                                            user.getUsername() + " is Logged in successfully",
                                            Toast.LENGTH_SHORT, FancyToast.SUCCESS,
                                            true).show();
                                    transitionToSocialMediaActivity();
                                }
                            }
                        });

                break;

            case R.id.btnSignUpLoginActivity:
                // Intent anlegen mit Zuordnung der Klasse für Activity wechseln
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                // die Activity starten
                startActivity(intent);

                break;
        }
    }

    // Methode erstellen für die Funktion aufrufen SocialMediaActivity
    private void transitionToSocialMediaActivity() {
        // lokalen Intent anlegen mit Zuordnung der Klasse für Activity wechseln
        Intent intent = new Intent(LoginActivity.this, TwitterUsers.class);
        // die Activity starten
        startActivity(intent);
        finish();
    }
}

