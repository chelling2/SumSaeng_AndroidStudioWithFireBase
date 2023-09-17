package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton sendCommissionButton;

    private long backBtnTime=0;

    @Override
    public void onBackPressed() {   // 백 버튼 눌렀을 때 뒤로가기 기능 활성화
        long curTime = System.currentTimeMillis();
        long gapTime = curTime-backBtnTime;
        //현재 시간을 가져와서 백버튼 누른 시간을 뺀 게 갭타임.

        if(0<=gapTime && 2000>= gapTime){
            super.onBackPressed(); //실행시 앱이 꺼지게 됨.
        }
        else{
            backBtnTime=curTime;
            Toast.makeText(this,"한번 더 누르면 종료됩니다!",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sendCommissionButton = findViewById(R.id.btSend);

        sendCommissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SendACommissionActivity.class);
                startActivity(intent);
            }
        });


        replaceFragment(new MainPage());
        binding.bottomNavigationView.setBackground(null);
//        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
//
//            switch (item.getItemId()) {
//                case R.id.home:
//                    replaceFragment(new MainPage());
//                    break;
//                case R.id.search:
//                    replaceFragment(new SearchPage());
//                    break;
//                case R.id.category:
//                    replaceFragment(new Category());
//                    break;
//                case R.id.myPage:
//                    replaceFragment(new MyPage());
//                    break;
//
//            }
//            return true;
//        });
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new MainPage());
            } else if (itemId == R.id.chat) {
                replaceFragment(new ChatPage());
            } else if (itemId == R.id.category) {
                replaceFragment(new Category());
            } else if (itemId == R.id.myPage) {
                replaceFragment(new MyPage());
            }

            return true;
        });




    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    }
