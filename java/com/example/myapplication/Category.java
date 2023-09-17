package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;


public class Category extends Fragment {

    RelativeLayout college, senior, junior, elementary;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);

        college = v.findViewById(R.id.college);
        senior = v.findViewById(R.id.high);
        junior = v.findViewById(R.id.middle);
        elementary = v.findViewById(R.id.elementary);

        college.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("대학생");
            }
        });

        senior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("고등학생");
            }
        });

        junior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("중학생");
            }
        });

        elementary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("초등학생");
            }
        });



        return v;
    }
    private void openCategoryActivity(String category) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}