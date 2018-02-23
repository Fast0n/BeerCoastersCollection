package com.fast0n.beercoasterscollection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fast0n.beercoasterscollection.java.DatabaseInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class HelpActivity extends AppCompatActivity {

    private EditText email_field;
    private Button reset_button;
    private TextView back_button;
    InputMethodManager keyboard;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        /**
         * Indirizzi java
         */
        email_field = (EditText) findViewById(R.id.editText);
        reset_button = (Button) findViewById(R.id.button);
        back_button = (TextView) findViewById(R.id.button2);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        /**
         * Colore Navbar
         */
        getWindow().setNavigationBarColor(Color.BLACK);

        /**
         * Firebase
         */
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        /**
         *  Nascondi/mostra il cursore quando si tocca il campo all'apertura dell'activity
         */
        email_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_field.setFocusable(true);
                email_field.setFocusableInTouchMode(true);
                email_field.requestFocus();
                keyboard.showSoftInput(email_field, 0);

            }
        });

        /**
         *  Click del tasto con la tastiera
         */
        email_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    reset_button.performClick();
                }
                return false;
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(HelpActivity.this, LoginActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mainActivity);
            }
        });

    }

    public void CheckFields(final View view) {
        /**
         * Al click del tasto c'è un controllo dei campi:
         * - controlla se è vuoto o minore di 5.
         */
        disableField();

        if (email_field.getText().toString().equals("")) {
            email_field.setError(getString(R.string.error));
            enableField();
        }

        else if (email_field.getText().length() < 5) {
            email_field.setError(getString(R.string.errorLength));
            enableField();
        } else {
            reset_button.setBackgroundDrawable(getDrawable(R.drawable.button_border));
            reset_button.setTextColor(Color.BLACK);

            if (isOnline()) {

                final Query query = databaseRef.child("users").orderByChild("email")
                        .equalTo(email_field.getText().toString().toLowerCase());

                final Query query2 = databaseRef.child("users").orderByChild("username")
                        .equalTo(email_field.getText().toString().toLowerCase());

                if (isEmail(email_field.getText().toString())) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String email = dataSnapshot.toString().split("=")[2].replace(" {", "");

                            if (email.equals(" null }")) {
                                email_field.setError(getString(R.string.errorEmail));
                                enableField();
                            } else {
                                keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                resetPasswordEmail(email_field.getText().toString());

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {

                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String username = dataSnapshot.toString().split("=")[2].replace(" {", "");

                            if (username.equals(" null }")) {
                                email_field.setError(getString(R.string.errorUsernameExists));
                                enableField();
                            } else if (email_field.getText().toString().equals(username.toLowerCase())
                                    || email_field.getText().toString().equals(username)) {
                                keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                databaseRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        resetPasswordUsername(dataSnapshot);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            } else {
                Toasty.error(HelpActivity.this, getString(R.string.errorConnection), Toast.LENGTH_SHORT, true).show();
            }

        }

    }

    /**
     * Controllo connessione internet
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void resetPasswordUsername(DataSnapshot dataSnapshot) {
        for (final DataSnapshot ds : dataSnapshot.getChildren()) {
            final DatabaseInformation uInfo = new DatabaseInformation();

            final Query query = databaseRef.child("users").orderByChild("username")
                    .equalTo(email_field.getText().toString().toLowerCase());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String username = dataSnapshot.toString().split("=")[2].replace(" {", "");

                    uInfo.setEmail((ds.child(username).getValue(DatabaseInformation.class).getEmail()));
                    resetPasswordEmail(uInfo.getEmail());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    /**
     * Controllo se il campo è una email
     */
    public static boolean isEmail(String email) {
        String expression = "^[\\w\\.]+@([\\w]+\\.)+[A-Z]{2,7}$";
        CharSequence inputString = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private void resetPasswordEmail(final String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    final ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar1);
                    spinner.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    reset_button.setText("");
                    Intent intent = new Intent(HelpActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    Toasty.info(HelpActivity.this, getString(R.string.toastChangePassword), Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    public void disableField() {
        email_field.setEnabled(false);
        reset_button.setEnabled(false);
    }

    public void enableField() {
        reset_button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
        reset_button.setTextColor(getColor(R.color.colorPrimary));
        email_field.setEnabled(true);
        reset_button.setEnabled(true);
    }
}