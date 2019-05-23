package com.example.jelelight.clinicqueuing;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private DatabaseReference mRefRoom;

    private List<String> doctor = new ArrayList<>();
    private List<String> patient = new ArrayList<>();

    private TextView doctor1,doctor2,doctor3,doctor4,patient1,patient2,patient3,patient4;
    private LinearLayout room1,room2,room3,room4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        bind();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(RoomActivity.this,MainActivity.class));
            finish();
        }
        pageManage();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.room1_layout:
                    startActivity(new Intent(RoomActivity.this,InRoomActivity.class));
                    break;
                case R.id.room2_layout:
                    startActivity(new Intent(RoomActivity.this,InRoom2Activity.class));
                    break;
                case R.id.room3_layout:
                    startActivity(new Intent(RoomActivity.this,InRoom3Activity.class));
                    break;
                case R.id.room4_layout:
                    startActivity(new Intent(RoomActivity.this,InRoom4Activity.class));
                    break;
            }
        }
    };

    private void bind(){
        doctor1 = findViewById(R.id.doctor1_tv);
        doctor2 = findViewById(R.id.doctor2_tv);
        doctor3 = findViewById(R.id.doctor3_tv);
        doctor4 = findViewById(R.id.doctor4_tv);
        patient1 = findViewById(R.id.patient1_tv);
        patient2 = findViewById(R.id.patient2_tv);
        patient3 = findViewById(R.id.patient3_tv);
        patient4 = findViewById(R.id.patient4_tv);

        room1 = findViewById(R.id.room1_layout);
        room2 = findViewById(R.id.room2_layout);
        room3 = findViewById(R.id.room3_layout);
        room4 = findViewById(R.id.room4_layout);

        room1.setOnClickListener(onClickListener);
        room2.setOnClickListener(onClickListener);
        room3.setOnClickListener(onClickListener);
        room4.setOnClickListener(onClickListener);
    }

    private void pageManage(){
        mRefRoom = mDatabase.getReference("Clinic").child("rooms");

        callRoom();
    }

    private void callRoom(){
        mRefRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctor.clear();
                patient.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    doctor.add(keyNode.child("doctor").getValue(String.class));
                    patient.add(keyNode.child("patient").getValue(String.class));
                }
                doctor1.setText(doctor.get(0));
                doctor2.setText(doctor.get(1));
                doctor3.setText(doctor.get(2));
                doctor4.setText(doctor.get(3));

                patient1.setText(patient.get(0));
                patient2.setText(patient.get(1));
                patient3.setText(patient.get(2));
                patient4.setText(patient.get(3));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
