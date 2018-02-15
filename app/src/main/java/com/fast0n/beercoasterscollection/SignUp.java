package com.fast0n.beercoasterscollection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    EditText name, username, mail;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = (EditText) findViewById(R.id.editText3);
        username = (EditText) findViewById(R.id.editText4);
        mail = (EditText) findViewById(R.id.editText5);
        signup = (Button) findViewById(R.id.button2);

    }

    public void CheckFields(View view) {

        String email = mail.getText().toString();
        if (name.getText().toString().equals("")) {
            name.setError(getString(R.string.error));
            signup.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
            signup.setTextColor(getColor(R.color.colorPrimary));
        }

        else if (name.getText().length() < 5) {
            name.setError(getString(R.string.errorLength));
            signup.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
            signup.setTextColor(getColor(R.color.colorPrimary));
        }

        if (username.getText().toString().equals("")) {
            username.setError(getString(R.string.error));
            signup.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
            signup.setTextColor(getColor(R.color.colorPrimary));
        }

        else if (username.getText().length() < 5) {
            username.setError(getString(R.string.errorLength));
            signup.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
            signup.setTextColor(getColor(R.color.colorPrimary));
        }

        if (mail.getText().toString().equals("")) {
            mail.setError(getString(R.string.error));
            signup.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
            signup.setTextColor(getColor(R.color.colorPrimary));
        }

        else if (!isValidEmail(email)) {
            mail.setError(getString(R.string.errorMail));
            signup.setBackgroundDrawable(getDrawable(R.drawable.button_disabled_border));
            signup.setTextColor(getColor(R.color.colorPrimary));
        } else {
            signup.setBackgroundDrawable(getDrawable(R.drawable.button_border));
            signup.setTextColor(Color.BLACK);

            SharedPreferences preferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor preferences1 = preferences.edit();
            preferences1.putString("singup", "1");
            preferences1.commit();

            Intent intent = new Intent(SignUp.this, LoginPage.class);
            startActivity(intent);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

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
}
