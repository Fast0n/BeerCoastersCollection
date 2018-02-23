package com.fast0n.beercoasterscollection.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fast0n.beercoasterscollection.LoginActivity;
import com.fast0n.beercoasterscollection.R;

public class FragmentSignUpActivity extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TextView signup;

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        signup = (TextView) view.findViewById(R.id.textView1);

        /**
         * Al click del tasto passa all'activity sign up
         */

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getActivity(), LoginActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mainActivity);
            }
        });

        return view;
    }
}
