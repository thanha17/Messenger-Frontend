package com.social.messenger.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.social.messenger.R;
import com.social.messenger.models.User;
import com.social.messenger.repository.UserRepository;
import com.social.messenger.utils.SharedPrefManager;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewOptions;
    private Button btnLogout;
    private Button btnSaveAvatar;
    private ImageView imgAvatar;
    private TextView txtUsername;
    private Uri selectedImageUri;
    private String currentUserId = MessagesFragment.currentUserId;
    private UserRepository userRepository = new UserRepository();
    private User user;

    // Dùng API mới để chọn ảnh
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.Source source = ImageDecoder.createSource(requireContext().getContentResolver(), selectedImageUri);
                                bitmap = ImageDecoder.decodeBitmap(source);
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                            }
                            imgAvatar.setImageBitmap(bitmap);
                            btnSaveAvatar.setVisibility(View.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerViewOptions = view.findViewById(R.id.recycler_view_profile_options);
        recyclerViewOptions.setLayoutManager(new LinearLayoutManager(getContext()));


        txtUsername = view.findViewById(R.id.text_view_username);

        imgAvatar = view.findViewById(R.id.image_view_avatar);
        btnSaveAvatar = view.findViewById(R.id.btn_edit_profile);
        btnLogout = view.findViewById(R.id.btn_logout);

        btnSaveAvatar.setVisibility(View.GONE);

        imgAvatar.setOnClickListener(v -> openImagePicker());
        setupUser();


        btnSaveAvatar.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                // TODO: Gửi ảnh lên backend ở đây
                user.setAvatarUrl(selectedImageUri.toString());
                userRepository.updateUser(user);
                btnSaveAvatar.setVisibility(View.GONE);
//                new android.os.Handler().postDelayed(() -> {
//                    setupUser();
//                    btnSaveAvatar.setVisibility(View.GONE);
//                }, 5000); // 5000 ms = 5 giây
            }
        });

        btnLogout.setOnClickListener(v -> {
            SharedPrefManager.getInstance(requireContext()).clear();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void setupUser() {
        userRepository.getUserById(currentUserId, new UserRepository.UserCallback() {
            @Override
            public void onUserReceived(User user) {
                ProfileFragment.this.user = user;
                // Dùng user ở đây
                Log.d("User", "Name: " + user.getFullName());
                txtUsername.setText(user.getFullName());
                Glide.with(ProfileFragment.this)
                        .load(user.getAvatarUrl())// ảnh khi lỗi
                        .into(imgAvatar);
            }

            @Override
            public void onError(String error) {
                Log.e("User", "Lỗi: " + error);
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
}
