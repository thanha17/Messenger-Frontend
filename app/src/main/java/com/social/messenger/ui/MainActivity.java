package com.social.messenger.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.social.messenger.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // XML chứa bottom_nav + FrameLayout

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load mặc định fragment Messages
        loadFragment(new MessagesFragment());

        // Lắng nghe chọn item bottom nav
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.navigation_messages) {
                selectedFragment = new MessagesFragment();
            }
            else if (id == R.id.navigation_new_friends) {
                selectedFragment = new AddFriendFragment();
            }
            else if (id == R.id.navigation_you) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
