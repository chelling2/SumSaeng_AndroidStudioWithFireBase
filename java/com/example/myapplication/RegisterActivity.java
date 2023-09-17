package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtPwd,mEteducation; //회원가입 입력필드
    private Button mBtnRegister; //회원가입 버튼
    private EditText mEtNickname;
    private Spinner spinner;
    private String edu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("application");
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnRegister = findViewById(R.id.btn_register);
        mEtNickname = findViewById(R.id.et_nickname);

//        mEteducation = findViewById(R.id.et_edu);
        spinner = (Spinner)findViewById(R.id.spinner);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                switch (parent.getItemAtPosition(position).toString()) {
                    case "학력" :
                    case "초등학생":
                        edu = "초등학생";
                        break;
                    case "중학생":
                        edu = "중학생";
                        break;
                    case "고등학생":
                        edu = "고등학생";
                        break;
                    case "대학생":
                        edu = "대학생";
                        break;

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register() {
        {

            //회원가입 시작
            String strEmail = mEtEmail.getText().toString();
            String strPwd = mEtPwd.getText().toString();
            String strNickname = mEtNickname.getText().toString();

            if (TextUtils.isEmpty(strEmail)) {
                Toast.makeText(this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(strPwd)) {
                Toast.makeText(this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(strNickname)) {
                Toast.makeText(this, "닉네임을 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (strPwd.length() < 8) {
                // 비밀번호가 8자리 이상이 아닐 경우 예외 처리
                Toast.makeText(this, "비밀번호는 8자리 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("application").child("UserAccount");

            usersRef.orderByChild("nickname").equalTo(strNickname).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // 닉네임이 이미 사용 중인 경우 예외 처리
                        Toast.makeText(RegisterActivity.this, "이미 사용 중인 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                UserAccount account = new UserAccount();
                                account.setIdToken(firebaseUser.getUid());
                                account.setEmailId(firebaseUser.getEmail());
                                account.setPassword(strPwd);
                                account.setNickname(strNickname);
                                account.setEducation(edu);
                                account.setVerified("0");
                                account.setPoint(0);
                                account.setMoney("0");
                                account.setEducation(edu);
                                account.setRating("브론즈");

                                // setValue: 데이터베이스에 사용자 정보 저장
                                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> databaseTask) {
                                                if (databaseTask.isSuccessful()) {
                                                    // 회원 정보 저장 성공 시
                                                    firebaseUser.sendEmailVerification()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> emailTask) {
                                                                    if (emailTask.isSuccessful()) {
                                                                        // 이메일 전송 성공 시
                                                                        Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다. 이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        // 이메일 전송 실패 시
                                                                        Toast.makeText(RegisterActivity.this, "이메일 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                            }


                    });


                }



    }
}




