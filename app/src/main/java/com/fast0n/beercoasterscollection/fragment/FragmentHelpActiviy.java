package com.fast0n.beercoasterscollection.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fast0n.beercoasterscollection.HelpActivity;
import com.fast0n.beercoasterscollection.R;

public class FragmentHelpActiviy extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TextView help;

        View view = inflater.inflate(R.layout.fragment_help_activiy, container, false);

        help = (TextView) view.findViewById(R.id.textView1);

        /**
         * Al click del tasto passa all'activity help
         */
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        return view;
    }
}