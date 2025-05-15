package com.social.messenger;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.social.messenger.models.FriendRequest;
import com.social.messenger.models.Message;
import com.social.messenger.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class StompClientManager {

    private static final String TAG = "StompClientManager";
    private static final String SOCKET_URL = "ws://192.168.68.197:8081/ws/websocket";

    private static StompClientManager instance;
    private StompClient stompClient;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final Gson gson = new Gson();
    private Context applicationContext;
    private FriendRequestListener friendRequestListener;

    private Disposable friendRequestSubscription;
    private Disposable messageNotificationSubscription;
    private Disposable privateMessageSubscription;
    private MessageListener messageListener;
    private StompClientManager() {}

    public static synchronized StompClientManager getInstance(Context context) {
        if (instance == null) {
            instance = new StompClientManager();
            instance.applicationContext = context.getApplicationContext();
        }
        return instance;
    }

    private String getAuthToken() {
        return SharedPrefManager.getInstance(applicationContext).getToken();
    }

    private String getCurrentUserId() {
        return SharedPrefManager.getInstance(applicationContext).getUserId();
    }

    public void connect() {
        String authToken = getAuthToken();
        String userId = getCurrentUserId();

        if (authToken == null || userId == null) {
            Log.e(TAG, "Cannot connect: Auth token or user ID is null");
            return;
        }

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + authToken));
        headers.add(new StompHeader("X-CSRF-Token", "your-hardcoded-token-here"));

        if (stompClient != null && stompClient.isConnected()) {
            Log.d(TAG, "Already connected");
            return;
        }

        String url = SOCKET_URL + "?token=" + authToken;
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);

        compositeDisposable.add(
                stompClient.lifecycle()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(event -> {
                            switch (event.getType()) {
                                case OPENED:
                                    Log.d(TAG, "STOMP connection opened");
                                    setupSubscriptions(userId);
                                    break;
                                case ERROR:
                                    Log.e(TAG, "STOMP error", event.getException());
                                    attemptReconnect();
                                    break;
                                case CLOSED:
                                    Log.d(TAG, "STOMP connection closed");
                                    break;
                            }
                        })
        );

        stompClient.connect(headers);
    }

    private void setupSubscriptions(String userId) {
        if ((friendRequestSubscription != null && !friendRequestSubscription.isDisposed()) ||
                (messageNotificationSubscription != null && !messageNotificationSubscription.isDisposed())) {
            Log.d(TAG, "Already subscribed to WebSocket topics");
            return;
        }

        clearSubscriptions(); // Nếu cần reset lại các subscription cũ

        String authToken = getAuthToken();

        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + authToken));
        headers.add(new StompHeader("X-CSRF-Token", "your-hardcoded-token-here"));

        // === 1. Lắng nghe lời mời kết bạn ===
        friendRequestSubscription = stompClient
                .topic("/user/queue/friend-requests")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        stompMessage -> {
                            String payload = stompMessage.getPayload();
                            Log.d(TAG, "Friend request received: " + payload);

                            FriendRequest request = gson.fromJson(payload, FriendRequest.class);

                            if (friendRequestListener != null) {
                                friendRequestListener.onFriendRequestReceived(request);
                            }
                        },
                        throwable -> Log.e(TAG, "Friend request topic error", throwable)
                );
        compositeDisposable.add(friendRequestSubscription);

        // === 2. Lắng nghe tin nhắn mới ===
        messageNotificationSubscription = stompClient
                .topic("/user/queue/receiver-mess")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        stompMessage -> {
                            String payload = stompMessage.getPayload();
                            Log.d(TAG, "Tin nhắn mới đến: " + payload);
                            Message chatMessage = gson.fromJson(payload, Message.class);
                            messageListener.onMessageReceived(chatMessage);

                            // Nếu bạn gửi object JSON:
                            // ChatNotificationMessage msg = gson.fromJson(payload, ChatNotificationMessage.class);
                            // Hiển thị thông báo, badge, v.v.
                        },
                        throwable -> Log.e(TAG, "Message topic error", throwable)
                );
        compositeDisposable.add(messageNotificationSubscription);
    }


    private void attemptReconnect() {
        Log.d(TAG, "Attempting to reconnect in 5 seconds...");
        new Handler().postDelayed(() -> {
            if (!isConnected()) {
                connect();
            }
        }, 5000);
    }

    public void sendFriendRequest(String toUserId) {
        String fromUserId = getCurrentUserId();
        if (fromUserId == null) {
            Log.e(TAG, "Cannot send request: Current user ID is null");
            return;
        }

        JSONObject payload = new JSONObject();
        try {
            payload.put("type", "FRIEND_REQUEST");
            payload.put("fromUserId", fromUserId);
            payload.put("toUserId", toUserId);
            payload.put("message", "Friend request from " + fromUserId);
        } catch (JSONException e) {
            Log.e(TAG, "JSON error", e);
            return;
        }

        String authToken = getAuthToken();
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + authToken));
        headers.add(new StompHeader("X-CSRF-Token", "your-hardcoded-token-here"));

        if (isConnected()) {
            stompClient.send("/app/friend-request", payload.toString())
                    .subscribe(
                            () -> Log.d(TAG, "Friend request sent"),
                            throwable -> Log.e(TAG, "Send error", throwable)
                    );
        } else {
            Log.w(TAG, "STOMP client not connected");
            connect();
        }
    }

    public boolean isConnected() {
        return stompClient != null && stompClient.isConnected();
    }

    private void clearSubscriptions() {
        if (friendRequestSubscription != null && !friendRequestSubscription.isDisposed()) {
            friendRequestSubscription.dispose();
        }
        if (privateMessageSubscription != null && !privateMessageSubscription.isDisposed()) {
            privateMessageSubscription.dispose();
        }
    }

    public void disconnect() {
        clearSubscriptions();
        compositeDisposable.clear();
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }
        Log.d(TAG, "Disconnected and cleared subscriptions");
    }

    public void setFriendRequestListener(FriendRequestListener listener) {
        this.friendRequestListener = listener;
    }

    public interface FriendRequestListener {
        void onFriendRequestReceived(FriendRequest request);
    }


    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public interface MessageListener {
        void onMessageReceived(Message message);
    }
}
