package com.fast0n.beercoasterscollection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fast0n.beercoasterscollection.java.DatabaseInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class DeleteAccountActivity extends AppCompatActivity {

    EditText email_field, password_field;
    Button login_button;
    InputMethodManager keyboard;
    ProgressBar spinner;
    TextInputLayout widget;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        /**
         * Indirizzi java
         */
        email_field = (EditText) findViewById(R.id.editText);
        password_field = (EditText) findViewById(R.id.editText2);
        login_button = (Button) findViewById(R.id.button);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        widget = (TextInputLayout) findViewById(R.id.widget1);
        /**
         * Firebase
         */
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        /**
         * Importa email e password dal SignUp
         */
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        email_field.setText(email);
        password_field.setText(password);

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

        password_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password_field.setFocusable(true);
                password_field.setFocusableInTouchMode(true);
                password_field.requestFocus();
                keyboard.showSoftInput(password_field, 0);
            }
        });

        password_field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    login_button.performClick();
                }
                return false;
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
        } else if (email_field.getText().length() < 5) {
            email_field.setError(getString(R.string.errorLength));
            enableField();
        } else if (email_field.getText().toString().length() > 0 && email_field.getText().toString().startsWith(" ")) {
            email_field.setError(getString(R.string.errorSpace));
            enableField();
        } else if (password_field.getText().toString().equals("")) {
            widget.setError(getString(R.string.error));
            enableField();
        } else if (password_field.getText().length() < 5) {
            widget.setError(getString(R.string.errorLength));
            enableField();
        } else if (password_field.getText().toString().length() > 0
                && password_field.getText().toString().startsWith(" ")) {
            widget.setError(getString(R.string.errorSpace));
            enableField();
        } else {
            login_button.setBackgroundDrawable(getDrawable(R.drawable.button_border));
            login_button.setTextColor(Color.BLACK);

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
                                loginUser(email_field.getText().toString(), password_field.getText().toString());
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
                            }

                            else if (email_field.getText().toString().equals(username.toLowerCase())
                                    || email_field.getText().toString().equals(username)) {
                                keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                disableField();
                                databaseRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        try {
                                            showData(dataSnapshot, username);
                                        } catch (Exception e) {
                                            Intent i = getBaseContext().getPackageManager()
                                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                        }

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
                Toasty.error(DeleteAccountActivity.this, getString(R.string.errorConnection), Toast.LENGTH_SHORT, true)
                        .show();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
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

    private void showData(DataSnapshot dataSnapshot, String username) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            DatabaseInformation uInfo = new DatabaseInformation();

            uInfo.setEmail((ds.child(username).getValue(DatabaseInformation.class).getEmail()));
            loginUser(uInfo.getEmail(), password_field.getText().toString());

        }
    }

    /**
     * Cancella l'account Authentication Database
     */

    private void loginUser(String e_mail, final String p_password) {
        auth.signInWithEmailAndPassword(e_mail, p_password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            widget.setError(getString(R.string.errorPassword));
                            enableField();
                        } else {

                            /**
                             * Elimina account su Authentication
                             */

                            if (isEmail(email_field.getText().toString())) {

                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final AuthCredential credential = EmailAuthProvider.getCredential(
                                        email_field.getText().toString(), password_field.getText().toString());

                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    SharedPreferences preferences = getSharedPreferences(
                                                            "SharedPreferences", MODE_PRIVATE);
                                                    SharedPreferences.Editor preferences1 = preferences.edit();
                                                    preferences1.putString("login", "0");
                                                    preferences1.commit();

                                                    /**
                                                     * Elimina account dal Database
                                                     */

                                                    final Query query = databaseRef.child("users").orderByChild("email")
                                                            .equalTo(email_field.getText().toString());

                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            String username = dataSnapshot.toString().split("=")[2]
                                                                    .replace(" {", "");

                                                            databaseRef.child("users").child(username).removeValue();
                                                            Intent i = getBaseContext().getPackageManager()
                                                                    .getLaunchIntentForPackage(
                                                                            getBaseContext().getPackageName());
                                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(i);
                                                            Toasty.success(DeleteAccountActivity.this,
                                                                    getString(R.string.toastAccountDeleted),
                                                                    Toast.LENGTH_LONG, true).show();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });

                                                }
                                            }
                                        });

                                    }
                                });

                            } else {

                                /**
                                 * Elimina account su Authentication
                                 */

                                final String email = auth.getCurrentUser().getEmail();

                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final AuthCredential credential = EmailAuthProvider.getCredential(email,
                                        password_field.getText().toString());

                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toasty.success(DeleteAccountActivity.this,
                                                            getString(R.string.toastAccountDeleted), Toast.LENGTH_SHORT,
                                                            true).show();

                                                    SharedPreferences preferences = getSharedPreferences(
                                                            "SharedPreferences", MODE_PRIVATE);
                                                    SharedPreferences.Editor preferences1 = preferences.edit();
                                                    preferences1.putString("login", "0");
                                                    preferences1.commit();
                                                    startActivity(
                                                            new Intent(DeleteAccountActivity.this, MainActivity.class));

                                                    /**
                                                     * Elimina account dal Database
                                                     */
                                                    final Query query = databaseRef.child("users").orderByChild("email")
                                                            .equalTo(email);

                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            String username = dataSnapshot.toString().split("=")[2]
                                                                    .replace(" {", "");
                                                            databaseRef.child("users").child(username).removeValue();
                                                            //clearApplicationData();
                                                            Intent i = getBaseContext().getPackageManager()
                                                                    .getLaunchIntentForPackage(
                                                                            getBaseContext().getPackageName());
                                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(i);
                                                            Toasty.success(DeleteAccountActivity.this,
                                                                    getString(R.string.toastAccountDeleted),
                                                                    Toast.LENGTH_LONG, true).show();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });

                                                }
                                            }
                                        });

                                    }
                                });

                            }

                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();

        Intent mainActivity = new Intent(DeleteAccountActivity.this, SettingsActivity.class);
        startActivity(mainActivity);
    }

    public void disableField() {
        spinner.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        login_button.setText("");
        email_field.setEnabled(false);
        password_field.setEnabled(false);
        login_button.setEnabled(false);
    }

    public void enableField() {
        spinner.setVisibility(View.GONE);
        spinner.setVisibility(View.INVISIBLE);

        login_button.setText(getString(R.string.login));
        login_button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
        login_button.setTextColor(getColor(R.color.colorPrimary));

        email_field.setEnabled(true);
        password_field.setEnabled(true);
        login_button.setEnabled(true);

        widget.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
    }
}
