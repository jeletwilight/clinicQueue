package com.example.jelelight.clinicqueuing;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Room4ChooseActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefQueues,mRefClinic,mUserPatient,mRefRoom;

    private Button back_btn,choose_btn;
    private EditText queue_id_edit;

    private RecyclerView mRecyclerView;

    private List<String> nameList = new ArrayList<>();
    private List<String> idList = new ArrayList<>();
    private List<String> queueList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_room4_choose);
        mRecyclerView = findViewById(R.id.recycler_queues_c4);
        bind();
        new BDFirebaseDB().readQueues(new BDFirebaseDB.DataStatusB() {
            @Override
            public void DataIsLoadedB(List<Queue> queues, List<String> keys) {
                new RecyclerViewConfig().setConfig(mRecyclerView,getApplicationContext(),queues,keys);

            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        readAllPatient();
        readAllQueues();
    }

    private void bind(){
        mDatabase = FirebaseDatabase.getInstance();
        mRefClinic = mDatabase.getReference("Clinic");
        mRefQueues = mDatabase.getReference("Clinic").child("Bqueues");
        mUserPatient = mDatabase.getReference("Users");
        mRefRoom = mDatabase.getReference("Clinic").child("rooms");

        queue_id_edit = findViewById(R.id.choose_id_room4);
        choose_btn = findViewById(R.id.choose_btn_room4);
        back_btn = findViewById(R.id.back_btn_c4);

        choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(queue_id_edit.getText().toString() != "" && queueList.contains(queue_id_edit.getText().toString())) {
                    mSetWaitQueues();
                    mRefRoom.child("4").child("queue").setValue(Integer.valueOf(queue_id_edit.getText().toString()));
                    for(int i = 0; i<idList.size(); i++){
                        if(idList.get(i).toString() == queue_id_edit.getText().toString()){
                            mRefRoom.child("4").child("patient").setValue(nameList.get(i).toString());
                        }
                    }
                    setInRoom();
                    Toast.makeText(Room4ChooseActivity.this, "Patient Choosed", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(Room4ChooseActivity.this, "Please Enter Queue ID", Toast.LENGTH_SHORT).show();
                }
            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


    }

    private void clearQueueFromUser(){
        mUserPatient.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("queueType").exists()) {
                        if (keyNode.child("queueType").getValue().toString().equals("normal")) {
                            mUserPatient.child(keyNode.getKey()).child("inQueue").setValue(Boolean.FALSE);
                            mUserPatient.child(keyNode.getKey()).child("queueNo").setValue(null);
                            mUserPatient.child(keyNode.getKey()).child("queueType").setValue(null);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAllPatient(){
        mUserPatient.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                nameList.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("queueNo").exists()) {
                        idList.add(keyNode.child("queueNo").getValue().toString());
                        nameList.add(keyNode.child("name").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAllQueues(){
        mRefQueues.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                queueList.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    queueList.add(keyNode.getKey().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mSetWaitQueues(){
        mRefQueues.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("status").getValue().toString().equals("inRoom4")){
                        mRefQueues.child(keyNode.getKey()).child("status").setValue("waiting");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setInRoom(){
        mRefQueues.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.getKey().toString().equals(queue_id_edit.getText().toString())){
                        mRefQueues.child(keyNode.getKey()).child("status").setValue("inRoom4");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
