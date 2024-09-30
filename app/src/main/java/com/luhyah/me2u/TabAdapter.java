package com.luhyah.me2u;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.luhyah.me2u.fragments.Discovered_Devices;
import com.luhyah.me2u.fragments.PairedDevices;

public class TabAdapter extends FragmentStateAdapter {
    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new PairedDevices();
            case 1:
                return new Discovered_Devices();
            default:
                return new PairedDevices();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
