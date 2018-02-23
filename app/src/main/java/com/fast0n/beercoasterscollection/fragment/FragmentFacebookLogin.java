package com.fast0n.beercoasterscollection.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fast0n.beercoasterscollection.R;

import es.dmoral.toasty.Toasty;

public class FragmentFacebookLogin extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TextView facebook;

        View view = inflater.inflate(R.layout.fragment_facebook_login, container, false);

        facebook = (TextView) view.findViewById(R.id.textView1);

        /**
         * Al click esegui il login con Facebook
         */
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(getActivity(), getString(R.string.comingSoon), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
