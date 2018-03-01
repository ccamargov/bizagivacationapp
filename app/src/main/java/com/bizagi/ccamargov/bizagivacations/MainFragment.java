package com.bizagi.ccamargov.bizagivacations;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View oView = inflater.inflate(R.layout.fragment_main, container, false);
        return oView;
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}