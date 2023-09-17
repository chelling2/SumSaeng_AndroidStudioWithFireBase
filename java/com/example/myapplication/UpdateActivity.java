package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {
    ImageView updateImage;
    Button updateButton,back,selectPhotosButton;
    EditText updateDesc, updateTitle, updatePrice;
    String title, desc;
    int price;
    String imageUrl;
    RecyclerView photoRecyclerView;

    boolean transaction;

    List<Uri> selectedImageUris = new ArrayList<>();//new
    String userId;
    String key, oldImageURL;
    String education;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateButton = findViewById(R.id.updateButton);
        updateDesc = findViewById(R.id.updateDesc);
        updateTitle = findViewById(R.id.updateTitle);
        updatePrice = findViewById(R.id.update_et_price);
        photoRecyclerView = findViewById(R.id.rvImage);
        selectPhotosButton = findViewById(R.id.btUDSelectPhotos);

        photoAdapter = new PhotoAdapter(selectedImageUris); // imageUris是包含所有图像URI的列表
        photoRecyclerView.setAdapter(photoAdapter);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        back =findViewById(R.id.upDateBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
//                            selectedImageUris.add(uri);
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "선택된 이미지가 없습니다!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            userId = bundle.getString("userId");
//            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setText(bundle.getString("Title"));
            updateDesc.setText(bundle.getString("Description"));
            updatePrice.setText(bundle.getString("Price"));//new
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
            education = bundle.getString("education");
            transaction= bundle.getBoolean("transaction");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Apple").child(key);

//        updateImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
//            }
//        });


        updateButton.setOnClickListener(new View.OnClickListener() {
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
//    public void saveData(){
//        title = updateTitle.getText().toString().trim();
//        desc = updateDesc.getText().toString().trim();
//        price =  Integer.parseInt(updatePrice.getText().toString().trim());
//
//        if (uri != null) {
//            storageReference = FirebaseStorage.getInstance().getReference().child("Apple").child(uri.getLastPathSegment());
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
//            builder.setCancelable(false);
//            builder.setView(R.layout.progress_layout);
//            AlertDialog dialog = builder.create();
//            dialog.show();
//
//            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri urlImage) {
//                            imageUrl = urlImage.toString();
//                            updateData();
//                            dialog.dismiss();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            dialog.dismiss();
//                        }
//                    });
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    dialog.dismiss();
//                }
//            });
//        } else {
//            updateData();
//        }
//    }
//    public void updateData(){
//        DataClass dataClass;
//
//
//        if (imageUrl != null) {
//            dataClass = new DataClass(title, desc, imageUrl, userId,education,price);
//        } else {
//            dataClass = new DataClass(title, desc, oldImageURL, userId,education,price);
//        }
//
//        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    if (oldImageURL != null && imageUrl != null && !oldImageURL.equals(imageUrl)) {
//                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
//                        reference.delete();
//                    }
//
//                    Toast.makeText(UpdateActivity.this, "수정이 완료되었습니다!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(UpdateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public void saveData() {
        title = updateTitle.getText().toString().trim();
        desc = updateDesc.getText().toString().trim();
        price = Integer.parseInt(updatePrice.getText().toString().trim());

        // 创建一个列表来存储图像的 URL
        List<String> imageUrls = new ArrayList<>();

        if (selectedImageUris.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            // 遍历每个选定的图像并上传
            for (Uri selectedUri : selectedImageUris) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Gallery").child(selectedUri.getLastPathSegment());

                storageReference.putFile(selectedUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri urlImage) {
                                        // 将每个图像的 URL 添加到列表中
                                        imageUrls.add(urlImage.toString());

                                        // 如果已上传的图像数量等于选定的图像数量，就调用更新数据的方法
                                        if (imageUrls.size() == selectedImageUris.size()) {
                                            dialog.dismiss();
                                            updateData(imageUrls);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                            }
                        });
            }
        } else {
            // 如果没有图像被选定，直接调用更新数据的方法
            updateData(imageUrls);
        }
    }


    public void updateData(List<String> imageUrls) {
        // 根据您的 DataClass 构造函数，传递 imageUrls 列表
        DataClass dataClass = new DataClass(title, desc, imageUrls, userId, education, price,transaction);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (oldImageURL != null && imageUrls != null && !imageUrls.contains(oldImageURL)) {
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                        reference.delete();
                    }

                    Toast.makeText(UpdateActivity.this, "수정이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
