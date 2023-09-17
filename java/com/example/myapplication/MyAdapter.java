package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DataClass data = dataList.get(position);
        int price = data.getPrice();
        DecimalFormat decimalFormat = new DecimalFormat("#,###"); // 创建一个带千位分隔符的格式
        String formattedPrice = decimalFormat.format(price);

        if (data.getUserId().isEmpty()) {
            // 這是空白項目，隱藏點擊事件
            holder.recCard.setOnClickListener(null);
        } else {
//        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
            holder.recTitle.setText(dataList.get(position).getDataTitle());
            holder.recDesc.setText(dataList.get(position).getDataDesc());

            holder.tvRIXPrice.setText(data.isTransaction() ? "": formattedPrice + "원");
            holder.tvRIXTran.setText(data.isTransaction() ? "거래완료" : "");


            holder.recCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
//                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());

                    intent.putStringArrayListExtra("Image", new ArrayList<>(dataList.get(holder.getAdapterPosition()).getDataImage()));

                    intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
                    intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
                    intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
                    intent.putExtra("UserId", dataList.get(holder.getAdapterPosition()).getUserId());
                    intent.putExtra("education", dataList.get(holder.getAdapterPosition()).getEducation());
                    intent.putExtra("Price", String.valueOf(dataList.get(holder.getAdapterPosition()).getPrice())); // new
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle, recDesc, tvRIXPrice, tvRIXTran;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

//        recImage = itemView.findViewById(R.id.recImage);
        tvRIXPrice = itemView.findViewById(R.id.tvRIXPrice);
        tvRIXTran = itemView.findViewById(R.id.tvRIXTran);

        recCard = itemView.findViewById(R.id.recCard1);
        recDesc = itemView.findViewById(R.id.recDesc);
        recTitle = itemView.findViewById(R.id.recTitle);
    }
}