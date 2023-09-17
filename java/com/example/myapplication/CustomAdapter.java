package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<ShopItem> arrayList;
    private Context context;

    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage firebasestorage;

    int mypoint;
    String sumpoint;

    public CustomAdapter(ArrayList<ShopItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;

        firebasestorage = FirebaseStorage.getInstance();
        storageReference = firebasestorage.getReference();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getItemImage())
                .into(holder.iv_itemimage);
        holder.tv_itemplace.setText("사용처 : " + arrayList.get(position).getItemPlace());
        holder.tv_itemname.setText("상품명 : " + arrayList.get(position).getItemName());
        holder.tv_itempoint.setText("포인트 : " + arrayList.get(position).getItemPoint());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_itemimage;
        private TextView tv_itemplace;
        private TextView tv_itemname;
        private TextView tv_itempoint;
        private Button btn_buy;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_itemimage = itemView.findViewById(R.id.iv_itemimage);
            this.tv_itemplace = itemView.findViewById(R.id.tv_itemplace);
            this.tv_itemname = itemView.findViewById(R.id.tv_itemname);
            this.tv_itempoint = itemView.findViewById(R.id.tv_itempoint);
            this.btn_buy = itemView.findViewById(R.id.btn_buy);
            mFirebaseAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            String key = mFirebaseAuth.getUid();
            firebasestorage = FirebaseStorage.getInstance();

            // 여기부터 point 데이터 가져오기
            databaseReference = database.getReference("application");
            databaseReference.child("UserAccount").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserAccount userAccount = snapshot.getValue(UserAccount.class);
                        mypoint = Integer.parseInt(userAccount.getMoney());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            btn_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentPos = getAdapterPosition();
                    ShopItem shopItem = arrayList.get(currentPos);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("구매");
                    builder.setIcon(R.drawable.baseline_shopping_cart_24);
                    builder.setMessage("선택한 상품 : " + shopItem.getItemName() +
                            "\n\n" +
                            "정말 구매하시겠습니까 ?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mypoint >= shopItem.getItemPoint())
                            {
                                sumpoint = String.valueOf(mypoint - shopItem.getItemPoint());

                                // 여기부터 데이터 업데이트 코드
                                HashMap<String, Object> data = new HashMap<>();
                                data.put("money",sumpoint);
                                databaseReference = database.getReference("application");
                                databaseReference.child("UserAccount").child(key).updateChildren(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // 화면 새로고침
                                        Intent intent = ((Activity)context).getIntent();
                                        ((Activity)context).finish();
                                        ((Activity)context).overridePendingTransition(0, 0);
                                        ((Activity)context).startActivity(intent);
                                        ((Activity)context).overridePendingTransition(0, 0);
                                        Toast.makeText(context,"[" + shopItem.getItemName() + "] 구매하였습니다.",Toast.LENGTH_SHORT).show();
                                        Intent intent2 = new Intent(context, ItemchangeActivity.class);
                                        intent2.putExtra("itemName", shopItem.getItemName());
                                        context.startActivity(intent2);

                                    }


                                });


                            }
                            else
                            {
                                Toast.makeText(context,"포인트가 부족합니다.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    builder.show();
                }
            });
        }

    }

    // saveImageToGallery 메서드 수정
    private void saveImageToGallery(Bitmap bitmap, String filename) {
        ContextWrapper wrapper = new ContextWrapper(context);
        File directory = wrapper.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, filename + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 갤러리에 이미지 추가
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + directory.getPath());
            values.put(MediaStore.Images.Media.TITLE, filename);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, filename + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.SIZE, file.length());

            ContentResolver contentResolver = context.getContentResolver();
            Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // 이미지 갤러리 갱신을 위해 브로드캐스트
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(imageUri);
            context.sendBroadcast(mediaScanIntent);

            Toast.makeText(context, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "이미지 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
