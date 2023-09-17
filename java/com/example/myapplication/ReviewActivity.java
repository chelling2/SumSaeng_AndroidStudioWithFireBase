package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {

    RatingBar ratingBar;
    TextView ratingValue,textView3;
    Button btSendReview;
    EditText etReview;
    ImageView opppro;

    String ratedUserId,key;

    Button back;
    private String currentUserID;//new

    private DatabaseReference mDatabaseRef;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ratingBar = findViewById(R.id.ratingBar);
        ratingValue = findViewById(R.id.ratingValue);
        btSendReview = findViewById(R.id.btSendReview);
        opppro = findViewById(R.id.oppro);
        textView3 = findViewById(R.id.textView3);
        etReview = findViewById(R.id.etReview);//******
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();//new


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("application");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        Bundle bundle = getIntent().getExtras();//******
        ratedUserId = bundle.getString("userId");//*******
        key = bundle.getString("Key");
        back = findViewById(R.id.btReviewBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btSendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = ratingBar.getRating();
                String review = etReview.getText().toString();//******
                String reviewerUserId = currentUserID; // 評價者的 ID
                uploadRatingToFirebase(rating, review, ratedUserId, reviewerUserId,key);
                finish();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue.setText(rating + " 점");
            }
        });


        DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(ratedUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String writerName = snapshot.child("nickname").getValue(String.class);
                    String educationName = snapshot.child("education").getValue(String.class);

                    textView3.setText(writerName + "님");

                    // 프로필 이미지 설정
                    StorageReference profileRef = mStorageReference.child("users/").child(ratedUserId);
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop())) // 원형으로 바꾸는 방법이래요~
                                .into(opppro);
                    }).addOnFailureListener(exception -> {
                        opppro.setImageResource(R.drawable.perprofile);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        }); // 수정된 부분: 중괄호 및 괄호 추가
    }
    private void uploadRatingToFirebase(float rating, String review, String ratedUserId, String reviewerUserId, String key) {


        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings")
                .child(ratedUserId)
                .child(reviewerUserId);

        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingData.put("review", review);
        ratingData.put("key", key);


        ratingsRef.setValue(ratingData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // 評分成功上傳後，更新使用者分數
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("application").child("UserAccount").child(ratedUserId);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    UserAccount user = snapshot.getValue(UserAccount.class);
                                    if (user != null) {
                                        // 增加評分後的值到使用者分數
                                        int currentPoint = user.getPoint();
                                        int newPoint = currentPoint + (int) rating;
                                        user.setPoint(newPoint);

                                        // 更新使用者分數到 Firebase Database
                                        userRef.setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(ReviewActivity.this, "성공적으로 후기를 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ReviewActivity.this, "후기 작성 실폐：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ReviewActivity.this, "사용자 접수 실폐：" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReviewActivity.this, "점수 업로드 실폐：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}


