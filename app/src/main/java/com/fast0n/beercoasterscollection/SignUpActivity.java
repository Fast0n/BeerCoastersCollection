package com.fast0n.beercoasterscollection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fast0n.beercoasterscollection.java.GeneratePassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {

    EditText name_field, username_field, email_field, password_field;
    Button signup_button;
    InputMethodManager keyboard;
    ProgressBar spinner;
    ConstraintLayout hide_layout;
    TextInputLayout widget;
    ImageButton generatePassword;
    LinearLayout linear_layout;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    Unregistrar mUnregistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /**
         * Indirizzi java
         */
        name_field = (EditText) findViewById(R.id.editText);
        username_field = (EditText) findViewById(R.id.editText2);
        email_field = (EditText) findViewById(R.id.editText3);
        password_field = (EditText) findViewById(R.id.editText4);
        signup_button = (Button) findViewById(R.id.button2);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        hide_layout = (ConstraintLayout) findViewById(R.id.hide);
        widget = (TextInputLayout) findViewById(R.id.widget4);
        generatePassword = (ImageButton) findViewById(R.id.button);
        linear_layout = (LinearLayout) findViewById(R.id.linear_layout);

        /**
         * Genera password sicura
         */
        generatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rand_password = GeneratePassword.randomString(12);
                password_field.setText(rand_password);
            }
        });

        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                updateKeyboardStatusText(isOpen);
            }
        });
        updateKeyboardStatusText(KeyboardVisibilityEvent.isKeyboardVisible(this));

        /**
         * Firebase
         */
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        /**
         *  Nascondi/mostra il cursore quando si tocca il campo all'apertura dell'activity
         */
        name_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_field.setFocusable(true);
                name_field.setFocusableInTouchMode(true);
                name_field.requestFocus();
                keyboard.showSoftInput(name_field, 0);

                username_field.setFocusable(true);
                username_field.setFocusableInTouchMode(true);

                email_field.setFocusable(true);
                email_field.setFocusableInTouchMode(true);

                password_field.setFocusable(true);
                password_field.setFocusableInTouchMode(true);
            }
        });

        username_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_field.setFocusable(true);
                username_field.setFocusableInTouchMode(true);
                username_field.requestFocus();
                keyboard.showSoftInput(username_field, 0);

                email_field.setFocusable(true);
                email_field.setFocusableInTouchMode(true);

                password_field.setFocusable(true);
                password_field.setFocusableInTouchMode(true);
            }
        });

        email_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_field.setFocusable(true);
                email_field.setFocusableInTouchMode(true);
                email_field.requestFocus();
                keyboard.showSoftInput(email_field, 0);

                password_field.setFocusable(true);
                password_field.setFocusableInTouchMode(true);
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
                    signup_button.performClick();
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

        if (name_field.getText().toString().equals("")) {
            name_field.setError(getString(R.string.error));
            enableField();
        }

        else if (name_field.getText().length() < 5) {
            name_field.setError(getString(R.string.errorLength));
            enableField();
        }

        else if (username_field.getText().toString().equals("")) {
            username_field.setError(getString(R.string.error));
            enableField();
        }

        else if (username_field.getText().length() < 5) {
            username_field.setError(getString(R.string.errorLength));

            enableField();
        }

        else if (email_field.getText().toString().equals("")) {
            email_field.setError(getString(R.string.error));
            enableField();
        }

        else if (password_field.getText().length() < 5) {
            widget.setError(getString(R.string.errorLength));
            enableField();
        }

        else if (password_field.getText().toString().equals("")) {
            widget.setError(getString(R.string.error));
            enableField();
        }

        else if (!isValidEmail(email_field.getText().toString())) {
            email_field.setError(getString(R.string.errorEmail));
            enableField();
        }

        else {
            signup_button.setBackgroundDrawable(getDrawable(R.drawable.button_border));
            signup_button.setTextColor(Color.BLACK);
            keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
            signup_button.setEnabled(false);

            /**
             * Controllo connessione internet
             */
            if (!isOnline()) {
                Toasty.error(SignUpActivity.this, getString(R.string.errorConnection), Toast.LENGTH_LONG, true).show();

            } else {

                /**
                 * Crea l'account con Autentificator
                 */
                signUpUser(email_field.getText().toString(), password_field.getText().toString());
            }

        }
    }

    private void signUpUser(String e_mail, String password) {
        auth.signInWithEmailAndPassword(e_mail, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            final Query query = databaseRef.child("users").orderByChild("username")
                                    .equalTo(username_field.getText().toString().toLowerCase());

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String username = dataSnapshot.toString().split("=")[2].replace(" {", "");

                                    if (username.equals(" null }")) {
                                        try {
                                            signUpUser_one(email_field.getText().toString(),
                                                    password_field.getText().toString());
                                        } catch (Exception e) {
                                            Intent i = getBaseContext().getPackageManager()
                                                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                        }

                                    } else {

                                        username_field.setError(getString(R.string.errorUsernameExists));
                                        enableField();

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            email_field.setError(getString(R.string.errorEmailExists));
                            signup_button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                            signup_button.setTextColor(getColor(R.color.colorPrimary));
                            enableField();
                        }
                    }
                });
    }
    /**
     * Controllo connessione internet
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void signUpUser_one(String e_mail, String password) {
        auth.createUserWithEmailAndPassword(e_mail, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            email_field.setError(getString(R.string.errorEmailExists));
                            signup_button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                            signup_button.setTextColor(getColor(R.color.colorPrimary));
                        } else {

                            disableField();

                            SharedPreferences preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                            SharedPreferences.Editor preferences1 = preferences.edit();
                            preferences1.putString("singup", "1");
                            preferences1.commit();

                            /**
                             * Aggiunge i parametri sul database
                             */

                            databaseRef = database.getReference("users");
                            databaseRef.child(username_field.getText().toString() + "").child("name")
                                    .setValue(name_field.getText() + "");
                            databaseRef.child(username_field.getText().toString() + "").child("username")
                                    .setValue(username_field.getText().toString().toLowerCase());
                            databaseRef.child(username_field.getText().toString() + "").child("email")
                                    .setValue(email_field.getText().toString().toLowerCase());
                            databaseRef.child(username_field.getText().toString() + "").child("status").setValue(0);

                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            intent.putExtra("email", email_field.getText().toString());
                            intent.putExtra("password", password_field.getText().toString());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            sendVerificationEmail();
                            startActivity(intent);
                            Toasty.warning(SignUpActivity.this, getString(R.string.toastSignup), 10000).show();

                        }
                    }
                });

    }

    /**
     * Nascondi layout se la tastierà è aperta
     */
    private void updateKeyboardStatusText(boolean isOpen) {

        if (isOpen == true) {
            hide_layout.setVisibility(View.INVISIBLE);
        } else {
            hide_layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Invia la mail di verifica
     */
    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    /**
     * Controllo se il campo è una email
     */
    public static boolean isValidEmail(String email) {
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

    void unregister() {
        mUnregistrar.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnregistrar.unregister();
    }

    public void disableField() {
        spinner.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);

        signup_button.setText("");
        name_field.setEnabled(false);
        username_field.setEnabled(false);
        email_field.setEnabled(false);
        password_field.setEnabled(false);

    }

    public void enableField() {
        spinner.setVisibility(View.GONE);
        spinner.setVisibility(View.INVISIBLE);

        signup_button.setText(getString(R.string.signup));
        signup_button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
        signup_button.setTextColor(getColor(R.color.colorPrimary));

        name_field.setEnabled(true);
        username_field.setEnabled(true);
        email_field.setEnabled(true);
        password_field.setEnabled(true);
        signup_button.setEnabled(true);
        linear_layout.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        generatePassword.getLayoutParams().height = 150;
    }
}
