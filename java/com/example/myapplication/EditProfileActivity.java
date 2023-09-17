package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private static final int GALLERY_CODE = 10;
    String editname, editpwd, editid, editedu;
    ImageView iv_profile;
    TextView edit_id;
    String key;
    EditText edit_name, edit_pwd, edit_edu;
    Button btn_save,back;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edit_id = findViewById(R.id.edit_id);
        edit_name = findViewById(R.id.edit_name);
        edit_pwd = findViewById(R.id.edit_pwd);
        btn_save = findViewById(R.id.btn_save);
        edit_edu = findViewById(R.id.edit_edu);
        iv_profile = findViewById(R.id.iv_profile);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("application");
        storage = FirebaseStorage.getInstance();
        back =findViewById(R.id.btEditBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 사용자 로그인 상태 체크
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        String userId = auth.getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference().child("users/").child(userId);

        // 사진 가져오기
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            iv_profile.setImageURI(uri);
                        }
                    }
                });

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        databaseReference.child("UserAccount").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAccount userData = snapshot.getValue(UserAccount.class);
                    editid = userData.getEmailId();
                    editname = userData.getNickname();
                    editpwd = userData.getPassword();
                    editedu = userData.getEducation();
                    edit_id.setText(editid);
                    edit_name.setText(editname);
                    edit_pwd.setText(editpwd);
                    edit_edu.setText(editedu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택한 이미지가 있다면
                if (uri != null) {
                    // 이미지 업로드 시도
                    uploadToFirebase(uri);
                }

                editname = edit_name.getText().toString().trim();
                editpwd = edit_pwd.getText().toString().trim();
                editedu = edit_edu.getText().toString().trim();
                key = auth.getUid();

                HashMap<String, Object> data = new HashMap<>();
                data.put("nickname", editname);
                data.put("password", editpwd);
                data.put("education", editedu);

                databaseReference.child("UserAccount").child(key).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfileActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private void uploadToFirebase(Uri uri) {
        StorageReference storageRef = storage.getReference().child("users/").child(auth.getUid());
        UploadTask uploadTask = storageRef.putFile(uri);

        // 업로드 상태 리스너 추가
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 이미지 업로드 성공 시 다운로드 URL 가져오기 시도
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        // TODO: 다운로드 URL을 사용하여 이미지 처리
                        // 예를 들어, 이미지 URL을 데이터베이스에 저장하거나 프로필 이미지로 설정
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 이미지 업로드 실패 처리
                Toast.makeText(EditProfileActivity.this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY_CODE);
    }
}
