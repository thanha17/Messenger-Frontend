package com.social.messenger.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.social.messenger.R;
import com.social.messenger.adapter.ChatAdapter;
import com.social.messenger.models.ChatMessage;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {

    private View layoutPersonal;
    private View layoutGroup;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewMessages;
    private ArrayList<ChatMessage> chatList;
    private ChatAdapter chatAdapter;

    public MessagesFragment() {
        // Bắt buộc phải có constructor rỗng cho Fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        layoutPersonal = view.findViewById(R.id.layout_personal);
        layoutGroup = view.findViewById(R.id.layout_group);
        tabLayout = view.findViewById(R.id.tab_layout_messages);

        layoutPersonal.setVisibility(View.VISIBLE);
        layoutGroup.setVisibility(View.GONE);

        showPersonalMessages();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    layoutPersonal.setVisibility(View.VISIBLE);
                    layoutGroup.setVisibility(View.GONE);
                    showPersonalMessages();
                } else {
                    layoutPersonal.setVisibility(View.GONE);
                    layoutGroup.setVisibility(View.VISIBLE);
                    showGroupMessages();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void showPersonalMessages() {
        recyclerViewMessages = layoutPersonal.findViewById(R.id.recycler_view_messages);
        View layoutEmptyState = layoutPersonal.findViewById(R.id.layout_empty_state);

        chatList = new ArrayList<>();
        chatList.add(new ChatMessage("1", "Viona", "Hello", "1 min", 2, ""));
        chatList.add(new ChatMessage("2", "John", "Let's meet", "5 min", 0, ""));
        chatList.add(new ChatMessage("3", "Emma", "Call me", "10 min", 5, ""));

        if (!chatList.isEmpty()) {
            recyclerViewMessages.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);

            recyclerViewMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
            chatAdapter = new ChatAdapter(requireContext(), chatList);
            recyclerViewMessages.setAdapter(chatAdapter);
        } else {
            recyclerViewMessages.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
    }

    private void showGroupMessages() {
        RecyclerView recyclerViewGroupMessages = layoutGroup.findViewById(R.id.recycler_view_group_messages);
        View layoutGroupEmptyState = layoutGroup.findViewById(R.id.layout_group_empty_state);

        ArrayList<ChatMessage> groupChatList = new ArrayList<>();
        groupChatList.add(new ChatMessage("4", "Family", "Dinner 7PM?", "3 min", 1, ""));
        groupChatList.add(new ChatMessage("5", "Work Team", "Project update?", "15 min", 0, ""));

        if (!groupChatList.isEmpty()) {
            recyclerViewGroupMessages.setVisibility(View.VISIBLE);
            layoutGroupEmptyState.setVisibility(View.GONE);

            recyclerViewGroupMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewGroupMessages.setAdapter(new ChatAdapter(requireContext(), groupChatList));
        } else {
            recyclerViewGroupMessages.setVisibility(View.GONE);
            layoutGroupEmptyState.setVisibility(View.VISIBLE);
        }
    }
}
