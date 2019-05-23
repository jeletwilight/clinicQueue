package com.example.jelelight.clinicqueuing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefDoctor;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ProgressBar progress;
    private EditText idField,pwField;
    private Button loginBtn;
    private TextView regBtn;

    private List<Doctor> doctorList = new ArrayList<>();
    private List<String> validEmail = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        bind();
        progress.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        validEmail.clear();
        pageManage();
        if(mUser != null){
            Intent i = new Intent(MainActivity.this,ManagerActivity.class);
            startActivity(i);
            finish();
        }

    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginBtn:
                    loginPress();
                    break;
                case R.id.reg:
                    Intent i = new Intent(MainActivity.this,SignUpActivity.class);
                    startActivity(i);
                    break;
            }
            hideKeyboardInput(v);
        }
    };

    private void bind(){

        progress = findViewById(R.id.progressBarLogin);
        loginBtn = findViewById(R.id.loginBtn);
        regBtn = findViewById(R.id.reg);
        idField = findViewById(R.id.idText);
        pwField = findViewById(R.id.pwText);

        loginBtn.setOnClickListener(onClickListener);
        regBtn.setOnClickListener(onClickListener);
    }

    private void pageManage(){
        mRefDoctor = mDatabase.getReference("Officers");
        checkValid();
    }


    private void hideKeyboardInput(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void loginPress(){
        if(idField.getText().toString().equals("") || pwField.getText().toString().equals("")){
            Toast.makeText(this, "Please enter ID and Password", Toast.LENGTH_SHORT).show();
        } else {
            String email = idField.getText().toString();
            String password = pwField.getText().toString();
            if (validEmail.contains(email)) {
                progress.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MainLogin", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progress.setVisibility(View.GONE);
                            Intent loginIntent = new Intent(MainActivity.this, ManagerActivity.class);
                            MainActivity.this.startActivity(loginIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progress.setVisibility(View.GONE);
                            Log.w("MainLogin", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                        }
                    });
            } else {
                progress.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        //Intent loginIntent = new Intent(MainActivity.this , LobbyActivity.class);
        //MainActivity.this.startActivity(loginIntent);

        //Intent i = new Intent(MainActivity.this,ManagerActivity.class);
        //startActivity(i);
    }

    private void checkValid(){
        mRefDoctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorList.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keynode : dataSnapshot.getChildren()){
                    keys.add(keynode.getKey());
                    Doctor doctor = keynode.getValue(Doctor.class);
                    doctor.setName(keynode.child("name").getValue(String.class));
                    doctor.setEmail(keynode.child("email").getValue(String.class));
                    if(!validEmail.contains(keynode.child("email").getValue(String.class))){
                        validEmail.add(keynode.child("email").getValue(String.class));
                    }
                    doctor.setStatus(keynode.child("status").getValue(String.class));
                    doctorList.add(doctor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
