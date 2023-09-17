package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomItemsAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] items;

    private int textColor;

    public CustomItemsAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;

        Resources res = context.getResources();
        textColor = res.getColor(R.color.green_7);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(items[position]);

        // 원하는 스타일 적용
        textView.setTextSize(18); // 텍스트 크기 설정
        textView.setTextColor(textColor); // 텍스트 색상 설정

        return view;
    }
}
