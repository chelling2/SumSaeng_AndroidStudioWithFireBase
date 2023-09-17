package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PersonalProfileActivity extends AppCompatActivity {
    //review
    RecyclerView reviewRecyclerView;
    List<Review> reviewList;
    ReviewAdapter reviewAdapter;
    //review
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser user;

    private ImageView profile;
    private TextView nickname, school;

    private Button back7;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    RatingBar ratingBar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        ratingBar = findViewById(R.id.rbOppon);
        back7 = findViewById(R.id.back7);

//review
        reviewRecyclerView = findViewById(R.id.rvReview);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
//review
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("application");
        user = mFirebaseAuth.getCurrentUser();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        nickname = findViewById(R.id.nickname);
        school = findViewById(R.id.school);
        profile = findViewById(R.id.profile);
//review
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setAdapter(reviewAdapter);



        back7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadReviewsFromFirebase();
        //review
        //-----------------------------------------
        Intent intent = getIntent();
        String authorId = intent.getStringExtra("authorId");
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings").child(authorId);

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int totalRatings = 0;

                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    Review rating = ratingSnapshot.getValue(Review.class); // 假設有一個 Rating 類型來表示評價
                    if (rating != null) {
                        totalRating += rating.getRating();
                        totalRatings++;
                    }
                }

                if (totalRatings > 0) {
                    float averageRating = totalRating / totalRatings;
                    // 使用計算出的 averageRating 來更新 ratingBar 或顯示在界面上
                    ratingBar.setRating(averageRating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 處理讀取資料失敗的情況
            }
        });

        //-----------------------------------------

        // 본인 프로필 또는 글 작성자 프로필을 표시

        DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(authorId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("nickname").getValue(String.class);
                    String schoola = snapshot.child("education").getValue(String.class);
                    nickname.setText(name + "님");
                    school.setText(schoola);

                    StorageReference profileRef = mStorageReference.child("users/").child(authorId);
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(PersonalProfileActivity.this)
                                .load(uri)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop())) // 원형으로 바꾸는 방법이래요~
                                .into(profile);
                    }).addOnFailureListener(exception -> {
                        // 이미지 로드 실패 시 동작
                        // 에러 처리를 원하는 대로 진행
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String errorMessage = "데이터 가져오기 실패: " + error.getMessage();
                Toast.makeText(PersonalProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //review

    private void loadReviewsFromFirebase () {
        Intent intent = getIntent();
        String authorId = intent.getStringExtra("authorId");

        // 使用 DatabaseReference 從 Firebase 加載評價數據
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings").child(authorId);
        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    review.setUserId(reviewSnapshot.getKey());// 或者根據你的數據結構設置使用者 ID
                    reviewList.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 載入失敗時的處理
            }
        });
    }
//review
}

