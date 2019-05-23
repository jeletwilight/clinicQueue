package com.example.jelelight.clinicqueuing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser,mReferenceOfficer;
    private FirebaseAuth mAuth;
    private Button submitBtn,cancelBtn;
    private EditText setId,setPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        bindView();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference("Users");
        mReferenceOfficer = mDatabase.getReference("Officers");
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submitBtn:
                    submitClick();
                    break;
                case R.id.cancelBtn:
                    cancelClick();
                    break;
            }
        }
    };

    void bindView(){
        setId = findViewById(R.id.setId);
        setPass = findViewById(R.id.setPass);
        submitBtn = findViewById(R.id.submitBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        submitBtn.setOnClickListener(onClickListener);
        cancelBtn.setOnClickListener(onClickListener);
    }

    void submitClick(){
        String _id = setId.getText().toString();
        String _pass = setPass.getText().toString();

        if(checkSignUp(_id,_pass)) {
            String mName = _id;
            mName = mName.substring(0,_id.indexOf('@'));
            final String finalName = mName;
            mAuth.createUserWithEmailAndPassword(_id, _pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isComplete()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(finalName).build();
                                user.updateProfile(profileUpdates);
                                //Add to Officers DB//
                                mReferenceOfficer.child(userID).child("name").setValue(user.getDisplayName());
                                mReferenceOfficer.child(userID).child("status").setValue("idle");
                                mReferenceOfficer.child(userID).child("email").setValue(user.getEmail());
                                //------------------//
                                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Failed : Invalid ID or Password", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkSignUp(String id,String pass){

        boolean permit = true;

        if(id.contains("@cmu.ac.th") || id.contains("@gmail.com")){
            permit = true;
        }else{
            permit = false;
        }

        if(id == "" || pass == "" || id == null || pass == null){
            permit = false;
        }

        if(id.length() <= 10 || pass.length() <= 6) {
            permit = false;
        }

        return permit;
    }

    void cancelClick(){
        Intent cancelIntent = new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(cancelIntent);
    }

}
