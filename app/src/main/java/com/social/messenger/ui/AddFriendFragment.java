package com.social.messenger.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.social.messenger.R;

import com.social.messenger.StompClientManager;
import com.social.messenger.adapter.FriendshipAdapter;
import com.social.messenger.models.FriendRequest;
import com.social.messenger.models.Message;
import com.social.messenger.models.User;
import com.social.messenger.repository.UserRepository;
import com.social.messenger.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendFragment extends Fragment {


    private RecyclerView recyclerView;
    private FriendshipAdapter adapter;
    private String currentUserId;
    private UserRepository userRepository;
    private TabLayout tabLayout;
    private List<User> suggestedFriends, sentRequests, receivedRequests;
    private EditText searchEditText;
    private List<User> stase;
    private Context context; // Store the context
    private int currentTabIndex = 0;
    private View layoutSuggested, layoutsent, layoutReceived;

    public AddFriendFragment() {
        // Bắt buộc phải có constructor rỗng
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context; // Get context when fragment is attached
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set listener khi fragment active
        StompClientManager.getInstance(requireContext()).setFriendRequestListener(new StompClientManager.FriendRequestListener() {
            @Override
            public void onFriendRequestReceived(FriendRequest request) {
                requireActivity().runOnUiThread(() -> {

                    Toast.makeText(requireContext(), "Lời mời từ " + request.getFromUserId(), Toast.LENGTH_SHORT).show();
                    reLoad();

                });
            }

        });
        StompClientManager.getInstance(requireContext()).setMessageListener(new StompClientManager.MessageListener() {
            @Override
            public void onMessageReceived(Message message) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Tin nhắn mới từ " + message.getSenderId() + message.getText(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove listener khi fragment không active
        StompClientManager.getInstance(context).setFriendRequestListener(null); // Use stored context
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        // Ánh xạ view
        searchEditText = view.findViewById(R.id.edit_text_search);
        recyclerView = view.findViewById(R.id.recycler_view_friend_suggestions);
        tabLayout = view.findViewById(R.id.tab_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        currentUserId = SharedPrefManager.getInstance(requireContext()).getUserId();

        switchtab();
        fetchSuggestedFriends();



        // Tìm kiếm
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFriends(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void switchtab() {
        // Hiển thị tab đầu tiên

        // Xử lý khi chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabIndex = tab.getPosition();
                reLoad();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    private void reLoad() {
        if (currentTabIndex == 0) {
            fetchSuggestedFriends();
        } else if (currentTabIndex == 1) {
            fetchSentRequests();
        } else if (currentTabIndex == 2) {
            fetchReceivedRequests();
        }
    }

    private void fetchSuggestedFriends() {
        userRepository = new UserRepository();
        userRepository.getSuggestedFriends(currentUserId)
                .enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stase = response.body();
                    adapter = new FriendshipAdapter(stase,"Kết bạn", user -> sendFriendRequest(currentUserId, user.getId()));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void fetchSentRequests() {
        userRepository = new UserRepository();
        userRepository.getSentFriends(currentUserId).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stase = response.body();
                    adapter = new FriendshipAdapter(stase,"Hủy kết bạn", user -> deleteFriendRequest(currentUserId, user.getId()));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void fetchReceivedRequests() {
        userRepository = new UserRepository();
        userRepository.getReceivedFriends(currentUserId).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stase = response.body();
                    adapter = new FriendshipAdapter(stase,"Chấp nhận", user -> acceptFriendRequest(currentUserId,user.getId()), user -> deleteFriendRequest(currentUserId, user.getId()));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void sendFriendRequest(String fromId, String toId) {
        userRepository.sendFriendRequest(fromId, toId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Friend request sent", Toast.LENGTH_SHORT).show();
                    // Cập nhật lại danh sách nếu cần
                    // Gửi lời mời qua WebSocket
                    StompClientManager.getInstance(requireContext().getApplicationContext())
                            .sendFriendRequest(toId);

                    // Use stored context
                    fetchSuggestedFriends();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void acceptFriendRequest(String fromId, String toId) {
        userRepository.acceptFriendRequest(fromId, toId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "added friend", Toast.LENGTH_SHORT).show();
                    // Cập nhật lại danh sách nếu cần
                    fetchReceivedRequests();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void deleteFriendRequest(String fromId, String toId) {
        userRepository.deleteFriendRequest(fromId, toId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Friend request sent", Toast.LENGTH_SHORT).show();
                    // Cập nhật lại danh sách nếu cần
                    fetchSentRequests();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void searchFriends(String query) {
        List<User> filteredList = new ArrayList<>();
        for (User user : stase) {
            if (user.getFullName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateData(filteredList); // Giả sử adapter có hàm này
    }

}
