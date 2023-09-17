package com.example.myapplication;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ItemchangeActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemchange);
        imageView = findViewById(R.id.imageView);
        btBack = findViewById(R.id.btBack);

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String itemName = getIntent().getStringExtra("itemName");

        // Firebase 스토리지에서 이미지를 다운로드하기 위한 레퍼런스 생성
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("Gifticon").child(itemName + ".jpg");

        // 이미지를 다운로드하고 이미지뷰에 설정
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Glide를 사용하여 이미지를 로드하고 표시
                Glide.with(ItemchangeActivity.this)
                        .load(uri)
                        .into(imageView);
            }
        });
    }
}
