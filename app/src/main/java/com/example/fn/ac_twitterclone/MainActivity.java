package com.example.fn.ac_twitterclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Ui Components
    // globalen EditText anlegen
    private EditText edtEmail, edtUsername, edtPassword;

    // globalen Button anlegen
    private Button btnSignUp, btnLogIn;

    // globale LongVar anlegen
    private long backPressedTime;
    // globale ToastVar anlegen
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // den Title für die Activity setzen
        setTitle("Welcome to Twitter!");

        // Information einer Installation durch einen User,
        // diese wird auf dem Server/ Backend gespeichert
        ParseInstallation.getCurrentInstallation().saveInBackground();

        // initialisieren
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogIn);
        btnSignUp.setOnClickListener(this);
        btnLogIn.setOnClickListener(this);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEnterEmail);
        edtPassword = findViewById(R.id.edtEnterPassword);

        // OnKeyListener erstellen für die Funktion SignUp-Button oder Enter gedrückt
        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                // überprüfen ob der User den SignUp-Button oder Enter gedrückt hat
                if (keyCode == KeyEvent.KEYCODE_ENTER &&
                        event.getAction() == KeyEvent.ACTION_DOWN) {

                    // Methode aufrufen
                    onClick(btnSignUp);
                }
                return false;
            }
        });

        // Abfrage ob ein User eingeloggt ist
        if (ParseUser.getCurrentUser() != null) {
            //ParseUser.getCurrentUser().logOut();

            // Methode aufrufen
            transitionToSocialMediaActivity();
        }
    }

    // Methode anlegen für Funktion Verbindung SocialMediaActivity
    private void transitionToSocialMediaActivity() {
        // Intent anlegen mit Zuordnung der Klasse für Activity wechseln
        Intent intent = new Intent(MainActivity.this,
                TwitterUsers.class);
        // die Activity starten
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        // Switch-Case für Auswahl/ Funktion Button
        switch (view.getId()) {
            case R.id.btnSignUp:
                // Abfrage ob die Werte bei der Registrierung gefüllt sind
                if (edtEmail.getText().toString().equals("") ||
                        edtUsername.getText().toString().equals("") ||
                        edtPassword.getText().toString().equals("")) {

                    FancyToast.makeText(MainActivity.this,
                            "Email, Username, Password is required!",
                            Toast.LENGTH_SHORT, FancyToast.INFO,
                            true).show();

                } else {
                    // neuen User der DB auf dem Server hinzufügen
                    // ParseObjekt erstellen
                    final ParseUser appUser = new ParseUser();
                    appUser.setEmail(edtEmail.getText().toString());
                    appUser.setUsername(edtUsername.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());

                    // ProgressDialog anlegen und anzeigen
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Signing up " + edtUsername.getText().toString());
                    progressDialog.show();

                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            // Abfrage ob kein Error
                            if (e == null) {
                                FancyToast.makeText(MainActivity.this,
                                        appUser.getUsername() + " is signed up",
                                        Toast.LENGTH_SHORT, FancyToast.SUCCESS,
                                        true).show();

                                // Methode aufrufen
                                transitionToSocialMediaActivity();
                            } else {

                                FancyToast.makeText(MainActivity.this,
                                        "There was an error: " + e.getMessage(),
                                        Toast.LENGTH_LONG, FancyToast.ERROR,
                                        true).show();
                            }

                            // progressDialog nachdem User anlegen (Sign up) schließen
                            progressDialog.dismiss();
                        }
                    });
                }
                break;

            case R.id.btnLogIn:
                // Intent anlegen mit Zuordnung der Klasse für Activity wechseln
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                // die Activity starten
                startActivity(intent);
                break;
        }
    }

    // Methode erstellen für Funktion wenn User in dem leeren Bildschirmbereich drückt
    // damit die App nicht abstürzt
    public void rootLayoutTapped(View view) {
        try {
            // lokalen InputMethodManager erstellen
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    // Methoden zum Beenden der App mit der Rücktaste
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            // Toast anlegen für Informationsausgabe Display und anzeigen
            backToast = Toast.makeText(getBaseContext(), "Tap again to quit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
