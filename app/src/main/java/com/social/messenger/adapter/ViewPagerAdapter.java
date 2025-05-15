package com.social.messenger.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.social.messenger.ui.MediaFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return MediaFragment.newInstance(position == 0
                ? MediaFragment.TYPE_IMAGE
                : MediaFragment.TYPE_VIDEO);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
