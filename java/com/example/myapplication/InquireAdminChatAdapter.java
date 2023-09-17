package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InquireAdminChatAdapter extends RecyclerView.Adapter<InquireAdminChatAdapter.UserViewHolder> {


    private List<String> emailList;
    private Context context;

    public InquireAdminChatAdapter(List<String> emailList, Context context) {
        this.emailList = emailList;
        this.context = context;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inquire_admin_chatlist, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userEmail = emailList.get(position);
        holder.bind(userEmail);
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userEmailTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmailTextView = itemView.findViewById(R.id.contentTextView);
        }

        public void bind(String userEmail) {
            userEmailTextView.setText(userEmail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 해당 사용자와의 채팅 화면으로 이동하는 로직을 여기에 추가
                    Intent chatIntent = new Intent(context, Inquire_Admin_User_ChatActivity.class);
                    chatIntent.putExtra("userEmail", userEmail);
                    context.startActivity(chatIntent);
                }
            });
        }
    }
}
