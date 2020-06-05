package com.example.fn.ac_twitterclone;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendTweetActivity extends AppCompatActivity implements View.OnClickListener {
    // Ui Components
    // globalen EditText anlegen
    private EditText edtTweet;

    // globale ListView anlegen
    private ListView viewTweetsListView;

    // globalen Button anlegen
    private Button btnViewTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_tweet);

        // den Title für die Activity setzen
        setTitle("Twitter - Tweets");

        // initialisieren
        edtTweet = findViewById(R.id.edtSendTweet);
        viewTweetsListView = findViewById(R.id.viewTweetsListView);
        btnViewTweets = findViewById(R.id.btnViewTweets);

        // OnKeyListener erstellen für die Funktion
        btnViewTweets.setOnClickListener(this);
    }

    // Methode anlegen für Funktion Tweet senden
    public void sendTweet(View view) {

        // neuen Tweet der DB auf dem Server hinzufügen
        // ParseObjekt erstellen
        ParseObject parseObject = new ParseObject("MyTweet");
        parseObject.put("tweet", edtTweet.getText().toString());
        parseObject.put("user", ParseUser.getCurrentUser().getUsername());

        // ProgressDialog anlegen und anzeigen
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // Abfrage ob kein Error
                if (e == null) {
                    FancyToast.makeText(SendTweetActivity.this,
                            ParseUser.getCurrentUser().getUsername() + "'s tweet" +
                                    "(" + edtTweet.getText().toString() + ")" + " is saved!!!",
                            Toast.LENGTH_LONG, FancyToast.SUCCESS, true).show();

                } else {
                    FancyToast.makeText(SendTweetActivity.this,
                            e.getMessage(), Toast.LENGTH_SHORT,
                            FancyToast.ERROR, true).show();
                }

                // progressDialog nachdem User anlegen (Sign up) schließen
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // ArrayList/ HashMap anlegen
        final ArrayList<HashMap<String, String>> tweetList = new ArrayList<>();
        // SimpleAdapter anlegen
        final SimpleAdapter adapter = new SimpleAdapter(SendTweetActivity.this,
                tweetList, android.R.layout.simple_list_item_2, new String[]{"tweetUserName", "tweetValue"},
                new int[]{android.R.id.text1, android.R.id.text2});
        try {
            // bestehenden Tweet von der DB auf dem Server abfragen
            // ParseObjekt anlegen
            ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("MyTweet");
            parseQuery.whereContainedIn("user", ParseUser.getCurrentUser().getList("fanOf"));
            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseObject tweetObject : objects) {
                            HashMap<String, String> userTweet = new HashMap<>();
                            userTweet.put("tweetUserName", tweetObject.getString("user"));
                            userTweet.put("tweetValue", tweetObject.getString("tweet"));
                            tweetList.add(userTweet);
                        }

                        viewTweetsListView.setAdapter(adapter);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
