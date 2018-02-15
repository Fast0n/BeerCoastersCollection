package com.fast0n.beercoasterscollection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Controllo del login tramite SharedPreferences
         */
        SharedPreferences preferences_control_login = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String Boot = preferences_control_login.getString("login", "");
        if (!Boot.equals("1")){

            SharedPreferences preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor preferences1= preferences.edit();
            preferences1.putString("login", "0");
            preferences1.commit();

            SharedPreferences preferences2 = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor preferences3= preferences2.edit();
            preferences3.putString("singup", "0");
            preferences3.commit();

            Intent mainActivity = new Intent(MainActivity.this, LoginPage.class);
            startActivity(mainActivity);

        }

    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
