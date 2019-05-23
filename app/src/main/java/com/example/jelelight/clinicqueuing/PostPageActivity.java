package com.example.jelelight.clinicqueuing;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PostPageActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefPost,mRefClinic,mUserPatient;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private EditText post_Edit;
    private Button post_Btn;

    private Calendar cld;

    private RecyclerView mRecyclerView;

    private List<String> postList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post_page);
        cld = Calendar.getInstance();
        mRecyclerView = findViewById(R.id.recycler_post);
        bind();
        new PostDB().readPost(new PostDB.DataStatus() {
            @Override
            public void DataIsLoaded(List<Post> posts, List<String> keys) {
                new RecyclePostConfig().setConfig(mRecyclerView,getApplicationContext(),posts,keys);

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
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRefClinic = mDatabase.getReference("Clinic");
        mRefPost = mDatabase.getReference("Clinic").child("Post");
        mUserPatient = mDatabase.getReference("Users");

        readPostCount();

        post_Edit = findViewById(R.id.post_edit);
        post_Btn = findViewById(R.id.post_btn);

        post_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(post_Edit.getText().toString() != "" && !post_Edit.getText().toString().equals("")){
                    mRefPost.child(String.valueOf(postList.size())).child("author").setValue(mUser.getDisplayName().toString());
                    mRefPost.child(String.valueOf(postList.size())).child("text").setValue(post_Edit.getText().toString());

                    String t = String.valueOf(cld.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(cld.get(Calendar.MINUTE))
                            + " " + String.valueOf(cld.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(cld.get(Calendar.MONTH))
                            + "/" + String.valueOf(cld.get(Calendar.YEAR));

                    mRefPost.child(String.valueOf(postList.size())).child("time").setValue(t);

                    post_Edit.setText("");
                }else{
                    Toast.makeText(PostPageActivity.this, "Please enter your text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void readPostCount(){
        mRefPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    postList.add(node.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
