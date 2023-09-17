package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class SendACommissionActivity extends AppCompatActivity {
    ImageView uploadImage;
    Button back, saveButton,selectPhotosButton;
    EditText uploadTopic, uploadDesc,etPrice;

    RecyclerView photoRecyclerView;
    List<Uri> selectedImageUris = new ArrayList<>();// 用於儲存選取的相片 URI
    PhotoAdapter photoAdapter;

//    Number price;
    Uri uri;
    String imageURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_acommission);

        back = findViewById(R.id.back);
//        uploadImage = findViewById(R.id.uploadImage);
        uploadTopic = findViewById(R.id.uploadTopic);
        uploadDesc = findViewById(R.id.uploadDesc);
        etPrice = findViewById(R.id.et_price);
        saveButton = findViewById(R.id.saveButton);

        //new
        photoRecyclerView = findViewById(R.id.imageRecyclerView);
        photoAdapter = new PhotoAdapter(selectedImageUris);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photoRecyclerView.setAdapter(photoAdapter);

        selectPhotosButton = findViewById(R.id.selectPhotosButton);

        selectPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 啟動選擇相片的Intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, 1);
            }
        });


//new


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(SendACommissionActivity.this, "선택된 이미지가 없습니다!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
////                photoPicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 允許選取多張相片
//                activityResultLauncher.launch(photoPicker);
//            }
//        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    // 添加 onActivityResult 方法来处理选定的照片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(uri);
                }
                photoAdapter.notifyDataSetChanged(); // 通知适配器数据已更改
            }
        }
    }

    public void saveData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SendACommissionActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        List<String> imageUrls = new ArrayList<>(); // 存储所有上传照片的 URL
        // 如果没有选择照片，直接上传数据
        if (selectedImageUris.isEmpty()) {
            uploadData(imageUrls);
            dialog.dismiss();
            return;
        }


        // 逐个上传每张照片
        for (Uri uri : selectedImageUris) {
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference().child("Gallery").child(uri.getLastPathSegment());

            storageReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri urlImage = task.getResult();
                                        String imageURL = urlImage.toString();
                                        imageUrls.add(imageURL);

                                        // 如果所有照片都已上传完成，执行上传数据
                                        if (imageUrls.size() == selectedImageUris.size()) {
                                            uploadData(imageUrls);
                                            dialog.dismiss();
                                        }
                                    } else {
                                        dialog.dismiss();
                                        // 处理获取下载链接失败的情况
                                        Toast.makeText(SendACommissionActivity.this, "사진 업로드 실폐", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            // 处理上传失败的情况
                            Toast.makeText(SendACommissionActivity.this, "사진 업로드 실폐", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void uploadData(List<String> imageUrls) {
        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String priceText = etPrice.getText().toString();
        boolean transaction = false;

        if (title.isEmpty() || desc.isEmpty()) {

            Toast.makeText(this, "제목과 설명을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        int price;
        if (!priceText.isEmpty()) {
            try {
                price = Integer.parseInt(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "유효하지 않은 가격입니다", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "가격을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("application").child("UserAccount").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String education = snapshot.child("education").getValue(String.class);
                DataClass dataClass = new DataClass(title, desc, imageUrls, userId, education, price,transaction);


                long currentTimeMillis = System.currentTimeMillis();
                String currentDate = String.valueOf(currentTimeMillis);

                FirebaseDatabase.getInstance().getReference("Apple").child(currentDate)
                        .setValue(dataClass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SendACommissionActivity.this, "게시글이 등록되었습니다!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SendACommissionActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendACommissionActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    public void saveData() {
//        if (uri != null) { // 사진이 선택된 경우에만 사진 업로드 진행
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Gallery")
//                    .child(uri.getLastPathSegment());
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(SendACommissionActivity.this);
//            builder.setCancelable(false);
//            builder.setView(R.layout.progress_layout);
//            AlertDialog dialog = builder.create();
//            dialog.show();
//
//            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                    while (!uriTask.isComplete()) ;
//                    Uri urlImage = uriTask.getResult();
//                    imageURL = urlImage.toString();
//                    uploadData();
//                    dialog.dismiss();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    dialog.dismiss();
//                }
//            });
//        } else { // 사진이 선택되지 않은 경우에도 글 업로드 진행
//            uploadData();
//        }
//    }
//
//    public void uploadData() {
//
//        String title = uploadTopic.getText().toString();
//        String desc = uploadDesc.getText().toString();
////        int price = Integer.parseInt(etPrice.getText().toString());
//        String priceText = etPrice.getText().toString();
//
//        //new
//        if (title.isEmpty() || desc.isEmpty()) {
//            // 如果标题或描述为空，向用户显示错误消息
//            Toast.makeText(this, "请输入标题和描述", Toast.LENGTH_SHORT).show();
//            return; // 在此返回，不继续上传逻辑
//        }
//        int price;
//        if (!priceText.isEmpty()) {
//            try {
//                price = Integer.parseInt(priceText);
//            } catch (NumberFormatException e) {
//                Toast.makeText(this, "无效的价格值", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        } else {
//            Toast.makeText(this, "请输入价格", Toast.LENGTH_SHORT).show();
//            return;
//        }//new@@
//
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();//new
//
//
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("application").child("UserAccount").child(userId);
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String education = snapshot.child("education").getValue(String.class);
//                DataClass dataClass = new DataClass(title, desc, imageURL, userId, education, price);
//
////                String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//
//                // 使用 Calendar 獲取當前時間的 Unix timestamp
//                long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
//
//                // 將 Unix timestamp 轉換為字符串，作為 Firebase 節點名稱
//                String currentDate = String.valueOf(currentTimeMillis);
//
//                FirebaseDatabase.getInstance().getReference("Apple").child(currentDate)
//                        .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(SendACommissionActivity.this, "게시글이 등록되었습니다!", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(SendACommissionActivity.this, MainActivity.class);
//                                    startActivity(intent);
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(SendACommissionActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//    }
}



