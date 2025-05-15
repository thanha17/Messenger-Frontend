package com.social.messenger.repository;

import com.social.messenger.dto.userlogin;
import com.social.messenger.models.User;
import com.social.messenger.network.ApiService;
import com.social.messenger.network.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AuthRepository {

    private ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    // Đăng nhập người dùng
    public Call<ResponseBody> loginUser(userlogin user) {
        return apiService.login(user);
    }
    // Đăng ký người dùng
    public Call<ResponseBody> registerUser(User user) {
        return apiService.register(user);
    }
}
