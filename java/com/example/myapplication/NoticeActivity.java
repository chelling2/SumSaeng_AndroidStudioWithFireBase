package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
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

public class NoticeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Notice> noticeList;
    private NoticeAdapter adapter;

    private Button noticeAdminWritingButton,back6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeList = new ArrayList<>();
        adapter = new NoticeAdapter(noticeList);
        recyclerView.setAdapter(adapter);
        back6 = findViewById(R.id.back6);

        back6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference noticesRef = database.getReference("notices");

        // 관리자로 로그인 했을 경우에만 글작성 버튼이 보이도록
        noticeAdminWritingButton = findViewById(R.id.notice_admin_writing);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail().equals("관리자 이메일!!")) {
            noticeAdminWritingButton.setVisibility(View.VISIBLE);
        } else {
            noticeAdminWritingButton.setVisibility(View.GONE);
        }

        noticeAdminWritingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoticeActivity.this, NoticeAdmin.class);
                startActivity(intent);
            }
        });

        noticesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noticeList.clear();
                for (DataSnapshot noticeSnapshot : dataSnapshot.getChildren()) {
                    Notice notice = noticeSnapshot.getValue(Notice.class);
                    if (notice != null) {
                        noticeList.add(notice);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터 가져오기 실패 시 처리
            }
        });
    }
}