package com.social.messenger.ui;

import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VisibleActivityInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.social.messenger.R;
import com.social.messenger.StompClientManager;
import com.social.messenger.adapter.ChatAdapter;
import com.social.messenger.dto.ChatItem;
import com.social.messenger.dto.GroupDTO;
import com.social.messenger.models.Message;
import com.social.messenger.models.User;
import com.social.messenger.repository.ChatRepository;
import com.social.messenger.repository.GroupRepository;
import com.social.messenger.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesFragment extends Fragment {

    private View layoutPersonal, layoutGroup;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewPersonal, recyclerViewGroup;
    private View emptyStatePersonal, emptyStateGroup;
    private ChatAdapter personalAdapter, groupAdapter;
    private Button btnAddFriend;
    private ImageView btnCreateGroup;
    private ChatRepository chatRepository;
    private GroupRepository groupRepository;
    public static String currentUserId;

    public MessagesFragment() {}


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        chatRepository = new ChatRepository();
        groupRepository = new GroupRepository();
        currentUserId = SharedPrefManager.getInstance(requireContext()).getUserId();

        layoutPersonal = view.findViewById(R.id.layout_personal);
        layoutGroup = view.findViewById(R.id.layout_group);
        tabLayout = view.findViewById(R.id.tab_layout_messages);

        recyclerViewPersonal = layoutPersonal.findViewById(R.id.recycler_view_messages);
        emptyStatePersonal = layoutPersonal.findViewById(R.id.layout_empty_state);
        emptyStatePersonal.setVisibility(View.VISIBLE);

        btnAddFriend = emptyStatePersonal.findViewById(R.id.button_add_new_message);
        btnCreateGroup = view.findViewById(R.id.image_view_add_group);

        recyclerViewGroup = layoutGroup.findViewById(R.id.recycler_view_group_messages);
        emptyStateGroup = layoutGroup.findViewById(R.id.layout_group_empty_state);
        emptyStateGroup.setVisibility(View.VISIBLE);

        recyclerViewPersonal.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewGroup.setLayoutManager(new LinearLayoutManager(requireContext()));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showPersonalMessages();
                } else {
                    showGroupMessages();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnAddFriend.setOnClickListener(v -> {
            BottomNavigationView navView = requireActivity().findViewById(R.id.bottom_navigation);
            navView.setSelectedItemId(R.id.navigation_new_friends);
        });
        btnCreateGroup.setOnClickListener(v -> {
            chatRepository.getChatItems(currentUserId).enqueue(new Callback<List<ChatItem>>() {
                @Override
                public void onResponse(@NonNull Call<List<ChatItem>> call, @NonNull Response<List<ChatItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ChatItem> chatList = response.body();
                        List<User> users = new ArrayList<>();
                        for (ChatItem chatItem : chatList) {
                            users.add(chatItem.getUser());
                        }
                        if (isAdded()) {
                            CreateGroupBottomSheetFragment fragment = new CreateGroupBottomSheetFragment(users);
                            fragment.show(requireActivity().getSupportFragmentManager(), "CreateGroupBottomSheetFragment");

                        } else {
                            Log.w("MessagesFragment", "Fragment not attached, skip UI update");
                        }
                    } else {
                        Log.e("ChatDebug", "Lỗi khi nhận dữ liệu: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ChatItem>> call, @NonNull Throwable t) {
                    Log.e("ChatDebug", "API thất bại: " + t.getMessage());
                }
            });
        });
        // Mặc định tab cá nhân
        showPersonalMessages();
        return view;
    }

    private void showPersonalMessages() {
        layoutPersonal.setVisibility(View.VISIBLE);
        layoutGroup.setVisibility(View.GONE);

        chatRepository.getChatItems(currentUserId).enqueue(new Callback<List<ChatItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatItem>> call, @NonNull Response<List<ChatItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatItem> chatList = response.body();
                    Collections.sort(chatList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
                    if (isAdded()) {
                        initRecyclerView(recyclerViewPersonal, emptyStatePersonal, chatList, null,true);
                    } else {
                        Log.w("MessagesFragment", "Fragment not attached, skip UI update");
                    }
                } else {
                    Log.e("ChatDebug", "Lỗi khi nhận dữ liệu: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatItem>> call, @NonNull Throwable t) {
                Log.e("ChatDebug", "API thất bại: " + t.getMessage());
            }
        });
    }

    private void showGroupMessages() {
        layoutGroup.setVisibility(View.VISIBLE);
        layoutPersonal.setVisibility(View.GONE);

        groupRepository.getAllGroups(currentUserId).enqueue(new Callback<List<GroupDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<GroupDTO>> call, @NonNull Response<List<GroupDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GroupDTO> groupDTOList = response.body();
                    Collections.sort(groupDTOList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
                    if (isAdded()) {
                        initRecyclerView(recyclerViewGroup, emptyStateGroup,null, groupDTOList, false);
                    } else {
                        Log.w("MessagesFragment", "Fragment not attached, skip UI update");
                    }
                } else {
                    Log.e("ChatDebug", "Lỗi khi nhận dữ liệu: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GroupDTO>> call, @NonNull Throwable t) {
                Log.e("ChatDebug", "API thất bại: " + t.getMessage());
            }
        });
    }

    private void initRecyclerView(RecyclerView recyclerView, View emptyState, List<ChatItem> items, List<GroupDTO> itemsGroup, boolean isPersonal) {
        if (!isAdded()) return;

        recyclerView.setVisibility(View.VISIBLE);
        ChatAdapter adapter = new ChatAdapter();
        adapter.setContext(requireContext());
        adapter.setPersonal(isPersonal);
        recyclerView.setAdapter(adapter);

        if (isPersonal) {
            if (items != null && !items.isEmpty()) {
                adapter.setChatList(items);
                emptyState.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.VISIBLE);
            }
            personalAdapter = adapter;
            layoutGroup.findViewById(R.id.layout_group_empty_state).setVisibility(View.GONE);
            Log.d("MessagesFragment", "Personal adapter set");
        } else {
            if (itemsGroup != null && !itemsGroup.isEmpty()) {
                adapter.setGroupDTOList(itemsGroup);
                emptyState.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.VISIBLE);
            }
            groupAdapter = adapter;
            layoutPersonal.findViewById(R.id.layout_empty_state).setVisibility(View.GONE);
            Log.d("MessagesFragment", "Group adapter set");
        }
    }


    private List<ChatItem> loadGroupChats() {
        return new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            StompClientManager.getInstance(requireContext()).setFriendRequestListener(request ->
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Lời mời từ " + request.getFromUserId(), Toast.LENGTH_SHORT).show()
                    )
            );
        }
        showPersonalMessages();
        StompClientManager.getInstance(requireContext()).setMessageListener(new StompClientManager.MessageListener() {
            @Override
            public void onMessageReceived(Message message) {
                requireActivity().runOnUiThread(() -> {
                    showPersonalMessages();
                    Toast.makeText(requireContext(), "Tin nhắn mới từ " + message.getSenderId() + message.getText(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
