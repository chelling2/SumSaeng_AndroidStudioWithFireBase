package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class PayMentActivity extends AppCompatActivity {

    TextView tv_point,tv_charge,tv_10000,tv_30000,tv_50000,tv_100000;
    Button btn_10000,btn_30000,btn_50000,btn_100000,pointback;
    String productName;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    String productPrice;
    FirebaseDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        btn_10000 = findViewById(R.id.btn_10000);
        btn_30000 = findViewById(R.id.btn_30000);
        btn_50000 = findViewById(R.id.btn_50000);
        btn_100000 = findViewById(R.id.btn_100000);
        tv_10000 = findViewById(R.id.tv_10000);
        tv_30000 = findViewById(R.id.tv_30000);
        tv_50000 = findViewById(R.id.tv_50000);
        tv_100000 = findViewById(R.id.tv_100000);
        tv_charge = findViewById(R.id.chargepoint);
        pointback=findViewById(R.id.pointback);


        pointback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productName = "10,000포인트";
                productPrice = "10000";
                pay();

            }
        });
        btn_30000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productName = "30,000포인트";
                productPrice = "30000";
                pay();
            }
        });
        btn_50000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productName = "50,000포인트";
                productPrice = "50000";
                pay();
            }
        });
        btn_100000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productName = "100,000포인트";
                productPrice = "100000";
                pay();
            }
        });


        tv_10000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });
        tv_30000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });
        tv_50000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });
        tv_100000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay();
            }
        });

    }
    public void pay() {
        Intent intent = new Intent(PayMentActivity.this, PayActivity.class);
        intent.putExtra("productPrice", productPrice);
        intent.putExtra("productName", productName);
        startActivity(intent);
    }
}