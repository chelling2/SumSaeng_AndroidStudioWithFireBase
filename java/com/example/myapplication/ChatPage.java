package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatPage extends Fragment {

    private DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private ArrayList<String> chatRoomList;
    private ChatListAdapter adapter; // 使用 ChatListAdapter
    private RecyclerView recyclerView;
    private ArrayList<DataSnapshot> chatRoomSnapshots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chats").child("chats");
        userReference = FirebaseDatabase.getInstance().getReference().child("application").child("UserAccount");

        chatRoomList = new ArrayList<>();
        chatRoomSnapshots = new ArrayList<>();

// 初始化 ChatListAdapter
        adapter = new ChatListAdapter(requireContext(), chatRoomList, chatRoomSnapshots, currentUser);


        recyclerView = view.findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatRoomList.clear();
                chatRoomSnapshots.clear();

                for (DataSnapshot chatRoomSnapshot : dataSnapshot.getChildren()) {
                    String chatRoomId = chatRoomSnapshot.getKey();

                    if (chatRoomId.contains(currentUser.getUid())) {
                        ArrayList<DataSnapshot> messageSnapshots = new ArrayList<>();

                        // 채팅방 내 메시지들을 timestamp를 기준으로 정렬하여 messageSnapshots 리스트에 추가
                        for (DataSnapshot messageSnapshot : chatRoomSnapshot.getChildren()) {
                            messageSnapshots.add(messageSnapshot);
                        }

                        if (!messageSnapshots.isEmpty()) {
                            // messageSnapshots 리스트를 timestamp 순으로 정렬
                            messageSnapshots.sort((o1, o2) -> {
                                Long timestamp1 = o1.child("timestamp").getValue(Long.class);
                                Long timestamp2 = o2.child("timestamp").getValue(Long.class);
                                return Long.compare(timestamp2, timestamp1);  // 역순으로 정렬
                            });

                            DataSnapshot lastMessageSnapshot = messageSnapshots.get(0);  // 가장 최근 메시지를 가져옴
                            String senderId = lastMessageSnapshot.child("senderId").getValue(String.class);
                            String recipientId = lastMessageSnapshot.child("recipientId").getValue(String.class);

                            // 현재 사용자가 메시지를 보냈는지 받았는지 확인하여 상대방의 ID 가져오기
                            String otherUserId = currentUser.getUid().equals(senderId) ? recipientId : senderId;

                            fetchUserNickname(otherUserId, chatRoomSnapshot);
                        }
                    }
                    // 更新 adapter 的資料
                    adapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });



        return view;
    }

    private void fetchUserNickname(String userId, DataSnapshot chatRoomSnapshot) {
        userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nickname = dataSnapshot.child("nickname").getValue(String.class);
                if (nickname == null) {
                    nickname = "Unknown User";
                }

                DataSnapshot lastMessageSnapshot = getLastMessageSnapshot(chatRoomSnapshot);
                String lastMessage = getLastMessageContent(lastMessageSnapshot);

                String displayInfo = nickname + "@" + lastMessage;
                chatRoomList.add(displayInfo);
                chatRoomSnapshots.add(chatRoomSnapshot);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private DataSnapshot getLastMessageSnapshot(DataSnapshot chatRoomSnapshot) {
        ArrayList<DataSnapshot> messageSnapshots = new ArrayList<>();
        for (DataSnapshot messageSnapshot : chatRoomSnapshot.getChildren()) {
            messageSnapshots.add(messageSnapshot);
        }

        if (!messageSnapshots.isEmpty()) {
            messageSnapshots.sort((o1, o2) -> {
                Long timestamp1 = o1.child("timestamp").getValue(Long.class);
                Long timestamp2 = o2.child("timestamp").getValue(Long.class);
                return Long.compare(timestamp2, timestamp1);  // 역순으로 정렬
            });

            return messageSnapshots.get(0);
        }
        return null;
    }

    private String getLastMessageContent(DataSnapshot lastMessageSnapshot) {
        if (lastMessageSnapshot != null) {
            String messageType = lastMessageSnapshot.child("type").getValue(String.class);
            if (messageType != null && messageType.equals("image")) {
                return "(사진)";
            } else {
                return lastMessageSnapshot.child("message").getValue(String.class);
            }
        }
        return "";
    }

//    @NonNull
//    private String generateChatRoomId(String uid1, String uid2) {
//        if (uid1 == null) {
//            uid1 = "";
//        }
//        if (uid2 == null) {
//            uid2 = "";
//        }
//
//        String[] sortedUids = {uid1, uid2};
//        Arrays.sort(sortedUids);
//
//        return "chats/" + sortedUids[0] + "_" + sortedUids[1];
//    }
}
