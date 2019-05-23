package com.example.jelelight.clinicqueuing;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDB {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceQueue;
    private List<Queue> queues = new ArrayList<>();

    public FirebaseDB(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceQueue = mDatabase.getReference("Clinic").child("queues");
    }

    public interface DataStatus{
        void DataIsLoaded(List<Queue> queues,List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }


    public void readQueues(final DataStatus dataStatus){
        mReferenceQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                queues.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Queue queue = keyNode.getValue(Queue.class);
                    queue.setPatientCase(keyNode.child("case").getValue(String.class));
                    queue.setPatientID(keyNode.child("user").getValue(String.class));
                    if(!keyNode.child("status").exists()) {
                        mReferenceQueue.child(String.valueOf(keyNode.getKey())).child("status").setValue("waiting");
                    }
                    queue.setStatus(keyNode.child("status").getValue(String.class));
                    queues.add(queue);
                }
                dataStatus.DataIsLoaded(queues,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
