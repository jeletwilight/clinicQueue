package com.example.jelelight.clinicqueuing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
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

public class ManagerActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private DatabaseReference mRefQueue,mRefCStat,mRefOfficer,mRefBqueue;

    private TextView sickCount,bCount;
    private Button genBtn,roomBtn,setBtn;
    private Switch statSw;
    private Menu menu;

    private List<String> queueList = new ArrayList<>();
    private List<String> bQueueList = new ArrayList<>();
    private List<String> myStatus = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        bind();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            Intent i = new Intent(ManagerActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        pageManage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.custom_menu,menu);
        this.menu = menu;
        menu.findItem(R.id.profileName).setTitle(mUser.getDisplayName());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_profile1:
                startActivity(new Intent(getApplicationContext(),MyProfileActivity.class));
                return true;
            case R.id.action_profile2:
                if(!myStatus.isEmpty()) {
                    if (myStatus.get(0).toString().equals("Room1")) {
                        startActivity(new Intent(getApplicationContext(), InRoomActivity.class));
                    } else if (myStatus.get(0).toString().equals("Room2")) {
                        startActivity(new Intent(getApplicationContext(), InRoom2Activity.class));
                    } else if (myStatus.get(0).toString().equals("Room3")) {
                        startActivity(new Intent(getApplicationContext(), InRoom3Activity.class));
                    } else if (myStatus.get(0).toString().equals("Room4")) {
                        startActivity(new Intent(getApplicationContext(), InRoom4Activity.class));
                    } else {
                        Toast.makeText(this, "You are not in any Room", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            case R.id.action_profile3:
                mAuth.getInstance().signOut();
                Toast.makeText(this, "Logged Out.",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ManagerActivity.this,MainActivity.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnClickListener  onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.to_gen_btn:
                    Intent i = new Intent(ManagerActivity.this,GenQueueActivity.class);
                    startActivity(i);
                    break;
                case R.id.room_btn:
                    Intent i2 = new Intent(ManagerActivity.this,RoomActivity.class);
                    startActivity(i2);
                    break;
                case R.id.manage_btn:
                    startActivity(new Intent(ManagerActivity.this,PostPageActivity.class));
                    break;
                case R.id.sickCount_txt:
                    Intent i4 = new Intent(ManagerActivity.this,AllQueueActivity.class);
                    startActivity(i4);
                    break;
                case R.id.acdCount_txt:
                    startActivity(new Intent(getApplicationContext(),BandageQueueActivity.class));
                    break;
            }
        }
    };

    private void bind(){
        sickCount = findViewById(R.id.sickCount_txt);
        bCount = findViewById(R.id.acdCount_txt);
        statSw = findViewById(R.id.clinic_switch);
        genBtn = findViewById(R.id.to_gen_btn);
        roomBtn = findViewById(R.id.room_btn);
        setBtn = findViewById(R.id.manage_btn);

        bCount.setOnClickListener(onClickListener);
        sickCount.setOnClickListener(onClickListener);
        genBtn.setOnClickListener(onClickListener);
        roomBtn.setOnClickListener(onClickListener);
        setBtn.setOnClickListener(onClickListener);

        genBtn.setVisibility(View.INVISIBLE);

        statSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mRefCStat.setValue(Boolean.TRUE);
                }else{
                    mRefCStat.setValue(Boolean.FALSE);
                }
            }
        });
    }

    private void pageManage(){
        mDatabase = FirebaseDatabase.getInstance();
        mRefQueue = mDatabase.getReference("Clinic").child("queues");
        mRefCStat = mDatabase.getReference("Clinic").child("status");
        mRefOfficer = mDatabase.getReference("Officers");
        mRefBqueue = mDatabase.getReference("Clinic").child("Bqueues");

        readBqueue();
        readCount();
        readClinicStatus();
        getOfficerRoom();
    }

    private void readCount(){
        mRefQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                queueList.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    queueList.add(keyNode.getKey());
                }
                sickCount.setText(String.valueOf(queueList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readClinicStatus(){
        mRefCStat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class).equals(Boolean.TRUE)){
                    statSw.setChecked(true);
                }else{
                    statSw.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getOfficerRoom(){
        mRefOfficer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myStatus.clear();
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    if(node.child("status").exists()) {
                        if (node.getKey().toString().equals(mUser.getUid().toString())) {
                            myStatus.add(node.child("status").getValue().toString());
                        }
                    }else{
                        mRefOfficer.child(node.getKey()).child("status").setValue("idle");
                        myStatus.add("idle");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readBqueue(){
        mRefBqueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bQueueList.clear();
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    bQueueList.add(node.getKey());
                }
                bCount.setText(String.valueOf(bQueueList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
