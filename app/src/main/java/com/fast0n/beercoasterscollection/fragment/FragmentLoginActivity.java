package com.fast0n.beercoasterscollection.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fast0n.beercoasterscollection.R;
import com.fast0n.beercoasterscollection.SignUpActivity;

public class FragmentLoginActivity extends Fragment {

 public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

     TextView login;


     View view = inflater.inflate(R.layout.fragment_login_activity, container, false);

     login = (TextView) view.findViewById(R.id.textView1);

     /**
      * Al click del tasto passa all'activity login
      */
     login.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent mainActivity = new Intent(getActivity(), SignUpActivity.class);
             mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
             startActivity(mainActivity);
         }
     });

     return view;
 }
}
