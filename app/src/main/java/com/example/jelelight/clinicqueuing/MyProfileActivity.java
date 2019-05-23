package com.example.jelelight.clinicqueuing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabasae;
    private DatabaseReference mReferenceProfile;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private TextView uidView,statusView,nameView;
    private Button editBtn,backBtn;
    private ImageButton copyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_profile);
        bind();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
        pageManage();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editProfile_Btn:
                    startActivity(new Intent(MyProfileActivity.this,EditProfileActivity.class));
                    break;
                case R.id.backProfile_Btn:
                    onBackPressed();
                    finish();
                    break;
                case R.id.copy_profile:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("UserID", uidView.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MyProfileActivity.this, "Copied to Clipboard.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void bind(){
        nameView = findViewById(R.id.name_profile);
        statusView = findViewById(R.id.status_profile);
        uidView = findViewById(R.id.uid_profile);

        copyBtn = findViewById(R.id.copy_profile);
        editBtn = findViewById(R.id.editProfile_Btn);
        backBtn = findViewById(R.id.backProfile_Btn);

        copyBtn.setOnClickListener(onClickListener);
        editBtn.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);

    }

    private void pageManage(){

        mDatabasae = FirebaseDatabase.getInstance();
        mReferenceProfile = mDatabasae.getReference("Officers");

        uidView.setText(mUser.getUid());
        readProfiles();

    }

    private void readProfiles(){
        mReferenceProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    if(node.getKey().toString().equals(mUser.getUid().toString())){
                        if(node.child("name").exists()) {
                            nameView.setText(node.child("name").getValue().toString());
                        }else{
                            nameView.setText(mUser.getDisplayName());
                            mReferenceProfile.child(node.getKey()).child("name").setValue(mUser.getDisplayName());
                        }

                        if(node.child("status").exists()) {
                            statusView.setText(node.child("status").getValue().toString());
                        }else{
                            statusView.setText("Idle");
                            mReferenceProfile.child(node.getKey()).child("status").setValue("idle");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
