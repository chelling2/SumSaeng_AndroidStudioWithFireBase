package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class InquireAdminChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> emailList; // 이메일 목록을 저장할 리스트

    private Button back4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquire_admin_list);

        recyclerView = findViewById(R.id.recycler_admin_chat);
        back4=findViewById(R.id.back4);

        back4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        emailList = new ArrayList<>();

        InquireAdminChatAdapter adminChatAdapter = new InquireAdminChatAdapter(emailList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminChatAdapter);

        DatabaseReference adminMessagesRef = FirebaseDatabase.getInstance().getReference("inquire_messages");

        adminMessagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String userEmail = snapshot.getKey();
                Log.d("InquireAdminChat", "User Email: " + userEmail);

                emailList.add(userEmail);
                adminChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Do nothing
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Do nothing
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Do nothing
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Firebase database operation failed: " + error.getMessage());
            }
        });
    }
}