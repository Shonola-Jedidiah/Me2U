package com.luhyah.me2u.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luhyah.me2u.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Discovered_Devices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Discovered_Devices extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discovered__devices, container, false);
    }
}