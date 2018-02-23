package com.fast0n.beercoasterscollection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fast0n.beercoasterscollection.java.DatabaseInformation;
import com.fast0n.beercoasterscollection.java.GeneratePassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.spec.ECField;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {

    String[] list2;
    Button delete_account;
    InputMethodManager keyboard;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        /**
         * Firebase
         */
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        /**
         * Indirizzi java
         */
        final ListView list = (ListView) findViewById(R.id.list);
        delete_account = (Button) findViewById(R.id.button);

        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        list2 = getResources().getStringArray(R.array.setting_list);

        /**
         * Colore Navbar
         */
        getWindow().setNavigationBarColor(Color.BLACK);

        /**
         * Click per eliminare l'account
         */

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(SettingsActivity.this, DeleteAccountActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mainActivity);
            }
        });
        /**
         * Creazione lista impostazioni
         */

        ArrayAdapter<String> setting = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_list_item_1, list2);
        list.setAdapter(setting);
        list.setDivider(null);
        list.setDividerHeight(5);

        /**
         * Cambio password
         */

        // Controlla la SharedPreferences
        SharedPreferences preferences_control_login = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String prefChangePassword = preferences_control_login.getString("changePassword", "");
        // Modifica un campo di SharedPreferences
        SharedPreferences.Editor preferences1 = preferences_control_login.edit();

        if (preferences_control_login.getString("changePassword", null) == null) {
            preferences1.putString("changePassword", "0");
            preferences1.commit();
        }

        else if (prefChangePassword.equals("2")) {

            preferences1.putString("changePassword", "0");
            preferences1.commit();
            final Dialog dialog = new Dialog(SettingsActivity.this, R.style.Dialog);
            dialog.setContentView(R.layout.dialog_change_password);
            final EditText new_password = (EditText) dialog.findViewById(R.id.editText);
            final ProgressBar spinner = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            final Button button = (Button) dialog.findViewById(R.id.button2);
            final TextInputLayout widget = (TextInputLayout) dialog.findViewById(R.id.widget);
            final ImageButton generatePassword = (ImageButton) dialog.findViewById(R.id.button);
            final LinearLayout linear_layout = (LinearLayout) dialog.findViewById(R.id.linear_layout);

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(true);

            dialog.show();

            /**
             * Genera password sicura
             */
            generatePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rand_password = GeneratePassword.randomString(12);
                    new_password.setText(rand_password);
                }
            });

            new_password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new_password.setFocusable(true);
                    new_password.setFocusableInTouchMode(true);
                    new_password.requestFocus();
                    keyboard.showSoftInput(new_password, 0);

                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (new_password.getText().toString().equals("")) {
                        widget.setError(getString(R.string.error));
                        button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                        button.setTextColor(getColor(R.color.colorPrimary));
                        linear_layout.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        generatePassword.getLayoutParams().height = 150;
                    } else if (new_password.getText().length() < 5) {
                        widget.setError(getString(R.string.errorLength));
                        button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                        button.setTextColor(getColor(R.color.colorPrimary));
                        linear_layout.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        generatePassword.getLayoutParams().height = 150;
                    } else {
                        button.setBackgroundDrawable(getDrawable(R.drawable.button_border));
                        button.setTextColor(Color.BLACK);
                        button.setEnabled(false);
                        new_password.setEnabled(false);
                        changePassword(new_password.getText().toString());
                        spinner.setVisibility(View.GONE);
                        spinner.setVisibility(View.VISIBLE);
                        button.setText("");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                Toasty.success(SettingsActivity.this, getString(R.string.changePasswordSuccess),
                                        Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                        }, 3000);

                    }
                }
            });

        }

        /**
         * Click in base all'oggetto della lista
         */

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                String value = (String) adapter.getItemAtPosition(position);

                if (position == 0) {

                    /**
                     * Cambia nome
                     */

                    databaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                changeName(dataSnapshot);
                                /**
                                 * Controllo connessione internet
                                 */
                                if (!isOnline()) {
                                    auth.signOut();
                                    logoutUser();
                                    Toasty.error(SettingsActivity.this, getString(R.string.errorConnection),
                                            Toast.LENGTH_LONG, true).show();
                                }
                            } catch (Exception e) {

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                if (position == 1) {
                    SharedPreferences preferences_control_login = getSharedPreferences("SharedPreferences",
                            MODE_PRIVATE);
                    String prefChangePassword = preferences_control_login.getString("changePassword", "");

                    if (prefChangePassword.equals("0")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                        builder.setTitle(getString(R.string.ic_logout));
                        builder.setMessage(getString(R.string.delete_account_three));

                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                logoutUser();
                                SharedPreferences preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor preferences1 = preferences.edit();
                                preferences1.putString("changePassword", "1");
                                preferences1.commit();
                                Toasty.info(SettingsActivity.this, getString(R.string.change_password_three), 10000)
                                        .show();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                }

                if (position == 2) {
                    Toasty.success(SettingsActivity.this, getString(R.string.toastOk), Toast.LENGTH_LONG, true).show();
                }

            }
        });

    }

    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    final Dialog dialog = new Dialog(SettingsActivity.this, R.style.Dialog);
                    dialog.dismiss();

                }

            }
        });
    }

    private void changeName(final DataSnapshot dataSnapshot) {
        String email = auth.getCurrentUser().getEmail();
        final Query query = databaseRef.child("users").orderByChild("email").equalTo(email);

        final Dialog dialog = new Dialog(SettingsActivity.this, R.style.Dialog);
        dialog.setContentView(R.layout.dialog_change_name);
        final EditText new_name = (EditText) dialog.findViewById(R.id.editText);
        final ProgressBar spinner = (ProgressBar) dialog.findViewById(R.id.progressBar1);
        final Button button = (Button) dialog.findViewById(R.id.button);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);

        try {
            dialog.show();
        } catch (Exception e) {

        }

        new_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_name.setFocusable(true);
                new_name.setFocusableInTouchMode(true);
                new_name.requestFocus();
                keyboard.showSoftInput(new_name, 0);

            }
        });

        for (final DataSnapshot ds : dataSnapshot.getChildren()) {
            final DatabaseInformation uInfo = new DatabaseInformation();

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String username = dataSnapshot.toString().split("=")[2].replace(" {", "");
                    uInfo.setName(ds.child(username).getValue(DatabaseInformation.class).getName());
                    new_name.setText(uInfo.getName());

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (new_name.getText().toString().equals("")) {
                                new_name.setError(getString(R.string.error));
                                button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                                button.setTextColor(getColor(R.color.colorPrimary));
                            } else if (new_name.getText().length() < 5) {
                                new_name.setError(getString(R.string.errorLength));
                                button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                                button.setTextColor(getColor(R.color.colorPrimary));
                            } else if (new_name.getText().toString().length() > 0
                                    && new_name.getText().toString().startsWith(" ")) {
                                new_name.setError(getString(R.string.errorSpace));
                                button.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                                button.setTextColor(getColor(R.color.colorPrimary));
                            } else {

                                spinner.setVisibility(View.GONE);
                                spinner.setVisibility(View.VISIBLE);
                                button.setText("");
                                button.setBackgroundDrawable(getDrawable(R.drawable.button_border));
                                button.setTextColor(Color.BLACK);
                                button.setEnabled(false);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        databaseRef.child("users").child(username).child("name")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        dataSnapshot.getRef().setValue(new_name.getText().toString());

                                                        Intent intent = new Intent(SettingsActivity.this,
                                                                MainActivity.class);
                                                        //

                                                        SettingsActivity.this.finish();
                                                        startActivity(intent);

                                                        intent = new Intent(getBaseContext(), SettingsActivity.class);
                                                        startActivity(intent);
                                                        dialog.cancel();

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    }
                                }, 3000);

                                Toasty.success(SettingsActivity.this, getString(R.string.changeNameSuccess),
                                        Toast.LENGTH_LONG, true).show();

                            }

                        }

                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void logoutUser() {
        auth.signOut();
        if (auth.getCurrentUser() == null) {
            SharedPreferences preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor preferences1 = preferences.edit();
            preferences1.putString("login", "0");
            preferences1.commit();

            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();

            Intent mainActivity = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(mainActivity);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();

        Intent mainActivity = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

}
