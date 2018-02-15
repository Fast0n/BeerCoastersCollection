package com.fast0n.beercoasterscollection;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SignUp_BottomBar extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TextView signup;

        View view = inflater.inflate(R.layout.activity_sign_up_bottom_bar, container, false);

        signup = (TextView) view.findViewById(R.id.textView1);

        /**
         * Al click del tasto "Sign up" passa all'activity sign up
         */

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getActivity(), LoginPage.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mainActivity);
            }
        });

        return view;
    }
}
