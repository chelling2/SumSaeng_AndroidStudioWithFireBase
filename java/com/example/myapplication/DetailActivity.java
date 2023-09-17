package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, nickNameImage, educationImage, tvPrice, transaction_con;//new
    ImageView profileImage;
    //    ImageView detailImage;
    RecyclerView recyclerView;
    FloatingActionButton deleteButton, editButton, transaction_cb;
    String key = "";
    String imageUrl = "";
    Button back;

    Button btChat;

    FloatingActionMenu floatingActionMenu;

    private String currentUserID;
    private String education;

    private DatabaseReference mDatabaseRef;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private ArrayList<String>  imageUris; // 用于存储多个图片的URI
    private boolean transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        floatingActionMenu = findViewById(R.id.floatingActionMenu);
//        detailImage = findViewById(R.id.detailImage);
        recyclerView = findViewById(R.id.rvDetailPhoto);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailDesc = findViewById(R.id.detailDesc);
        back = findViewById(R.id.detailBack);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tvPrice = findViewById(R.id.tvPrice);
        transaction_con = findViewById(R.id.transaction_con);
        transaction_cb = findViewById(R.id.transaction_cb);

        profileImage = findViewById(R.id.profileimage);
        nickNameImage = findViewById(R.id.nicknameimage);
        educationImage = findViewById(R.id.educationimage);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("application");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        btChat = findViewById(R.id.btChat);


//        StorageReference imagesRef = mStorageReference.child("Gallery");


        // 初始化imageUris
        imageUris = getIntent().getStringArrayListExtra("Image");
//        ArrayList<String>
        DetailPhotoAdapter photoAdapter = new DetailPhotoAdapter(imageUris); // imageUris是包含所有图像URI的列表
        recyclerView.setAdapter(photoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        String recipientUserId = getIntent().getStringExtra("UserId");//new*********

        btChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, ChatActivity.class);
                intent.putExtra("recipientUserId", recipientUserId);
                intent.putExtra("Key", key);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        String userIdFromBundle = getIntent().getStringExtra("UserId");//new*********
        Bundle bundle = getIntent().getExtras();



        if (bundle != null) {//new
            if (currentUserID.equals(userIdFromBundle)) {  //new
                floatingActionMenu.setVisibility(View.VISIBLE);
            } else {
                floatingActionMenu.setVisibility(View.GONE);
            }//new

        }

        if (bundle != null) {//new
            if (currentUserID.equals(userIdFromBundle)) {  //new
                btChat.setVisibility(View.GONE);
            } else {
                btChat.setVisibility(View.VISIBLE);
            }//new
        }


        if (bundle != null) {
            //22
            int price = Integer.parseInt(bundle.getString("Price")); // Convert the String back to int
            DecimalFormat decimalFormat = new DecimalFormat("#,###"); // 创建一个带千位分隔符的格式
            String formattedPrice = decimalFormat.format(price);
            tvPrice.setText(String.valueOf(formattedPrice) + "원"); // Set the converted int value to the TextView


            //22

            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
//            tvPrice.setText(bundle.getString("Price"));//new
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            education = bundle.getString("education");

//            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

        DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(userIdFromBundle);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String writerName = snapshot.child("nickname").getValue(String.class);
                    String educationName = snapshot.child("education").getValue(String.class);

                    nickNameImage.setText(writerName + "님");
                    educationImage.setText(educationName);

                    // 프로필 이미지 설정
                    StorageReference profileRef = mStorageReference.child("users/").child(userIdFromBundle);
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop())) // 원형으로 바꾸는 방법이래요~
                                .into(profileImage);
                    }).addOnFailureListener(exception -> {
                        // 이미지 로드 실패 시 동작
                        // 에러 처리를 원하는 대로 진행
                    });
                } else {
                    profileImage.setImageResource(R.drawable.perprofile);
                    Toast.makeText(DetailActivity.this, "프로필 이미지 없음", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // onCreate 메서드 내에서 transaction 값을 가져와서 설정
        DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("Apple").child(key).child("transaction");
        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue(Boolean.class)) {
                    // 거래가 완료된 경우
                    transaction = true;
                    transaction_con.setText("거래완료");
                } else {
                    // 거래가 완료되지 않은 경우
                    transaction = false;
                    transaction_con.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 데이터베이스에서 값을 읽어오지 못한 경우 처리
            }
        });

        transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Boolean.class) ) {
                    btChat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 데이터베이스에서 값을 읽어오지 못한 경우 처리
            }
        });

        transaction_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 파이어베이스 데이터베이스 레퍼런스
                DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("Apple").child(key).child("transaction");

                // 새로운 값으로 업데이트
                transactionRef.setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // 업데이트가 성공적으로 완료되었을 때 수행할 작업
                                transaction_con.setText("거래완료");
                                transaction = true;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 업데이트 중 오류 발생 시 수행할 작업
                                Toast.makeText(DetailActivity.this, "거래 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        nickNameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, PersonalProfileActivity.class);
                intent.putExtra("authorId", userIdFromBundle);
                startActivity(intent);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, PersonalProfileActivity.class);
                intent.putExtra("authorId", userIdFromBundle);
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Apple");

                if (imageUrl != null) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            deleteData(reference);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "사진 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    deleteData(reference);
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String priceWithoutWon = tvPrice.getText().toString().replace("원", "");//new
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("Price", priceWithoutWon)// new
                        .putExtra("Image", imageUrl)
                        .putExtra("userId", userIdFromBundle)
                        .putExtra("education", education)
                        .putExtra("Key", key)
                        .putExtra("transaction", transaction);

                startActivity(intent);
            }
        });

    }

    private void deleteData(DatabaseReference reference) {
        reference.child(key).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onDeleteSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailActivity.this, "사진 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onDeleteSuccess() {
        Toast.makeText(DetailActivity.this, "삭제가 완료되었습니다!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}


