package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NoticeAdmin extends AppCompatActivity {

    private EditText noticeTitleEditText;
    private EditText noticeTextEditText;
    private Button noticeSaveButton;

    private DatabaseReference noticesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_admin);

        noticeTitleEditText = findViewById(R.id.noticeTitle);
        noticeTextEditText = findViewById(R.id.noticeText);
        noticeSaveButton = findViewById(R.id.notice_save);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        noticesRef = database.getReference("notices");

        noticeSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotice();
            }
        });
    }

    private void saveNotice() {
        String title = noticeTitleEditText.getText().toString();
        String content = noticeTextEditText.getText().toString();

        if (!title.isEmpty() && !content.isEmpty()) {
            String noticeId = noticesRef.push().getKey();
            Notice notice = new Notice(title, content);

            if (noticeId != null) {
                noticesRef.child(noticeId).setValue(notice);
                noticeTitleEditText.setText("");
                noticeTextEditText.setText("");
            }
        }
    }
}


