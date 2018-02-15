package com.fast0n.beercoasterscollection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {

    EditText mail, password;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        /**
         * Indirizzi java
         */
        mail = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        login = (Button) findViewById(R.id.button);

        /**
         * Controllo le SharedPreferences, se il valore di signup è diverso da 0
         * appare un toast.
         * Infine setto 0 le SharedPreferences per lo stesso.
         */
        SharedPreferences preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String SignUp = preferences.getString("singup", "");
        if (!SignUp.equals("0")) {

            for (int i = 0; i < 20; i++)
                Toast.makeText(LoginPage.this, getString(R.string.toast_signup), Toast.LENGTH_LONG).show();

            SharedPreferences preferences2 = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor preferences3 = preferences2.edit();
            preferences3.putString("singup", "0");
            preferences3.commit();
        }

        /**
         * Al click del tasto "Login" c'è un controllo dei campi:
         * - controlla se è vuoto o minore di 5.
         * Se i campi sono giusti setto le SharedPreferences a 1 e apro la MainActivity.
         */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mail.getText().toString().equals("")) {
                    mail.setError(getString(R.string.error));
                    login.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                    login.setTextColor(getColor(R.color.colorPrimary));
                }

                else if (mail.getText().length() < 5) {
                    mail.setError(getString(R.string.errorLength));
                    login.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                    login.setTextColor(getColor(R.color.colorPrimary));
                }

                if (password.getText().toString().equals("")) {
                    password.setError(getString(R.string.error));
                    login.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                    login.setTextColor(getColor(R.color.colorPrimary));
                }

                else if (password.getText().length() < 5) {
                    password.setError(getString(R.string.errorLength));
                    login.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
                    login.setTextColor(getColor(R.color.colorPrimary));
                }
                else {
                    login.setBackgroundDrawable(getDrawable(R.drawable.button_border));
                    login.setTextColor(Color.BLACK);

                    SharedPreferences preferences2 = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor preferences3 = preferences2.edit();
                    preferences3.putString("login", "1");
                    preferences3.commit();
                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                    startActivity(intent);
                }

            }
        });

    }

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
