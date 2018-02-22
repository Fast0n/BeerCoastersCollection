package com.fast0n.beercoasterscollection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fast0n.beercoasterscollection.java.CustomSpinner;
import com.fast0n.beercoasterscollection.java.DatabaseInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    View header;
    int a = 2;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /**
         * Imposta il secondo menu sulla barra di default
         */

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        /**
         * Indirizzi java
         */
        header = navigationView.getHeaderView(0);

        /**
         * Colore Navbar
         */
        getWindow().setNavigationBarColor(Color.BLACK);

        /**
         * Programmazione del tasto esci con uno spinner modificato
         */
        final List<String> list = new ArrayList<String>();
        list.add(getString(R.string.ic_logout));
        list.add("");
        final int listsize = list.size() - 1;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list) {
            @Override
            public int getCount() {

                return (listsize); // Truncate the list
            }
        };

        final CustomSpinner spinner = (CustomSpinner) header.findViewById(R.id.spinner);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(listsize); // Hidd

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle(getString(R.string.ic_logout));
                    builder.setMessage(getString(R.string.sure_two));

                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            auth.signOut();
                            logoutUser();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                    spinner.setSelection(1);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //View headerview = navigationView.getHeaderView(0);

        /**
         * Firebase
         */
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        /**
         * Controllo del login tramite SharedPreferences
         */

        SharedPreferences preferences_control_login = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String Boot = preferences_control_login.getString("login", "");

        String prefChangePassword = preferences_control_login.getString("changePassword", "");

        if (prefChangePassword.equals(null)) {

            SharedPreferences preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor preferences1 = preferences.edit();
            preferences1.putString("changePassword", "0");
            preferences1.commit();

        } else if (prefChangePassword.equals("2")) {

            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        }

        if (!Boot.equals("1")) {

            SharedPreferences preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor preferences1 = preferences.edit();
            preferences1.putString("login", "0");
            preferences1.commit();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);

        } else {

            databaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        showData(dataSnapshot);
                        /**
                         * Controllo connessione internet
                         */
                        if (!isOnline()) {
                            auth.signOut();
                            logoutUser();
                            Toasty.error(MainActivity.this, getString(R.string.errorConnection), Toast.LENGTH_LONG,
                                    true).show();
                        }
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

    private void showData(DataSnapshot dataSnapshot) {
        for (final DataSnapshot ds : dataSnapshot.getChildren()) {
            final DatabaseInformation uInfo = new DatabaseInformation();

            String email = auth.getCurrentUser().getEmail();
            final Query query = databaseRef.child("users").orderByChild("email").equalTo(email);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String username = dataSnapshot.toString().split("=")[2].replace(" {", "");

                    try {
                        uInfo.setName(ds.child(username).getValue(DatabaseInformation.class).getName());
                    } catch (Exception e) {

                    }
                    uInfo.setEmail((ds.child(username).getValue(DatabaseInformation.class).getEmail()));
                    uInfo.setUsername(ds.child(username).getValue(DatabaseInformation.class).getUsername());
                    uInfo.setStatus(ds.child(username).getValue(DatabaseInformation.class).getStatus());

                    final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(MainActivity.this);

                    final TextView email = (TextView) header.findViewById(R.id.email);
                    TextView name = (TextView) header.findViewById(R.id.name);
                    Menu menu = navigationView.getMenu();

                    try {
                        menu.findItem(R.id.label).setTitle(uInfo.getUsername().toUpperCase());
                    } catch (Exception e) {

                    }

                    /**
                     * Controlla il numero di caratteri nella Email e nel nome e lo modifica
                     * in base al numero
                     */

                    if (uInfo.getEmail().length() <= 15) {
                        email.setText(uInfo.getEmail());
                    } else {
                        email.setText(uInfo.getEmail().substring(0, 18) + "...");
                    }

                    if (uInfo.getName().length() <= 18) {
                        name.setText(uInfo.getName());
                    } else {
                        name.setText(uInfo.getName().substring(0, 20) + "...");
                    }

                    if (uInfo.getStatus() == 1) {
                        auth.signOut();
                        logoutUser();
                        Toasty.error(MainActivity.this, getString(R.string.toastBlock), Toast.LENGTH_LONG, true).show();
                    }
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

            startActivity(new Intent(MainActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View header = navigationView.getHeaderView(0);
            TextView email = (TextView) header.findViewById(R.id.email);

            /**
             * Controllo connessione internet
             */
            if (!isOnline()) {
                auth.signOut();
                logoutUser();
                Toasty.error(MainActivity.this, getString(R.string.errorConnection), Toast.LENGTH_LONG, true).show();

            } else {
                Intent mainActivity = new Intent(MainActivity.this, SettingsActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mainActivity.putExtra("email", email.getText().toString());
                startActivity(mainActivity);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
