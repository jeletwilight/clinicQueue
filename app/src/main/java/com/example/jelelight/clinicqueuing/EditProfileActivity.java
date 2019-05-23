package com.example.jelelight.clinicqueuing;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser,mReferenceCurrentUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Button submitBtn,backBtn;
    private EditText nameEdit;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);
        bind();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
        if(mUser == null){
            startActivity(new Intent(EditProfileActivity.this,MainActivity.class));
            finish();
        }
        pageManage();

    }

    private void bind(){
        nameEdit = findViewById(R.id.name_edit);
        submitBtn = findViewById(R.id.editSubmit_Btn);
        backBtn = findViewById(R.id.backEdit_Btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEdit.getText().toString() != "") {
                    mReferenceUser.child(mUser.getUid()).child("name").setValue(nameEdit.getText().toString());
                    UserProfileChangeRequest req = new UserProfileChangeRequest.Builder().setDisplayName(String.valueOf(nameEdit.getText())).build();
                    mUser.updateProfile(req);
                    Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    private void pageManage(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference("Officers");

        readProfile();
    }

    private void readProfile(){
        mReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    if(node.getKey().toString().equals(mUser.getUid().toString())){
                        nameEdit.setText(node.child("name").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
