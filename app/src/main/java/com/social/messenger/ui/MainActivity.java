package com.social.messenger.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.social.messenger.R;
import com.social.messenger.StompClientManager;
import com.social.messenger.utils.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    private ActivityResultLauncher<String> requestMediaImagesPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Khởi tạo ActivityResultLauncher để xin quyền READ_MEDIA_IMAGES
        requestMediaImagesPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Quyền READ_MEDIA_IMAGES đã được cấp.");
                        // Tiếp tục thực hiện các thao tác cần quyền đọc ảnh
                        // Ví dụ: load hình ảnh từ bộ nhớ
                    } else {
                        Toast.makeText(this, "Ứng dụng cần quyền truy cập ảnh để thực hiện chức năng này.", Toast.LENGTH_SHORT).show();
                        // Xử lý trường hợp người dùng từ chối quyền
                    }
                });

        // Load mặc định fragment Messages
        loadFragment(new MessagesFragment());

        // Lắng nghe chọn item bottom nav
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.navigation_messages) {
                selectedFragment = new MessagesFragment();
            } else if (id == R.id.navigation_new_friends) {
                selectedFragment = new AddFriendFragment();
            } else if (id == R.id.navigation_you) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        // Kiểm tra và xin quyền truy cập ảnh khi Activity được tạo
        checkAndRequestMediaImagesPermission();
    }

    private void checkAndRequestMediaImagesPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                // Xin quyền READ_MEDIA_IMAGES
                requestMediaImagesPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                // Đã có quyền READ_MEDIA_IMAGES
                Log.d(TAG, "Đã có quyền READ_MEDIA_IMAGES.");
                // Tiếp tục thực hiện các thao tác cần quyền đọc ảnh
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Xin quyền READ_EXTERNAL_STORAGE
                requestMediaImagesPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                // Đã có quyền READ_EXTERNAL_STORAGE
                Log.d(TAG, "Đã có quyền READ_EXTERNAL_STORAGE.");
                // Tiếp tục thực hiện các thao tác cần quyền đọc ảnh
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Connect WebSocket (chỉ gọi 1 lần)
        String currentUserId = SharedPrefManager.getInstance(this).getUserId();
        if (currentUserId != null) {
            StompClientManager.getInstance(this).connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // StompClientManager.getInstance().disconnect(); // Tùy bạn muốn giữ hay ngắt kết nối
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}