package com.social.messenger.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.social.messenger.R;
import com.social.messenger.adapter.FriendshipAdapter;
import com.social.messenger.models.Group;
import com.social.messenger.models.User ;
import com.social.messenger.repository.GroupRepository;
import com.social.messenger.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateGroupBottomSheetFragment extends BottomSheetDialogFragment {
    private RecyclerView rvFriends;
    private Button btnDoCreateGroup;
    private FriendshipAdapter friendAdapter;
    private List<User> friends;
    private List<String> selectedFriends;             // truyền vào list bạn bè
    private GroupRepository groupRepository = new GroupRepository();
    private Set<String> selectedFriendIds = new HashSet<>();

    public CreateGroupBottomSheetFragment(List<User> friends) {
        this.friends = friends;
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        EditText etGroupName = view.findViewById(R.id.etGroupName);
        rvFriends = view.findViewById(R.id.rvFriends);
        btnDoCreateGroup = view.findViewById(R.id.btnDoCreateGroup);

        selectedFriends = new ArrayList<>();

        // Thiết lập RecyclerView
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAdapter = new FriendshipAdapter(friends,"Thêm",selectedFriends,user -> {
            if (selectedFriends.contains(user.getId())) {
                selectedFriends.remove(user.getId());
                Log.d("CreateGroupBottomSheetFragment", "User removed: " + user.getFullName());
            } else {
                selectedFriends.add(user.getId());
            }
            friendAdapter.notifyItemChanged(friends.indexOf(user));
        });
        rvFriends.setAdapter(friendAdapter);

        // Xử lý click Create Group
        btnDoCreateGroup.setOnClickListener(v -> {
            if (selectedFriends.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một bạn", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: gọi API tạo nhóm với selectedFriendIds
            selectedFriends.add(SharedPrefManager.getInstance(getContext()).getUserId());
            Group group = new Group();
            group.setName(etGroupName.getText().toString());
            group.setMemberIds(selectedFriends);
            groupRepository.createGroup(group);
            dismiss();
        });

        return view;
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bsd = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bsd.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.66);
                bottomSheet.getLayoutParams().height = height;
                bottomSheet.requestLayout();
            }
        });
        return dialog;
    }
}
