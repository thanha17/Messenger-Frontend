package com.social.messenger.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.social.messenger.models.User;
import com.social.messenger.repository.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    private MutableLiveData<List<User>> suggestedFriends = new MutableLiveData<>();
    private MutableLiveData<List<User>> sentFriends = new MutableLiveData<>();
    private MutableLiveData<List<User>> receivedFriends = new MutableLiveData<>();
    private MutableLiveData<List<User>> searchResults = new MutableLiveData<>();

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    // LiveData cho Suggested Friends
    public LiveData<List<User>> getSuggestedFriends(String currentUserId) {
        userRepository.getSuggestedFriends(currentUserId).enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(retrofit2.Call<List<User>> call, retrofit2.Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestedFriends.setValue(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<User>> call, Throwable t) {
                // Handle failure
            }
        });
        return suggestedFriends;
    }

    // LiveData cho Sent Friends
    public LiveData<List<User>> getSentFriends(String currentUserId) {
        userRepository.getSentFriends(currentUserId).enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(retrofit2.Call<List<User>> call, retrofit2.Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sentFriends.setValue(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<User>> call, Throwable t) {
                // Handle failure
            }
        });
        return sentFriends;
    }

    // LiveData cho Received Friends
    public LiveData<List<User>> getReceivedFriends(String currentUserId) {
        userRepository.getReceivedFriends(currentUserId).enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(retrofit2.Call<List<User>> call, retrofit2.Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    receivedFriends.setValue(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<User>> call, Throwable t) {
                // Handle failure
            }
        });
        return receivedFriends;
    }

    // Method to send Friend Request
    public void sendFriendRequest(String fromId, String toId) {
        userRepository.sendFriendRequest(fromId, toId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                // Handle success
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                // Handle failure
            }
        });
    }

    // Method to accept Friend Request
    public void acceptFriendRequest(String fromId, String toId) {
        userRepository.acceptFriendRequest(fromId, toId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                // Handle success
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                // Handle failure
            }
        });
    }

    // Method to delete Friend Request
    public void deleteFriendRequest(String fromId, String toId) {
        userRepository.deleteFriendRequest(fromId, toId).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                // Handle success
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                // Handle failure
            }
        });
    }

    // Method to search for friends (Sửa theo cách bạn đang làm)
    public void searchFriends(String query) {
//        if (allUsers.getValue() != null) {
//            List<User> result = new ArrayList<>();
//            for (User user : allUsers.getValue()) {
//                if (user.getName().toLowerCase().contains(query.toLowerCase())) {
//                    result.add(user);
//                }
//            }
//            searchResults.setValue(result);
//        }
    }

    // Trả về LiveData đã được cập nhật
    public LiveData<List<User>> getSearchResults() {
        return searchResults;
    }
}
