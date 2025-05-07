package com.social.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.social.messenger.R;

public class ChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageView imageViewAttach = findViewById(R.id.image_view_attach);
        imageViewAttach.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.getMenu().add("Gửi ảnh");
            popupMenu.getMenu().add("Picture");

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getTitle().equals("picture")) {
                    // Xử lý gửi ảnh
                } else if (menuItem.getTitle().equals("video")) {
                    // Xử lý picture
                }
                return true;
            });

            popupMenu.show();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hiển thị nút "Back" trên Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        // Xử lý sự kiện nhấn nút Back
        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed(); // Quay lại màn hình trước đó
        });

        ImageView imageViewAvatar = findViewById(R.id.image_view_chat_partner_avatar);
        TextView textViewName = findViewById(R.id.text_view_chat_partner_name);

        View.OnClickListener openProfileListener = view -> {
            Intent intent = new Intent(ChatActivity.this, UserProfileActivity.class);
            intent.putExtra("USER_ID", "user123"); // Truyền ID của đối phương
            startActivity(intent);
        };

        imageViewAvatar.setOnClickListener(openProfileListener);
        textViewName.setOnClickListener(openProfileListener);

    }
}
