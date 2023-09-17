package com.example.myapplication;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Uri> imageUris;

    public PhotoAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        // 使用Glide或其他圖片加載庫來加載圖片
        Glide.with(holder.itemView.getContext()).load(imageUri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    // 데이터 변경 시 RecyclerView에 알림
    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
        notifyDataSetChanged(); // 데이터 변경 후 RecyclerView에 알림
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        Button picturedelete;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoImageView);
            picturedelete = itemView.findViewById(R.id.picturedelete);


            picturedelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition(); // 삭제하려는 아이템의 위치를 가져옴
                    if (position != RecyclerView.NO_POSITION) {
                        // 해당 위치의 아이템을 데이터 리스트에서 삭제
                        imageUris.remove(position);
                        // 어댑터에 아이템 삭제를 알림
                        notifyItemRemoved(position);
                    }
                }
            });
        }
    }
}


