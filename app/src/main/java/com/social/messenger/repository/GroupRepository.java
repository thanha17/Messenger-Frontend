package com.social.messenger.repository;

import android.util.Log;

import com.social.messenger.dto.GroupDTO;
import com.social.messenger.models.Group;
import com.social.messenger.network.ApiService;
import com.social.messenger.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRepository {
    private ApiService apiService;

    public GroupRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public Call<List<GroupDTO>> getAllGroups(String userId) {
        return apiService.getAllGroups(userId);
    }
    public void createGroup(Group group) {

        Call<Group> call = apiService.createGroup(group);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Group createdGroup = response.body();
                    Log.d("CreateGroup", "Group created: " + createdGroup.getName());
                    // TODO: gọi callback về UI hoặc chuyển màn hình
                } else {
                    Log.e("CreateGroup", "Create group failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.e("CreateGroup", "Error: " + t.getMessage());
            }
        });
    }
}
