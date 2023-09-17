package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InquireChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private Button back3;
    private TextView sendButton;
    private DatabaseReference databaseReference;
    private List<InquireChatMessage> messageList;
    private InquireAdapter chatAdapter;
    private String currentUserUid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire);

        recyclerView = findViewById(R.id.recycler_chatrooms);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.btn_send);
        back3= findViewById(R.id.back3);


        back3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
        });

        messageList = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
        } else {

        }

        currentUserUid = getCurrentUserUid(); // currentUserUid 초기화

        // Adapter 객체를 먼저 생성하고, 데이터 로드는 Adapter의 생성자에서 호출하도록 수정합니다.
        chatAdapter = new InquireAdapter(messageList, currentUserUid);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // loadData() 메서드를 onCreate()에서 한 번만 호출하도록 수정합니다.
        loadData();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("inquire_messages");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageEditText.setText("");

                }
            }
        });
    }


    public void sendMessage(String message) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            String userEmail = user.getEmail();
            long timestamp = System.currentTimeMillis();

            InquireChatMessage chatMessage = new InquireChatMessage();
            chatMessage.setMessage(message);
            chatMessage.setTimestamp(timestamp);
            chatMessage.setSenderId(uid); // 현재 사용자의 UID를 설정

                chatMessage.setType(InquireChatMessage.MessageType.USER);
                chatMessage.setSenderEmail(userEmail); // 사용자의 이메일 설정
                chatMessage.setTimestamp(timestamp);
                String userPath = userEmail.replace(".", "_");
                databaseReference.child(userPath).push().setValue(chatMessage);
            }

        else {
            // 사용자가 인증되지 않은 경우를 처리
        }
    }

    private String getCurrentUserUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return ""; // 사용자가 로그인하지 않은 경우 빈 문자열 반환하도록 수정
    }

    private boolean isAdminUser(String userId) {
        String adminUserId = "admin";
        return userId.equals(adminUserId);
    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("inquire_messages");
            reference.child(user.getEmail().replace(".", "_"))
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            InquireChatMessage message = snapshot.getValue(InquireChatMessage.class);
                            if (message != null) {
                                messageList.add(message);
                                chatAdapter.notifyDataSetChanged(); // 데이터 변경 시 어댑터 갱신

                                // 추가된 메시지가 있을 때마다 자동 스크롤
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            }
                        }


                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        // onChildChanged, onChildRemoved, onChildMoved 메서드는 필요 없으므로 구현 생략

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Firebase 데이터베이스 작업이 취소되거나 실패한 경우
                            Log.e("FirebaseError", "Firebase 데이터베이스 작업 실패: " + error.getMessage());
                        }
                    });
        }
    }
}
