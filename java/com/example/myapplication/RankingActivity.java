package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RankAdapter rankAdapter;
    private DatabaseReference databaseReference;
    private Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankAdapter = new RankAdapter();
        recyclerView.setAdapter(rankAdapter);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("application").child("UserAccount"); // Changed "application" to your appropriate reference path

        // Query Firebase Realtime Database for ranking data
        Query query = databaseReference.orderByChild("point").limitToLast(10);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserAccount> userAccounts = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserAccount user = userSnapshot.getValue(UserAccount.class); // Changed User to UserAccount
                    userAccounts.add(user);
                }
                Collections.reverse(userAccounts); // To show highest points at the top
                rankAdapter.setUsers(userAccounts); // Pass the userAccounts list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RankingActivity", "Error getting ranking data", error.toException());
            }
        });

        back = findViewById(R.id.rankingBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
}
