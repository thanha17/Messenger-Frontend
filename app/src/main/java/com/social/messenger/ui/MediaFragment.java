package com.social.messenger.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.social.messenger.R;
import com.social.messenger.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MediaFragment extends Fragment {

    public static final String MEDIA_TYPE = "media_type";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";

    private String mediaType;
    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    public static MediaFragment newInstance(String mediaType) {
        MediaFragment fragment = new MediaFragment();
        Bundle args = new Bundle();
        args.putString(MEDIA_TYPE, mediaType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_video, container, false); // Dùng chung layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewGallery);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        if (getArguments() != null) {
            mediaType = getArguments().getString(MEDIA_TYPE, TYPE_IMAGE);
        }

        loadMedia();
    }

    private void loadMedia() {
        List<String> mediaPaths = new ArrayList<>();
        String[] projection = {MediaStore.MediaColumns.DATA};
        String selection;
        Uri collectionUri;
        String orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC";
        Cursor cursor = null;

        try {
            if (TYPE_IMAGE.equals(mediaType)) {
                collectionUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                selection = MediaStore.Images.Media.DATA + " IS NOT NULL";
            } else if (TYPE_VIDEO.equals(mediaType)) {
                collectionUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                selection = MediaStore.Video.Media.DATA + " IS NOT NULL";
                orderBy = MediaStore.Video.Media.DATE_ADDED + " DESC";
            } else {
                // Xử lý trường hợp mediaType không hợp lệ (có thể log lỗi hoặc hiển thị thông báo)
                return;
            }

            cursor = requireActivity().getContentResolver().query(
                    collectionUri,
                    projection,
                    selection,
                    null,
                    orderBy
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    mediaPaths.add(cursor.getString(0));
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        adapter = new ImageAdapter(mediaPaths, mediaType); // Truyền mediaType vào Adapter
        if (getActivity() instanceof ImageAdapter.OnMediaSelectedCallback) {
            adapter.setOnMediaSelectedCallback((ImageAdapter.OnMediaSelectedCallback) getActivity());
        }
        recyclerView.setAdapter(adapter);
    }
}