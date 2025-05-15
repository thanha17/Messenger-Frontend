package com.social.messenger.repository;

import android.util.Log;

import com.social.messenger.dto.ChatItem;
import com.social.messenger.dto.UploadResponse;
import com.social.messenger.models.GroupMessage;
import com.social.messenger.models.Message;
import com.social.messenger.network.ApiService;
import com.social.messenger.network.RetrofitClient;

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

public class ChatRepository {

    private ApiService apiService;

    public ChatRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    // Lấy danh sách chat
    public Call<List<ChatItem>> getChatItems(String currentUserId) {
        return apiService.getChatItems(currentUserId);
    }

    // Gửi tin nhắn
    public void sendMessage(Message message) {
        // Kiểm tra nếu có ảnh/video, thực hiện upload
        if (message.getFileUrl()!=null){
            File file = new File(message.getFileUrl());
            if (file != null && file.exists()) {
                uploadMedia(file, new UploadCallback() {
                    @Override
                    public void onUploadSuccess(String url) {
                        Log.d("ChatRepository", "Upload success: " + url);
                        message.setFileUrl(url); // Set URL vào tin nhắn
                        sendMessageToServer(message); // Gửi tin nhắn với URL
                    }

                    @Override
                    public void onUploadFailure(String error) {
                        Log.e("ChatRepository", "Upload failed: " + error);
                        //sendMessageToServer(message); // Gửi tin nhắn mặc dù upload thất bại
                    }
                });
            }
        }
        else {
            // Nếu không có ảnh/video, gửi tin nhắn ngay lập tức
            sendMessageToServer(message);
        }
    }
    public void sendGroupMessage(GroupMessage gmessage) {
        // Kiểm tra nếu có ảnh/video, thực hiện upload
        if (gmessage.getFileUrl()!=null){
            File file = new File(gmessage.getFileUrl());
            if (file != null && file.exists()) {
                uploadMedia(file, new UploadCallback() {
                    @Override
                    public void onUploadSuccess(String url) {

                        gmessage.setFileUrl(url); // Set URL vào tin nhắn
                        sendGroupMessageToServer(gmessage); // Gửi tin nhắn với URL
                    }

                    @Override
                    public void onUploadFailure(String error) {
                        Log.e("ChatRepository", "Upload failed: " + error);
                        //sendMessageToServer(message); // Gửi tin nhắn mặc dù upload thất bại
                    }
                });
            }
        }
        else {
            // Nếu không có ảnh/video, gửi tin nhắn ngay lập tức
            sendGroupMessageToServer(gmessage);
        }
    }

    // Gửi tin nhắn tới backend
    private void sendMessageToServer(Message message) {
        Call<Message> call = apiService.sendMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful()) {
                    Log.d("ChatRepository", "Message sent successfully: " + response.body());
                } else {
                    Log.e("ChatRepository", "Failed to send message: " + response.code() + " - " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("ChatRepository", "Network error while sending message: " + t.getMessage());
            }
        });
    }
    private void sendGroupMessageToServer(GroupMessage message) {
        Call<GroupMessage> call = apiService.sendGroupMessage(message);
        call.enqueue(new Callback<GroupMessage>() {
            @Override
            public void onResponse(Call<GroupMessage> call, Response<GroupMessage> response) {
                if (response.isSuccessful()) {
                    Log.d("ChatRepository", "Message sent successfully: " + response.body());
                } else {
                    Log.e("ChatRepository", "Failed to send GroupMessage: " + response.code() + " - " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GroupMessage> call, Throwable t) {
                Log.e("ChatRepository", "Network error while sending message: " + t.getMessage());
            }
        });
    }

    // Phương thức upload ảnh/video
    private void uploadMedia(File mediaFile, UploadCallback callback) {
        String mimeType;
        if (mediaFile.getName().endsWith(".jpg") || mediaFile.getName().endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (mediaFile.getName().endsWith(".png")) {
            mimeType = "image/png";
        } else if (mediaFile.getName().endsWith(".mp4")) {
            mimeType = "video/mp4";
        } else {
            mimeType = "application/octet-stream"; // default fallback
        }

        RequestBody requestBody = RequestBody.create(mediaFile, MediaType.parse(mimeType));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", mediaFile.getName(), requestBody);


        Call<UploadResponse> call = apiService.uploadMedia(body); // Gọi API upload file
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String url = response.body().getUrl(); // Backend trả về URL của file đã upload
                    callback.onUploadSuccess(url);
                } else {
                    callback.onUploadFailure("Upload failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                callback.onUploadFailure("Upload failed: " + t.getMessage());
            }
        });
    }

    // Lấy danh sách tin nhắn
    public Call<List<Message>> getConversation(String fromId, String toId) {
        Map<String, String> body = new HashMap<>();
        body.put("user1Id", fromId);
        body.put("user2Id", toId);
        return apiService.getConversation(body);
    }
    public Call<List<GroupMessage>> getGroupConversation(String groupID) {
        Log.d("ChatRepository", "getGroupConversation called with groupID: " + groupID);
        return apiService.getGroupConversation(groupID);
    }

    // Đăng nhập người dùng
    // ... (Các phương thức khác của repository)

    // UploadCallback interface sẽ được định nghĩa ở đây (nội bộ trong class)
    private interface UploadCallback {
        void onUploadSuccess(String url);  // Gọi khi upload thành công và có URL
        void onUploadFailure(String error);  // Gọi khi có lỗi trong quá trình upload
    }
    public void markMessagesAsRead(String senderId, String receiverId) {
        apiService.markMessagesAsRead(senderId, receiverId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ChatActivity", "Marked messages as READ");
                } else {
                    Log.e("ChatActivity", "Failed to mark as read: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ChatActivity", "API error: " + t.getMessage());
            }
        });
    }
}
