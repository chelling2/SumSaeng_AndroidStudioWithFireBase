package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ipsec.ike.IkeSessionCallback;
import android.net.ipsec.ike.IkeSessionConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


import kotlin.Unit;
import kotlin.jvm.functions.Function2;


public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; //로그인 입력필드

    private FirebaseUser User; //현재 로그인 된 유저 정보를 담을 변수
    private String currentUserUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("application");
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        User = mFirebaseAuth.getInstance().getCurrentUser();



//        // 로그인 버튼 클릭 이벤트
//        Button button = findViewById(R.id.btn_kakao);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UserApiClient.getInstance().loginWithKakaoAccount(getApplicationContext(), new Function2<OAuthToken, Throwable, Unit>() {
//                    @Override
//                    public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
//
//                        if (throwable != null) {
//                            Log.e("Debug", "로그인 실패!");
//                        } else if (oAuthToken != null) {
//                            Log.e("Debug", "로그인 성공!");
//                            // 로그인 성공 시 사용자 정보 받기
//                            UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
//                                @Override
//                                public Unit invoke(User user, Throwable throwable) {
//                                    if (throwable != null) {
//                                        Log.e("Deubg", "사용자 정보 받기 실패!");
//                                    } else if (user != null) {
//                                        Log.e("Debug", "사용자 정보 받기 성공!");
//
//                                    }
//                                    return null;
//                                }
//                            });
//                        }
//                        return null;
//                    }
//                });
//            }
//        });

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

                            if (currentUser != null && currentUser.isEmailVerified()) {
                                if (currentUser.getEmail().equals("관리자 이메일!")) {
                                    Log.d("LoginActivity", "관리자로 로그인되었습니다.");
                                    currentUserUid = currentUser.getUid(); // 현재 사용자의 UID 저장
                                    Log.d("LoginActivity", "Current user UID: " + currentUserUid);
                                }

                                if (currentUser != null) {
                                    // 현재 로그인한 사용자 정보를 다시 가져옴
                                    currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> reloadTask) {
                                            if (reloadTask.isSuccessful()) {
                                                if (currentUser.isEmailVerified()) {
                                                    // 이메일이 인증된 경우
                                                    DatabaseReference userRef = mDatabaseRef.child("UserAccount").child(currentUser.getUid());
                                                    userRef.child("verified").setValue("1");
                                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                String verified = dataSnapshot.child("verified").getValue(String.class);

                                                                if (verified != null && verified.equals("1")) {
                                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(LoginActivity.this, "회원 정보가 확인되지 않습니다.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            // 데이터베이스 읽기 오류 처리
                                                        }
                                                    });
                                                } else {
                                                    // 이메일이 인증되지 않은 경우
                                                    Toast.makeText(LoginActivity.this, "이메일 인증되지 않았습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "아이디, 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        //회원가입 버튼
        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //비밀번호 재설정
        Button btn_reset = findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //아이디 비밀번호 재설정 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, UserresetActivity.class);
                startActivity(intent);
            }
        });
    }


//    //로그아웃 안했으면, 즉 로그인 되어있으면 자동으로 메인페이지로 이동시키기 [자동로그인]
//    @Override
//    public void onStart() {
//        super.onStart();
//        User = mFirebaseAuth.getCurrentUser();
//        if(User != null){
//            startActivity(new Intent(LoginActivity.this, MyPage.class));
//            finish();
//        }
//    }
}