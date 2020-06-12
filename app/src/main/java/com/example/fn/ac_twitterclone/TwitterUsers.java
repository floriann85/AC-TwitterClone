package com.example.fn.ac_twitterclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class TwitterUsers extends AppCompatActivity implements AdapterView.OnItemClickListener {
    // Ui Components
    // globale ListView anlegen
    private ListView listView;
    // globale ArrayList anlegen
    private ArrayList<String> tUsers;
    // globalen ArrayAdapter anlegen
    private ArrayAdapter adapter;
    // globalen String anlegen
    private String followedUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        // den Title für die Activity setzen
        setTitle("Twitter - Users");

        // Begrüßung angemeldeter User
        FancyToast.makeText(this, "Welcome " +
                        ParseUser.getCurrentUser().getUsername(),
                Toast.LENGTH_LONG, FancyToast.INFO, true).show();

        // initialisieren
        listView = findViewById(R.id.listView);

        // anlegen und initialisieren
        tUsers = new ArrayList<>();
        adapter = new ArrayAdapter(TwitterUsers.this, android.R.layout.simple_list_item_checked, tUsers);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE); // MULTIPLE Optionsfelder
        // OnItemClickListener erstellen für die Funktion
        listView.setOnItemClickListener(this);

        try {
            // bestehenden User von der DB auf dem Server abfragen
            // ParseObjekt erstellen
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                // Methode anlegen
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {

                        for (ParseUser twitterUser : objects) {
                            // User hinzufügen
                            tUsers.add(twitterUser.getUsername());
                        }
                        // die ListView zu dem Adapter setzen
                        listView.setAdapter(adapter);

                        for (String twitterUser : tUsers) {
                            // Abfrage ob die Liste nicht leer ist, damit die App nicht abstürzt
                            if (ParseUser.getCurrentUser().getList("fanOf") != null) {
                                // Abfrage ob der User anderen Usern folgt
                                if (ParseUser.getCurrentUser().getList("fanOf").contains(twitterUser)) {

                                    // der StringVar die folgenden User hinzufügen
                                    followedUser = followedUser + twitterUser + "\n";

                                    // die Liste aktualisieren
                                    listView.setItemChecked(tUsers.indexOf(twitterUser), true);

                                    // Informationen welchen Usern gefolgt wird
                                    FancyToast.makeText(TwitterUsers.this,
                                            ParseUser.getCurrentUser().getUsername() +
                                                    " is following: " + followedUser, FancyToast.LENGTH_LONG,
                                            FancyToast.INFO, true).show();
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    // Methode anlegen für Funktion Menu
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    // Methode anlegen für Funktion Menu
    public boolean onOptionsItemSelected(MenuItem item) {
        // Switch-Case für Auswahl/ Funktion Button
        switch (item.getItemId()) {
            // User ausloggen
            case R.id.logout_item:
                // der angemeldete User wird ausgeloggt
                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Intent anlegen mit Zuordnung der Klasse für Activity wechseln
                        Intent intent = new Intent(TwitterUsers.this, MainActivity.class);
                        // die Activity starten
                        startActivity(intent);
                        finish();
                    }
                });

                break;
            // Tweet senden
            case R.id.sendTweetItem:
                // Intent anlegen mit Zuordnung der Klasse für Activity wechseln
                Intent intent = new Intent(TwitterUsers.this, SendTweetActivity.class);
                // die Activity starten
                startActivity(intent);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent
            , View view, final int position, long id) {

        // lokale CheckedTextView anlegen
        CheckedTextView checkedTextView = (CheckedTextView) view;

        // Abfrage ob die CheckedTextView angeklickt ist
        if (checkedTextView.isChecked()) {
            // dem User wird gefolgt
            FancyToast.makeText(TwitterUsers.this,
                    tUsers.get(position) + " is now followed!",
                    Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
            // dem gefolgten User in der Liste mit Verknüpfung anzeigen
            ParseUser.getCurrentUser().add("fanOf", tUsers.get(position));
        } else {
            // dem User wird nicht gefolgt
            FancyToast.makeText(TwitterUsers.this,
                    tUsers.get(position) + " is now unfollowed!",
                    Toast.LENGTH_SHORT, FancyToast.INFO, true).show();

        }

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // Abfrage ob kein Error
                if (e == null) {
                    FancyToast.makeText(TwitterUsers.this,
                            "Saved", Toast.LENGTH_SHORT,
                            FancyToast.SUCCESS, true).show();

                    // dem nicht mehr gefolgten User von der Verknüpfung der liste entfernen
                    ParseUser.getCurrentUser().getList("fanOf").remove(tUsers.get(position));
                    // ein Objekt anlegen mit Verweis auf die fanOf-Liste
                    List currentUserFanOfList = ParseUser.getCurrentUser().getList("fanOf");
                    ParseUser.getCurrentUser().remove("fanOf");
                    // dem User der Liste hinzufügen
                    ParseUser.getCurrentUser().put("fanOf", currentUserFanOfList);
                }
            }
        });
    }
}
