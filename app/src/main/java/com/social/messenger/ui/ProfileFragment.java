package com.social.messenger.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.social.messenger.R;

public class ProfileFragment extends Fragment {
    private RecyclerView recyclerViewOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerViewOptions = view.findViewById(R.id.recycler_view_profile_options);
        recyclerViewOptions.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerViewOptions.setAdapter(new ProfileOptionsAdapter());

        return view;
    }
}
