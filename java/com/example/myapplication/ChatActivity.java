package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private EditText messageEditText;
    private Button sendButton, plusButton, sendMoney, review;
    private ListView chatListView;
    private ChatAdapter chatAdapter;

    private String recipientUserId;

    private ImageView otherUserProfileImage;
    private TextView otherUserNickname, price, title, contents;
    String key = "";

    DatabaseReference usersRef;

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher;

    ImageView uploadImage;

    Uri uri;
    private String currentUserID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        recipientUserId = getIntent().getStringExtra("recipientUserId");

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        plusButton = findViewById(R.id.plusButton);
        sendMoney = findViewById(R.id.sendMoney);
        chatListView = findViewById(R.id.chatListView);
        uploadImage = findViewById(R.id.chat_uploadImage);
        otherUserNickname = findViewById(R.id.otherUserNickname);
        otherUserProfileImage = findViewById(R.id.otherUserProfileImage);
        review = findViewById(R.id.bPersonReview);
        price = findViewById(R.id.cPrice);
        title = findViewById(R.id.cTitle);
        contents = findViewById(R.id.cContents);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {//new
            key = bundle.getString("Key");
            DatabaseReference data = FirebaseDatabase.getInstance().getReference("Apple").child(key);
            DatabaseReference transactionRef = data.child("transaction");
            DatabaseReference priceRef = data.child("price");
            DatabaseReference titleRef = data.child("dataTitle");
            DatabaseReference contentsRef = data.child("dataDesc");

            priceRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int priceValue = dataSnapshot.getValue(int.class);
                        DecimalFormat decimalFormat = new DecimalFormat("#,###"); // 创建一个带千位分隔符的格式
                        String formattedPrice = decimalFormat.format(priceValue);
                        price.setText(String.valueOf(formattedPrice) + "원");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "오류 : " + databaseError.getMessage());
                }
            });
            titleRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String priceValue = dataSnapshot.getValue(String.class);
                        title.setText(priceValue);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "오류 : " + databaseError.getMessage());
                }
            });
            contentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String priceValue = dataSnapshot.getValue(String.class);
                        contents.setText(priceValue);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "오류 : " + databaseError.getMessage());
                }
            });




            transactionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(Boolean.class) && !currentUserID.equals(recipientUserId)) {

                        review.setVisibility(View.VISIBLE);
                    } else {

                        review.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // 데이터베이스에서 값을 읽어오지 못한 경우 처리
                }
            });

        }

        review.setOnClickListener(new View.OnClickListener() { //*************
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ReviewActivity.class)
                        .putExtra("userId", recipientUserId)//***************
                        .putExtra("Key", key);//***************
                startActivity(intent);
            }
        });

        usersRef = FirebaseDatabase.getInstance().getReference().child("application").child("UserAccount").child(recipientUserId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String otherUserNicknameStr = snapshot.child("nickname").getValue(String.class);
                    Log.d("ChatActivity", "Other user nickname: " + otherUserNicknameStr); // 닉네임을 로그로 출력

                    // 상대방의 닉네임을 설정
                    updateUIWithOtherUserInfo(otherUserNicknameStr);

                    StorageReference profileRef = mFirebaseStorage.getReference().child("users/").child(recipientUserId);

                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d("ChatActivity", "Profile image URL: " + uri.toString()); // 프로필 이미지 URL을 로그로 출력
                        Glide.with(ChatActivity.this)
                                .load(uri)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(otherUserProfileImage);
                    }).addOnFailureListener(exception -> {
                        otherUserProfileImage.setImageResource(R.drawable.perprofile);
                    });
                } else {
                    otherUserProfileImage.setImageResource(R.drawable.perprofile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });


        // 기존의 ActivityResultCallback 대신 람다식으로 사용
        galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();

                            uploadImageAndSendMessage(imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageEditText.setText("");

                    // 메시지를 추가한 후 스크롤을 맨 아래로 이동
                    chatListView.setSelection(chatAdapter.getCount() - 1);
                }

            }
        });

        sendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransferDialog();
            }
        });

        Button detailBackButton = findViewById(R.id.detailBack);
        detailBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티를 종료하여 이전 페이지로 돌아갑니다.
            }
        });

        String chatRoomId = getChatId();

        String currentUserId = (currentUser != null) ? currentUser.getUid() : null;
        chatAdapter = new ChatAdapter(this, currentUserId, recipientUserId);
        chatListView.setAdapter(chatAdapter);

        DatabaseReference chatRef = databaseReference.child("chats").child(chatRoomId);
//        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
//                    chatAdapter.addMessage(chatMessage);
//                }
//                chatAdapter.notifyDataSetChanged();
//
//                // 모든 메시지를 로드한 후 스크롤을 맨 아래로 이동
//                chatListView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        chatListView.setSelection(chatAdapter.getCount() - 1);
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // 에러 처리...
//            }
//        });

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                chatAdapter.addMessage(chatMessage);
                chatAdapter.notifyDataSetChanged();

                // 메시지를 추가한 후 스크롤을 맨 아래로 이동
                chatListView.setSelection(chatAdapter.getCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        galleryActivityResultLauncher.launch(photoPicker);
    }

    private void uploadImageAndSendMessage(Uri imageUri) {
        uploadImageToStorage(imageUri, new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageUrl = uri.toString();
                sendMessageWithImage(imageUrl);
            }
        });
    }

    private void sendMessageWithImage(String imageUrl) {
        ChatMessage message = new ChatMessage(currentUser != null ? currentUser.getUid() : null, recipientUserId, "");
        message.setType("image");
        message.setContent(imageUrl);

        DatabaseReference chatRef = databaseReference.child("chats").child(getChatId());
        chatRef.push().setValue(message);
        chatListView.smoothScrollToPosition(chatAdapter.getCount() - 1);
    }

    private void uploadImageToStorage(Uri imageUri, OnSuccessListener<Uri> successListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child("images/" + currentUser.getUid() + ".jpg");

        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnSuccessListener(successListener);
                } else {
                    Toast.makeText(ChatActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String message) {
        String chatRoomId = getChatId();

        if (chatRoomId != null) {
            DatabaseReference chatRef = databaseReference.child("chats").child(chatRoomId);
            String messageId = chatRef.push().getKey();

            ChatMessage chatMessage = new ChatMessage(currentUser.getUid(), recipientUserId, message);
            chatMessage.setType("text");

            chatRef.child(messageId).setValue(chatMessage);

            // 메시지를 추가한 후 스크롤
            // chatListView.setSelection(chatAdapter.getCount() - 1);
        } else {
            Toast.makeText(this, "채팅방 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getChatId() {
        return generateChatRoomId(currentUser != null ? currentUser.getUid() : "", recipientUserId);
    }

    @NonNull
    private String generateChatRoomId(String uid1, String uid2) {
        if (uid1 == null) {
            uid1 = "";
        }
        if (uid2 == null) {
            uid2 = "";
        }

        String[] sortedUids = {uid1, uid2};
        Arrays.sort(sortedUids);

        return "chats/" + sortedUids[0] + "_" + sortedUids[1] + "_" + key;
    }

    private void updateUIWithOtherUserInfo(String otherUserNicknameStr) {
        otherUserNickname.setText(otherUserNicknameStr);
    }

    private void showTransferDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.transfer_dialog, null);
        dialogBuilder.setView(dialogView);

        // 获取对话框中的视图元素
        TextView recipientUsername = dialogView.findViewById(R.id.recipientUsername);
        EditText transferAmount = dialogView.findViewById(R.id.transferAmount);
        Button transferButton = dialogView.findViewById(R.id.transferButton);
        Button cancelButton = dialogView.findViewById(R.id.btCancel);


        AlertDialog alertDialog = dialogBuilder.create();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // 다이얼로그 닫기
            }
        });

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String otherUserNicknameStr = snapshot.child("nickname").getValue(String.class);
                    // 상대방의 닉네임을 설정
                    recipientUsername.setText(otherUserNicknameStr);
//
//                    StorageReference profileRef = mFirebaseStorage.getReference().child("users/").child(recipientUserId);
//
//                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        Log.d("ChatActivity", "Profile image URL: " + uri.toString()); // 프로필 이미지 URL을 로그로 출력
//                        Glide.with(ChatActivity.this)
//                                .load(uri)
//                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                                .into(otherUserProfileImage);
//                    }).addOnFailureListener(exception -> {
//                        otherUserProfileImage.setImageResource(R.drawable.perprofile);
//                    });
//                } else {
//                    otherUserProfileImage.setImageResource(R.drawable.perprofile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });


        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientUserId = getIntent().getStringExtra("recipientUserId");
                String transferAmountStr = transferAmount.getText().toString();

                performTransfer(recipientUserId, transferAmountStr);

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void performTransfer(String recipientUserId, String transferAmountStr) {
        // 將轉帳金額從字符串轉換為 BigDecimal
        final BigDecimal transferAmount = new BigDecimal(transferAmountStr);
        BigDecimal feePercentage = new BigDecimal("0.05"); // 手續費的百分比，轉換為 BigDecimal
        BigDecimal fee = transferAmount.multiply(feePercentage); // 計算手續費

        // 獲取當前用戶的UID
        final String currentUserId = currentUser.getUid();

        // 步驟1：獲取當前用戶的帳戶信息和餘額
        DatabaseReference currentAccountRef = FirebaseDatabase.getInstance().getReference()
                .child("application")
                .child("UserAccount")
                .child(currentUserId);

        currentAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 獲取當前用戶的餘額
                    String currentBalanceStr = dataSnapshot.child("money").getValue(String.class);
                    BigDecimal currentBalance = new BigDecimal(currentBalanceStr);

                    // 步驟2：獲取接收方用戶的帳戶信息和餘額
                    DatabaseReference recipientAccountRef = FirebaseDatabase.getInstance().getReference()
                            .child("application")
                            .child("UserAccount")
                            .child(recipientUserId);

                    recipientAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot recipientDataSnapshot) {
                            if (recipientDataSnapshot.exists()) {
                                // 獲取接收方用戶的餘額
                                String recipientBalanceStr = recipientDataSnapshot.child("money").getValue(String.class);
                                BigDecimal recipientBalance = new BigDecimal(recipientBalanceStr);

                                // 步驟3：檢查當前用戶是否有足夠的餘額來執行轉帳操作
                                if (currentBalance.compareTo(transferAmount) >= 0) {
                                    // 步驟4：扣除當前用戶的金額並增加接收方用戶的金額
                                    BigDecimal newCurrentBalance = currentBalance.subtract(transferAmount);
                                    BigDecimal newRecipientBalance = recipientBalance.add(transferAmount.subtract(fee));
                                    newRecipientBalance = newRecipientBalance.setScale(0, RoundingMode.HALF_UP);

                                    // 步驟5：更新數據庫中的餘額信息
                                    currentAccountRef.child("money").setValue(newCurrentBalance.toString());
                                    recipientAccountRef.child("money").setValue(newRecipientBalance.toString());

                                    // 提示轉帳成功或執行其他相關操作
                                    Toast.makeText(ChatActivity.this, "송금 완료", Toast.LENGTH_SHORT).show();
                                } else {
                                    // 提示餘額不足或執行其他相關操作
                                    Toast.makeText(ChatActivity.this, "잔액 부족", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError recipientDatabaseError) {
                            // 處理接收方用戶數據庫讀取錯誤
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 處理當前用戶數據庫讀取錯誤
            }
        });
    }


}