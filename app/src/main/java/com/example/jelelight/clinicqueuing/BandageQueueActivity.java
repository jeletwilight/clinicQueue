package com.example.jelelight.clinicqueuing;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BandageQueueActivity extends AppCompatActivity {


    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefQueues,mRefClinic,mUserPatient,mRoom;

    private Button bClear_btn;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_bandage_queue);
        mRecyclerView = findViewById(R.id.recycler_bqueues);
        bind();
        new BDFirebaseDB().readQueues(new BDFirebaseDB.DataStatusB() {
            @Override
            public void DataIsLoadedB(List<Queue> queues, List<String> keys) {
                new RecyclerViewConfig().setConfig(mRecyclerView,BandageQueueActivity.this,queues,keys);

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

    private void bind(){
        mDatabase = FirebaseDatabase.getInstance();
        mRefClinic = mDatabase.getReference("Clinic");
        mRefQueues = mDatabase.getReference("Clinic").child("Bqueues");
        mRoom = mDatabase.getReference("Clinic").child("rooms");
        mUserPatient = mDatabase.getReference("Users");

        bClear_btn = findViewById(R.id.clear_all_btn_b);

        bClear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearQueueFromUser();
                clearAllRoom();
                mRefQueues.setValue(null);
                mRefClinic.child("Bqueue_count").setValue((int)0);
                mRefClinic.child("Bqueue_token").setValue((int)1);
            }
        });
    }

    private void clearQueueFromUser(){
        mUserPatient.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("queueType").exists()) {
                        if (keyNode.child("queueType").getValue().toString().equals("bandage")) {
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

    private void clearAllRoom(){
        mRoom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    if(node.child("type").equals("bandage")){
                        mRoom.child(node.getKey()).child("queue").setValue((Integer)0);
                        mRoom.child(node.getKey()).child("patient").setValue("Empty");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
