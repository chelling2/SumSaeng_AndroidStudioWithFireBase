package com.example.myapplication;

import android.os.Bundle;
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

public class Inquire_Admin_User_ChatActivity extends AppCompatActivity {

    private EditText adminMessageEditText;
    private Button back5;

    private TextView userEmailTextView,sendButton;
    private RecyclerView recyclerView;
    private InquireAdapter chatAdapter;
    private List<InquireChatMessage> messageList;
    private String currentUserUid;
    private String adminEmail = "admin@example.com"; // Replace with actual admin email
    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_admin_user_chat);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Get the current user
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
        } else {
            // Handle the case when the user is not logged in
        }


        userEmailTextView = findViewById(R.id.txt_Title);
        recyclerView = findViewById(R.id.recycler_admin_chatrooms);
        adminMessageEditText = findViewById(R.id.adminmessageEditText); // Fix variable name
        sendButton = findViewById(R.id.btn_send2);
        back5=findViewById(R.id.back5);

        back5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String userEmail = getIntent().getStringExtra("userEmail");
        userEmailTextView.setText(userEmail + " 님이 보낸 문의사항");

        messageList = new ArrayList<>();
        chatAdapter = new InquireAdapter(messageList, currentUserUid);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adminMessageText = adminMessageEditText.getText().toString().trim();
                if (!adminMessageText.isEmpty()) {
                    DatabaseReference userMessagesRef = FirebaseDatabase.getInstance().getReference("inquire_messages");

                    String userPath = userEmail.replace(".", "_");

                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    currentUserUid = user.getUid();
                    String adminId = currentUserUid; // 관리자의 id
                    String adminNickname = "Admin";

                    InquireChatMessage adminMessage = new InquireChatMessage(
                            adminMessageText, adminId, currentUserUid, adminNickname, InquireChatMessage.MessageType.ADMIN, adminEmail, System.currentTimeMillis()
                    );

                    DatabaseReference newUserMessageRef = userMessagesRef.child(userPath).push();

                    newUserMessageRef.setValue(adminMessage);

                    int insertedPosition = messageList.size() - 1;
                    chatAdapter.notifyItemInserted(insertedPosition);
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(insertedPosition);

                    // Clear the text input
                    adminMessageEditText.setText("");

                    // 추가된 메시지가 있을 때마다 자동 스크롤
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }
        });

        DatabaseReference userMessagesRef = FirebaseDatabase.getInstance().getReference("inquire_messages");
        String userPath = userEmail.replace(".", "_");
        userMessagesRef.child(userPath).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                InquireChatMessage userMessage = snapshot.getValue(InquireChatMessage.class);
                if (!messageList.contains(userMessage)) {
                    int insertIndex = messageList.size(); // 새로운 아이템이 추가될 인덱스
                    messageList.add(userMessage);
                    chatAdapter.notifyItemInserted(insertIndex);
                    chatAdapter.notifyDataSetChanged();
//                    recyclerView.scrollToPosition(insertIndex);

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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCurrentUserUid() {
        // TODO: Write code to get the current user's UID here
        return currentUserUid;
    }
}