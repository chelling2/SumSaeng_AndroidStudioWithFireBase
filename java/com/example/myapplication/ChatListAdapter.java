package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private List<String> chatRoomList;
    private Context context; // 新增一個成員變數 context
    private List<DataSnapshot> chatRoomSnapshots; // 添加 chatRoomSnapshots 變數
    FirebaseUser currentUser;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();


    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();


    public ChatListAdapter(Context context, List<String> chatRoomList, List<DataSnapshot> chatRoomSnapshots, FirebaseUser currentUser) {
        this.context = context; // 初始化 context
        this.chatRoomList = chatRoomList;
        this.chatRoomSnapshots = chatRoomSnapshots;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
        return new ViewHolder(itemView,currentUser);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataSnapshot chatRoomSnapshot = chatRoomSnapshots.get(position);
        mStorageReference = mFirebaseStorage.getReference();

        // 從 chatRoomSnapshot 獲取用戶的 userId
        String userId = chatRoomSnapshot.child("userId").getValue(String.class);

        holder.bind(chatRoomSnapshot, userId);


//        String profileImageUrl = chatRoomSnapshot.child("profileImageUrl").getValue(String.class);

//         設置頭像圖片
//        if (profileImageUrl != null) {
//            StorageReference profileRef = mStorageReference.child(profileImageUrl);
//            profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                Glide.with(context)
//                        .load(uri)
//                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                        .into(holder.profileImageView);
//            }).addOnFailureListener(exception -> {
//                holder.profileImageView.setImageResource(R.drawable.perprofile);
//            });
//        } else {
//            holder.profileImageView.setImageResource(R.drawable.perprofile);
//        }
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
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

        return "chats/" + sortedUids[0] + "_" + sortedUids[1];
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView displayInfoTextView;
        TextView displayUserTextView;
        ImageView profileImageView;
        FirebaseUser currentUser;

        private StorageReference mStorageReference;


        public ViewHolder(View itemView, FirebaseUser currentUser) {
            super(itemView);
            this.currentUser = currentUser;
            displayUserTextView = itemView.findViewById(R.id.displayUserTextView);
            displayInfoTextView = itemView.findViewById(R.id.displayInfoTextView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            mStorageReference = FirebaseStorage.getInstance().getReference();
            mFirebaseStorage = FirebaseStorage.getInstance();

        }

        public void bind(DataSnapshot chatRoomSnapshot, String userId) {
            String displayInfo = chatRoomList.get(getAdapterPosition());
            String[] display = displayInfo.split("@");
            String displayInfo1 = display[0];
            String displayInfo2 = display[1];
            Log.d("MyApp", "displayInfo1: " + displayInfo1);
            Log.d("MyApp", "displayInfo2: " + displayInfo2);

            String profileImageUrl = "users/" + userId + "/profileImage";

            itemView.setOnClickListener(view -> {
                String chatRoomId = chatRoomSnapshot.getKey();
                String[] userIds = chatRoomId.split("_");

                String senderUserId = userIds[0];
                String recipientUserId = userIds[1];
                String key = userIds[2];;

                // 使用者 B 登入時，交換 senderUserId 與 recipientUserId
                if (currentUser.getUid().equals(recipientUserId)) {
                    String temp = senderUserId;
                    senderUserId = recipientUserId;
                    recipientUserId = temp;
                }

                String clickedChatRoomId = generateChatRoomId(senderUserId, recipientUserId);

                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra("chatRoomId", clickedChatRoomId);
                intent.putExtra("recipientUserId", recipientUserId);
                intent.putExtra("Key", key);
                context.startActivity(intent);
            });

            displayUserTextView.setText(displayInfo1);
            displayInfoTextView.setText(displayInfo2);


            if (profileImageUrl != null) {
                String chatRoomId = chatRoomSnapshot.getKey();
                String[] userIds = chatRoomId.split("_");
                String senderUserId = "";
                if (userIds.length == 3) {
                    if (userIds[0].equals(currentUser.getUid())) {
                        senderUserId = userIds[1];
                    } else if (userIds[1].equals(currentUser.getUid())) {
                        senderUserId = userIds[0];
                    }
                }


                StorageReference profileRef = mStorageReference.child("users/").child(senderUserId);//("users/").child(user.getUid())
                profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(context)
                            .load(uri)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(profileImageView);
                }).addOnFailureListener(exception -> {
                    profileImageView.setImageResource(R.drawable.perprofile);
                });
            } else {
                profileImageView.setImageResource(R.drawable.perprofile);
            }

        }

    }
}

