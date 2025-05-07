package com.social.messenger.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.ImageView;
import com.social.messenger.R;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView imageViewAvatar;
    private TextView textViewName, textViewBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Ánh xạ view
        imageViewAvatar = findViewById(R.id.image_view_profile_avatar);
        textViewName = findViewById(R.id.text_view_profile_name);
        textViewBio = findViewById(R.id.text_view_profile_bio);

        // Giả lập dữ liệu người dùng
        String userId = getIntent().getStringExtra("USER_ID");
        textViewName.setText("Noelle Norman"); // Sau này lấy từ API hoặc DB
        textViewBio.setText("Lập trình viên, yêu thích công nghệ! 🚀");
    }
}
