package com.example.jelelight.clinicqueuing;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDB {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceQueue;
    private List<Post> posts = new ArrayList<>();

    public PostDB(){
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceQueue = mDatabase.getReference("Clinic").child("Post");
    }

    public interface DataStatus{
        void DataIsLoaded(List<Post> posts,List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }


    public void readPost(final DataStatus dataStatus){
        mReferenceQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Post post = keyNode.getValue(Post.class);
                    post.setAuthor(keyNode.child("author").getValue(String.class));
                    //post.setKey(keyNode.getKey());
                    post.setText(keyNode.child("text").getValue(String.class));
                    post.setTime(keyNode.child("time").getValue(String.class));
                    posts.add(post);
                }
                dataStatus.DataIsLoaded(posts,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
