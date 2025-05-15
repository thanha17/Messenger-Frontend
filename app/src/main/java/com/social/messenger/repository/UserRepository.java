    package com.social.messenger.repository;

    import android.util.Log;

    import com.social.messenger.dto.UploadResponse;
    import com.social.messenger.models.User;
    import com.social.messenger.network.ApiService;
    import com.social.messenger.network.RetrofitClient;
    import com.social.messenger.ui.ProfileFragment;

    import java.io.File;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    import okhttp3.MediaType;
    import okhttp3.MultipartBody;
    import okhttp3.RequestBody;
    import retrofit2.Call;

    import retrofit2.Callback;
    import retrofit2.Response;


    public class UserRepository {
        private ApiService apiService;
//        private WebSocketClient webSocketClient;

        public UserRepository() {
            apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        }
        public interface UploadCallback {
            void onUploadSuccess(String url);
            void onUploadFailure(String error);
        }
        public interface UserCallback {
            void onUserReceived(User user);
            void onError(String error);
        }

        public Call<User> findUserByEmail(String email) {
            return apiService.findUserByEmail(email);
        }
        public Call<List<User>> getSuggestedFriends(String currentUserId) {
            return apiService.getSuggestedFriends(currentUserId);
        }
        public Call<List<User>> getSentFriends(String currentUserId) {
            return apiService.getSentFriends(currentUserId);
        }
        public Call<List<User>> getReceivedFriends(String currentUserId) {
            return apiService.getReceivedFriends(currentUserId);
        }
        public Call<List<User>> getListFriends(String currentUserId) {
            return apiService.getReceivedFriends(currentUserId);
        }

        public Call<Void> sendFriendRequest(String fromId, String toId) {
            Map<String, String> body = new HashMap<>();
            body.put("user1Id", fromId);
            body.put("user2Id", toId);
            return apiService.sendFriendRequest(body);
        }
        public Call<Void> acceptFriendRequest(String fromId, String toId) {
            Map<String, String> body = new HashMap<>();
            body.put("user1Id", fromId);
            body.put("user2Id", toId);
            return apiService.acceptFriendRequest(body);

        }
        public Call<Void> deleteFriendRequest(String fromId, String toId) {
            Map<String, String> body = new HashMap<>();
            body.put("user1Id", fromId);
            body.put("user2Id", toId);
            return apiService.deleteFriendRequest(body);
        }
        public void updateUser(User user) {
            String avatarPath = user.getAvatarUrl();

            // Nếu có avatar mới (là đường dẫn local đến file)
            if (avatarPath != null && !avatarPath.isEmpty()) {
                File avatarFile = new File(avatarPath);

                if (avatarFile.exists()) {
                    // Upload ảnh lên server
                    uploadMedia(avatarFile, new UploadCallback() {
                        @Override
                        public void onUploadSuccess(String url) {
                            Log.d("UserRepository", "Avatar uploaded: " + url);
                            user.setAvatarUrl(url); // Gán URL ảnh từ server trả về
                            updateUserToServer(user); // Gửi user đã có URL ảnh lên server
                        }

                        @Override
                        public void onUploadFailure(String error) {
                            Log.e("UserRepository", "Avatar upload failed: " + error);
                            // Vẫn cập nhật user, nhưng giữ nguyên avatarUrl ban đầu (có thể là local path)
                            updateUserToServer(user);
                        }
                    });
                } else {
                    Log.e("UserRepository", "Avatar file does not exist: " + avatarPath);
                    updateUserToServer(user); // Không có file, vẫn cập nhật user
                }
            } else {
                // Không có avatar, chỉ cập nhật user
                updateUserToServer(user);
            }
        }

        private void updateUserToServer(User user) {
            Call<User> call = apiService.updateUser(user.getId(), user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Log.d("UserRepository", "User updated successfully: " + response.body());
                    } else {
                        Log.e("UserRepository", "Failed to update user: " + response.code() + " - " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("UserRepository", "Network error while updating user: " + t.getMessage());
                }
            });
        }


        private void uploadMedia(File mediaFile, UploadCallback callback) {
            String mimeType;
            String name = mediaFile.getName().toLowerCase();

            if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                mimeType = "image/jpeg";
            } else if (name.endsWith(".png")) {
                mimeType = "image/png";
            } else if (name.endsWith(".mp4")) {
                mimeType = "video/mp4";
            } else {
                mimeType = "application/octet-stream";
            }

            RequestBody requestBody = RequestBody.create(mediaFile, MediaType.parse(mimeType));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", mediaFile.getName(), requestBody);

            apiService.uploadMedia(body).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onUploadSuccess(response.body().getUrl());
                    } else {
                        callback.onUploadFailure("Upload failed: code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    callback.onUploadFailure    ("Upload error: " + t.getMessage());
                }
            });
        }
        public void getUserById(String id, UserCallback callback) {
            apiService.getUserById(id).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onUserReceived(response.body());
                    } else {
                        callback.onError("Không tìm thấy user hoặc lỗi server");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    callback.onError("Lỗi kết nối: " + t.getMessage());
                }
            });
        }


    }
