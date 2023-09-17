package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private List<Review> reviewList;
    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.bindReview(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}

class ReviewViewHolder extends RecyclerView.ViewHolder {

    private RatingBar ratingBar;
    private TextView reviewTextView, tvReviewUserName;

    private ImageView reviewImage;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser user;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;


    public ReviewViewHolder(@NonNull View itemView) {
        super(itemView);
        ratingBar = itemView.findViewById(R.id.reviewRatingBar);
        reviewTextView = itemView.findViewById(R.id.reviewTextView);
        tvReviewUserName = itemView.findViewById(R.id.tvReviewUserName);
        reviewImage = itemView.findViewById(R.id.reviewImage);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("application");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        user = mFirebaseAuth.getCurrentUser();
    }



    public void bindReview(Review review) {

        ratingBar.setRating(review.getRating());
        reviewTextView.setText(review.getReview());

        if (review.getUserId() != null) {

            DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(review.getUserId());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("nickname").getValue(String.class);

                        tvReviewUserName.setText(name + "님");
                    }
                    mStorageReference = mFirebaseStorage.getReference().child("users/").child(review.getUserId());
                    mStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        Glide.with(itemView.getContext())
                                .load(uri)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop())) // 원형으로 바꾸는 방법이래요~
                                .into(reviewImage);
                    }).addOnFailureListener(exception -> {

                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            reviewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // reviewImage를 클릭했을 때 실행되는 코드 블록

                    // 예를 들어, ReviewDetailActivity로 전환하고자 한다면 다음과 같이 Intent를 사용할 수 있습니다.
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, PersonalProfileActivity.class);

                    // 전환에 필요한 데이터를 Intent에 추가할 수도 있습니다.
                    intent.putExtra("authorId",review.getUserId()); // 예시로 reviewId를 넘기는 경우

                    context.startActivity(intent);
                }
            });

        }
    }
}


