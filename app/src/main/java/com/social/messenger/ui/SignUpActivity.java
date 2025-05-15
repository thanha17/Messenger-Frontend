package com.social.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.social.messenger.R;
import com.social.messenger.models.User;
import com.social.messenger.repository.AuthRepository;
import com.social.messenger.repository.UserRepository;
import com.social.messenger.utils.SharedPrefManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
//    Button btnBack, btnSignUp;
    private Button btnSignUp;
    private EditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    private AuthRepository authRepository;
    private UserRepository userRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = findViewById(R.id.button_finish_signup);
        edtFullName = findViewById(R.id.edt_full_name);
        edtEmail = findViewById(R.id.edt_your_email);
        edtPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);

        authRepository = new AuthRepository();
        userRepository = new UserRepository();

        btnSignUp = findViewById(R.id.button_finish_signup);
        btnSignUp.setOnClickListener(v -> {
            String FullName = edtFullName.getText().toString().trim();
            String Email = edtEmail.getText().toString().trim();
            String Password = edtPassword.getText().toString().trim();
            String ConfirmPassword = edtConfirmPassword.getText().toString().trim();

            if (FullName.isEmpty() || Email.isEmpty() || Password.isEmpty() || ConfirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Password.equals(ConfirmPassword)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }
            User user = new User(FullName, Email, Password);
            authRepository.registerUser(user).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        userRepository.findUserByEmail(user.getEmail()).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    User fullUser = response.body();
                                    // Lưu user đầy đủ vào SharedPref hoặc global

                                    Toast.makeText(SignUpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Không lấy được thông tin người dùng!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(SignUpActivity.this, "Lỗi kết nối khi lấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }
}