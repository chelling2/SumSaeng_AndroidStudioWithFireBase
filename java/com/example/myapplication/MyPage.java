package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class MyPage extends Fragment {

    String items[] = new String[]{"랭킹","포인트 교환","결제", "회원정보 수정", "1:1 문의", "공지", "로그아웃", "회원 탈퇴"};

    RecyclerView writingListRecyclerView;
    List<DataClass> dataClassList;
    MyAdapter myAdapter;

    ValueEventListener eventListener;

    ImageView profileImage;
    TextView tv_mpname, tv_writetitle, everyavg,tv_havemoney;

    int sum = 0, count = 0;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseUser user;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_page, container, false);

        tv_mpname = v.findViewById(R.id.tv_mpname);
        tv_writetitle = v.findViewById(R.id.tv_writetitle);
        profileImage = v.findViewById(R.id.mpprofileImage);
        tv_havemoney = v.findViewById(R.id.tv_havemoney);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("application");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        user = mFirebaseAuth.getCurrentUser();
        DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(user.getUid());
        DatabaseReference userRef2 = mDatabaseRef.child("UserAccount");

        ListView listView = v.findViewById(R.id.listviewMy);
        CustomItemsAdapter adapter = new CustomItemsAdapter(getContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> onListItemClick(position));

        userRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newSum = 0;
                int newCount = 0;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserAccount ua = userSnapshot.getValue(UserAccount.class);

                    newCount++;
                    newSum += ua.getPoint();
                }

                sum = newSum;
                count = newCount;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 에러 처리
            }
        });

        writingListRecyclerView = v.findViewById(R.id.writinglist);
        dataClassList = new ArrayList<>();
        myAdapter = new MyAdapter(getContext(), dataClassList);
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        writingListRecyclerView.setLayoutManager(layoutManager);
        writingListRecyclerView.setAdapter(myAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);
        writingListRecyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();

        dataClassList = new ArrayList<>();
        myAdapter = new MyAdapter(requireContext(), dataClassList);
        writingListRecyclerView.setAdapter(myAdapter);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Apple");
        dialog.show();

        eventListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataClassList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    if (dataClass.getUserId().equals(user.getUid())) {
                        dataClass.setKey(itemSnapshot.getKey());
                        dataClassList.add(dataClass);
                    }
                }

                myAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        if (user != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    View progressView = getView().findViewById(R.id.loading);


                    progressView.setVisibility(View.GONE);

                    if (snapshot.exists()) {
                        String name = snapshot.child("nickname").getValue(String.class);
                        Integer point = snapshot.child("point").getValue(Integer.class);
                        String money = snapshot.child("money").getValue(String.class);
                        tv_havemoney.setText(money+"원");
                        tv_mpname.setText(name + "님");
                        tv_writetitle.setText(point + "점");

                        StorageReference profileRef = mStorageReference.child("users/").child(user.getUid());

                        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Glide.with(MyPage.this)
                                    .load(uri)
                                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                    .into(profileImage);
                        }).addOnFailureListener(exception -> {
                            profileImage.setImageResource(R.drawable.perprofile);
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    String errorMessage = "데이터 가져오기 실패: " + error.getMessage();
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Intent loginIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(loginIntent);
        }

        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PersonalProfileActivity.class);
            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            intent.putExtra("authorId", currentUser);
            startActivity(intent);
        });

        return v;
    }

    private void onListItemClick(int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(getActivity(),RankingActivity.class);
                startActivity(intent);
                break;
            case 1:
                Intent intent6 = new Intent(getActivity(), ShopActivity.class);
                startActivity(intent6);
                break;
            case 2:
                Intent intent0 = new Intent(getActivity(), PayMentActivity.class);
                startActivity(intent0);
                break;
            case 3:
                Intent intent1 = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent1);
                break;
            case 4:
                handleInquireClick();
                break;
            case 5:
                Intent intent3 = new Intent(getActivity(), NoticeActivity.class);
                startActivity(intent3);
                break;
            case 6:
                handleSignOut();
                break;
            case 7:
                handleWithdrawal();
                break;
        }
    }

    private void handleInquireClick() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String currentUserEmail = currentUser.getEmail().trim().toLowerCase();
                            boolean isAdmin = currentUserEmail.equals("관리자 이메일!");

                            Intent intent = new Intent(getActivity(), isAdmin ? InquireAdminChatActivity.class : InquireChatActivity.class);
                            startActivity(intent);
                        } else {
                            // 인증 작업 실패 시 처리
                        }
                    });
        }
    }

    private void handleSignOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void handleWithdrawal() {
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.delete();
            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).removeValue();
        }
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}