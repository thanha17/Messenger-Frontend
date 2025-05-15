package com.social.messenger.network;

import com.social.messenger.dto.ChatItem;
import com.social.messenger.dto.GroupDTO;
import com.social.messenger.dto.UploadResponse;
import com.social.messenger.dto.userlogin;
import com.social.messenger.models.Group;
import com.social.messenger.models.GroupMessage;
import com.social.messenger.models.Message;
import com.social.messenger.models.MessageStatus;
import com.social.messenger.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    //Authprivate String email;
    //    private String password;
    @POST("/auth/login")
    Call<ResponseBody> login(@Body userlogin user);
    @POST("/auth/register")
    Call<ResponseBody> register(@Body  User user);

    //User
// Sử dụng @Query để truyền tham số qua query string
    @GET("/api/users/findemail")
    Call<User> findUserByEmail(@Query("email") String email);

    @PUT("/api/users/{id}")
    Call<User> updateUser(@Path("id") String userId, @Body User user);

    @GET("/api/users/{id}")
    Call<User> getUserById(@Path("id") String id);

    //friendships

    @GET("/friendship/get-suggested-friends")
    Call<List<User>> getSuggestedFriends(@Query("userID") String currentUserId);
    @GET("/friendship/get-sent-friends")
    Call<List<User>> getSentFriends(@Query("userID") String currentUserId);
    @GET("/friendship/get-received-friends")
    Call<List<User>> getReceivedFriends(@Query("userID") String currentUserId);
    @POST("/friendship/sendRequest")
    Call<Void> sendFriendRequest(@Body Map<String, String> body);
    @POST("/friendship/acceptRequest")
    Call<Void> acceptFriendRequest(@Body Map<String, String> body);
    @POST("/friendship/deleteRequest")
    Call<Void> deleteFriendRequest(@Body Map<String, String> body);
    @GET("/friendship/get-list-friend")
    Call<List<User>> getListFriends(@Query("userID") String currentUserId);



    //message
    // Gửi tin nhắn
    @POST("/api/messages/send")
    Call<Message> sendMessage(@Body Message message);
    @Multipart
    @POST("/api/upload")
    Call<UploadResponse> uploadMedia(@Part MultipartBody.Part file);

    // Lấy conversation giữa 2 user (user1Id và user2Id được truyền qua body dưới dạng Map<String, String>)
    @POST("/api/messages/conversation")
    Call<List<Message>> getConversation(@Body Map<String, String> request);

    // Update trạng thái tin nhắn dựa vào messageId
    // (status sẽ được truyền qua query parameter)
    @PUT("/api/messages/update-status/{messageId}")
    Call<ResponseBody> updateMessageStatus(@Path("messageId") String messageId,
                                           @Query("status") MessageStatus status);
    // Update trạng thái tin nhắn
    @POST("/api/messages/mark-read")
    Call<Void> markMessagesAsRead(
            @Query("senderId") String senderId,
            @Query("receiverId") String receiverId
    );
    @GET("/api/messages/list-chat/{userId}")
    Call<List<ChatItem>> getChatItems(@Path("userId") String userId);
    // Lấy danh sách tin nhắn (GET /api/messages)
    @GET("/api/messages")
    Call<List<Message>> getMessages();


    //Group
    // Tạo mới 1 group (POST /api/groups)
    @POST("/api/groups")
    Call<Group> createGroup(@Body Group group);

    // Lấy tất cả các group (GET /api/groups)
    @GET("/api/groups/get-all-group/{userId}")
    Call<List<GroupDTO>> getAllGroups(@Path("userId") String userId);

    // Lấy thông tin group theo id (GET /api/groups/{id})
    @GET("/api/groups/{id}")
    Call<Group> getGroupById(@Path("id") String id);

    // Gửi tin nhắn nhóm
    @POST("/api/group-messages")
    Call<GroupMessage> sendGroupMessage(@Body GroupMessage message);

    // Lấy danh sách tin nhắn theo groupId
    @GET("/api/group-messages/{groupId}")
    Call<List<GroupMessage>> getGroupConversation(@Path("groupId") String groupId);

}
