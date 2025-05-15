package com.social.messenger.ui;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.social.messenger.R;
import com.social.messenger.StompClientManager;
import com.social.messenger.adapter.ImageAdapter;
import com.social.messenger.adapter.MessageAdapter;
import com.social.messenger.models.FriendRequest;
import com.social.messenger.models.GroupMessage;
import com.social.messenger.models.Message;
import com.social.messenger.models.User;
import com.social.messenger.repository.ChatRepository;
import com.social.messenger.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements ImageAdapter.OnMediaSelectedCallback {

    private TextView textViewName;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private final List<Message> messageList = new ArrayList<>();
    private final List<GroupMessage> GroupMessage = new ArrayList<>();
    private ChatRepository chatRepository;
    private View viewemty_state;

    private boolean isGroupChat = false;
    private String currentUserId;
    private String chatId;    // receiverId hoặc groupId
    private String chatName;  // tên người hoặc nhóm
    private User chatPartner; // chỉ dùng nếu là chat cá nhân

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initData();
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupMessageInput();
        setupAttachButton();
        setupProfileNavigation();




        loadConversation(currentUserId, chatId);
    }
    @Override
    public void onResume() {
        super.onResume();
        chatRepository.markMessagesAsRead(chatId,currentUserId);
        StompClientManager.getInstance(this).setMessageListener(new StompClientManager.MessageListener() {
            @Override
            public void onMessageReceived(Message message) {
                ChatActivity.this.runOnUiThread(() -> {
                    loadConversation(currentUserId, chatId);
                    Log.d("ChatActivity", "Tin nhắn cá nhân đến: " + message.getSenderId());
                });
            }
        });
    }


    private void initData() {
        Intent intent = getIntent();
        isGroupChat = intent.getBooleanExtra("IS_GROUP_CHAT", false);
        currentUserId = SharedPrefManager.getInstance(this).getUserId();
        chatRepository = new ChatRepository();

        if (isGroupChat) {
            chatId = intent.getStringExtra("groupId");
            chatName = intent.getStringExtra("groupName");
            Log.d("ChatActivity", chatName);        }
        else {
            chatPartner = (User) intent.getSerializableExtra("user");
            chatId = chatPartner.getId();
            chatName = chatPartner.getFullName();
        }
    }

    private void initViews() {
        textViewName = findViewById(R.id.text_view_chat_partner_name);
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        viewemty_state = findViewById(R.id.empty_state_chat);
        textViewName.setText(chatName);
    }

    private void setupRecyclerView() {
        if (isGroupChat) {
            messageAdapter = new MessageAdapter(GroupMessage,currentUserId,true);
        }
        else {
            messageAdapter = new MessageAdapter(messageList, currentUserId);
        }
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupMessageInput() {
        EditText editTextMessage = findViewById(R.id.edit_text_message);
        ImageView imageViewSend = findViewById(R.id.image_view_send);
        ImageView imageViewMic = findViewById(R.id.image_view_mic);
        ImageView imageViewEmoji = findViewById(R.id.image_view_emoji);

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isEmpty = s.toString().trim().isEmpty();
                imageViewSend.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                imageViewMic.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                imageViewEmoji.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        imageViewSend.setOnClickListener(v -> {
            String text = editTextMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                if (isGroupChat) {
                    GroupMessage groupMessage = new GroupMessage();
                    groupMessage.setSenderId(currentUserId);
                    groupMessage.setType("text");
                    groupMessage.setText(text);
                    groupMessage.setGroupId(chatId);
                    chatRepository.sendGroupMessage(groupMessage);
                }
                else {
                    Message message = new Message();
                    message.setSenderId(currentUserId);
                    message.setType("text");
                    message.setText(text);
                    message.setReceiverId(chatId);
                    chatRepository.sendMessage(message);
                }
                new android.os.Handler().postDelayed(() -> {
                    loadConversation(currentUserId, chatId);
                }, 500); // 5000 ms = 5 giây

            }
            editTextMessage.setText("");
        });
    }

    private void loadConversation(String fromId, String toIdOrGroupId) {
        if (isGroupChat) {
            chatRepository.getGroupConversation(toIdOrGroupId).enqueue(new Callback<List<GroupMessage>>() {
                @Override
                public void onResponse(@NonNull Call<List<GroupMessage>> call, @NonNull Response<List<GroupMessage>> response) {
                    List<GroupMessage> messages = response.body();
                    if (response.isSuccessful() && messages != null) {

                        GroupMessage.clear();
                        GroupMessage.addAll(messages);
                        messageAdapter.notifyDataSetChanged();
                        recyclerViewMessages.scrollToPosition(GroupMessage.size() - 1);
                        viewemty_state.setVisibility(messages.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<GroupMessage>> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "Không thể tải tin nhắn nhóm", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            chatRepository.getConversation(fromId, toIdOrGroupId).enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                    List<Message> messages = response.body();
                    if (response.isSuccessful() && messages != null) {
                        messageList.clear();
                        messageList.addAll(messages);
                        messageAdapter.notifyDataSetChanged();
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                        viewemty_state.setVisibility(messages.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                    else Log.e("ChatDebug", "Lỗi khi nhận dữ liệu: " + response.code());
                }

                @Override
                public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "Không thể tải tin nhắn", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMediaSelectedCallback(String mediaPath, String mediaType) {
        Log.d("ChatActivity", "Media selected: " + mediaPath + ", type: " + mediaType);
        if(isGroupChat){
            GroupMessage groupMessage = new GroupMessage();
            groupMessage.setSenderId(currentUserId);
            groupMessage.setType(mediaType);
            groupMessage.setFileUrl(mediaPath);
            groupMessage.setGroupId(chatId);
            chatRepository.sendGroupMessage(groupMessage);
        }
        else{
            Message message = new Message();
            message.setSenderId(currentUserId);
            message.setType(mediaType);
            message.setFileUrl(mediaPath);
            message.setReceiverId(chatId);
            chatRepository.sendMessage(message);
        }

        loadConversation(currentUserId, chatId);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupAttachButton() {
        ImageView imageViewAttach = findViewById(R.id.image_view_attach);
        imageViewAttach.setOnClickListener(v -> {
            GalleryBottomSheetFragment bottomSheet = new GalleryBottomSheetFragment();
            bottomSheet.show(getSupportFragmentManager(), "GalleryBottomSheet");
        });
    }

    private void setupProfileNavigation() {
        ImageView imageViewAvatar = findViewById(R.id.image_view_chat_partner_avatar);

        if (!isGroupChat && chatPartner != null) {
            textViewName.setOnClickListener(this::openProfile);
            imageViewAvatar.setOnClickListener(this::openProfile);
        }
    }

    private void openProfile(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("USER_ID", chatPartner.getId());
        startActivity(intent);
    }
}
