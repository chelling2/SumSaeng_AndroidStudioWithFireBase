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
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private List<UserAccount> userAccounts = new ArrayList<>();

    public void setUsers(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserAccount userAccount = userAccounts.get(position);

        if (userAccount.getPoint() >= 0) {
            holder.bindData(position + 1, userAccount);
        }
    }

    @Override
    public int getItemCount() {
        return userAccounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView pointTextView, rankingTextView;
        ImageView imageView;
        ConstraintLayout cl;

        private FirebaseAuth mFirebaseAuth;
        private DatabaseReference mDatabaseRef;
        private FirebaseUser user;

        private FirebaseStorage mFirebaseStorage;
        private StorageReference mStorageReference;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            pointTextView = itemView.findViewById(R.id.pointTextView);
            rankingTextView = itemView.findViewById(R.id.rankingTextView);
            cl = itemView.findViewById(R.id.cl);
            imageView = itemView.findViewById(R.id.imageView);

            mFirebaseAuth = FirebaseAuth.getInstance();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("application");

            mFirebaseStorage = FirebaseStorage.getInstance();
            mStorageReference = mFirebaseStorage.getReference();
            user = mFirebaseAuth.getCurrentUser();

            cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = getAdapterPosition();
                    if (clickedPosition != RecyclerView.NO_POSITION) {
                        UserAccount clickedUser = userAccounts.get(clickedPosition);
                        Context context = itemView.getContext();
                        Intent intent = new Intent(context, PersonalProfileActivity.class);
                        intent.putExtra("authorId", clickedUser.getIdToken());
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bindData(int ranking, UserAccount userAccount) {
            rankingTextView.setText(ranking + "등");
            nameTextView.setText(userAccount.getNickname());
            pointTextView.setText(String.valueOf(userAccount.getPoint()));

            // Firebase 데이터 가져오는 코드
            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(userAccount.getIdToken());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Firebase 데이터 가져오는 코드

                    mStorageReference = mFirebaseStorage.getReference().child("users/").child(userAccount.getIdToken());
                    mStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        Glide.with(itemView.getContext())
                                .load(uri)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .into(imageView);

                        int textColor;
                        if (ranking == 1) {
                            textColor = itemView.getContext().getColor(R.color.gold); // 금색
                            Log.d("RankAdapter", "1등 색: 금색");
                        } else if (ranking == 2) {
                            textColor = itemView.getContext().getColor(R.color.silver); // 은색
                            Log.d("RankAdapter", "2등 색: 은색");
                        } else if (ranking == 3) {
                            textColor = itemView.getContext().getColor(R.color.bronze); // 동색
                            Log.d("RankAdapter", "3등 색: 동색");
                        } else {
                            textColor = itemView.getContext().getColor(R.color.green_3); // 기본색
                            Log.d("RankAdapter", "기본 색: 기본색");
                        }
                        Log.d("RankAdapter", "ranking 값: " + ranking);
                        rankingTextView.setTextColor(textColor);
                    }).addOnFailureListener(exception -> {
                        imageView.setImageResource(R.drawable.perprofile);

                        int textColor;
                        if (ranking == 1) {
                            textColor = itemView.getContext().getColor(R.color.gold); // 금색
                            Log.d("RankAdapter", "1등 색: 금색");
                        } else if (ranking == 2) {
                            textColor = itemView.getContext().getColor(R.color.silver); // 은색
                            Log.d("RankAdapter", "2등 색: 은색");
                        } else if (ranking == 3) {
                            textColor = itemView.getContext().getColor(R.color.bronze); // 동색
                            Log.d("RankAdapter", "3등 색: 동색");
                        } else {
                            textColor = itemView.getContext().getColor(R.color.green_3); // 기본색
                            Log.d("RankAdapter", "기본 색: 기본색");
                        }
                        Log.d("RankAdapter", "ranking 값: " + ranking);
                        rankingTextView.setTextColor(textColor);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}