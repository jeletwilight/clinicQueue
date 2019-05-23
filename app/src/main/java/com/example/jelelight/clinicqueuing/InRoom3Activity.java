package com.example.jelelight.clinicqueuing;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InRoom3Activity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private DatabaseReference mRoom1,mClinic,mAllQueue,mPatients,mUserPatient,mOfficers;

    private TextView doc1,queue1,name1,gen1,blood1,birth1,weigh1,height1,cau1,note1,pstate1,phone1;
    private Button enterB,exitB, curedB, findNextB, chooseB,refB;
    private Integer oldPatient;

    private Boolean isAllQueue,isRoom,isToken,isCurrentQueueID,isInRoomSet,isPatientID;

    private List<Profile> thisProfile = new ArrayList<>();
    private List<Profile> profiles = new ArrayList<>();
    private List<String> allCounter = new ArrayList<>();
    private List<String> patientID= new ArrayList<>();
    private List<String> patientKeys = new ArrayList<>();
    private List<String> thisSymp = new ArrayList<>();
    private List<String> thisStatus = new ArrayList<>();
    private List<Integer> tokenQ = new ArrayList<>();
    private List<Integer> roomQ  = new ArrayList<>();
    private List<Boolean> gotProfile = new ArrayList<>();
    private List<String> waitingList = new ArrayList<>();
    private List<String> phoneNumber = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_in_room3);
        bind();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isInRoomSet = false;
        isCurrentQueueID = false;
        isRoom = false;
        isToken = false;
        isAllQueue = false;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(InRoom3Activity.this,MainActivity.class));
            finish();
        }
        pageManage();
    }

    private final View.OnClickListener  onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.enter_room3:
                    resetRoom();
                    setOfficerRoom();
                    mReadRoomQueue();
                    isInRoomSet = false;
                    mRoom1.child("doctor").setValue(mUser.getDisplayName());
                    mRoom1.child("status").setValue(Boolean.TRUE);
                    readThisProfile();
                    if(!roomQ.isEmpty() && !thisProfile.isEmpty()) {
                        updatePatient(thisProfile.get(0));
                    }
                    break;

                case R.id.exit_room3:
                    //setWaiting();
                    resetRoom();
                    setOfficerIdle();
                    //startActivity(new Intent(getApplicationContext(),RoomActivity.class));
                    //finish();
                    break;

                case R.id.findNext_room3:
                    oldPatient = roomQ.get(0);
                    manualReadAllQueue();
                    mReadRoomQueue(); // start 0
                    mReadPatientID();
                    findWaitingQueue();
                    mAllQueue.child("0").setValue(null);
                    break;

                case R.id.cured_room3:
                    curedPatient();
                    Toast.makeText(InRoom3Activity.this, "Cured This Patient", Toast.LENGTH_SHORT).show();

                    // For Call Next Queue //
                    /*mReadClinicQueue();
                    if(isToken == true && isAllQueue == true && tokenQ.get(0) <= allCounter.size()){
                        mRoom1.child("queue").setValue((Integer)tokenQ.get(0));
                        mClinic.child("queue_token").setValue(tokenQ.get(0) + 1);
                        isInRoomSet = false;
                    }else{
                        Log.d("CallNext", isToken.toString()+" "+isAllQueue.toString());
                        Toast.makeText(InRoomActivity.this, "Can't call next queue", Toast.LENGTH_SHORT).show();
                    }*/

                    break;

                case R.id.choose_room3:
                    startActivity(new Intent(getApplicationContext(), Room3ChooseActivity.class));
                    break;

                case R.id.refresh_room3:
                    mReadRoomQueue();
                    if(!roomQ.get(0).toString().equals("0")) {
                        mReadClinicQueue();
                        clickReadThisQueue();
                        manualReadAllQueue();
                    }
                    if(isCurrentQueueID) {
                        updatePatient();
                    }
                    break;

                case R.id.phone_room3:
                    if(phone1.getText().toString()!="(NULL)"){
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber.get(0), null)));
                    }
                    break;
            }
        }
    };

    private void bind(){
        doc1 = findViewById(R.id.doctor_room3);
        queue1 = findViewById(R.id.queue_room3);
        name1 = findViewById(R.id.name_room3);
        gen1 = findViewById(R.id.gend_symb3);
        birth1 = findViewById(R.id.birth_room3);
        blood1 = findViewById(R.id.blood_room3);
        weigh1 = findViewById(R.id.weight_room3);
        height1 = findViewById(R.id.height_room3);
        cau1 = findViewById(R.id.caution_room3);
        note1 = findViewById(R.id.note_room3);
        pstate1 = findViewById(R.id.pstate_room3);
        phone1 = findViewById(R.id.phone_room3);

        exitB = findViewById(R.id.exit_room3);
        enterB = findViewById(R.id.enter_room3);
        curedB = findViewById(R.id.cured_room3);
        findNextB = findViewById(R.id.findNext_room3);
        chooseB = findViewById(R.id.choose_room3);
        refB = findViewById(R.id.refresh_room3);

        exitB.setOnClickListener(onClickListener);
        enterB.setOnClickListener(onClickListener);
        curedB.setOnClickListener(onClickListener);
        findNextB.setOnClickListener(onClickListener);
        chooseB.setOnClickListener(onClickListener);
        refB.setOnClickListener(onClickListener);
        phone1.setOnClickListener(onClickListener);

    }

    private void pageManage(){
        mDatabase = FirebaseDatabase.getInstance();

        mClinic = mDatabase.getReference("Clinic");
        mRoom1 = mDatabase.getReference("Clinic").child("rooms").child("3");
        mOfficers = mDatabase.getReference("Officers");
        mAllQueue = mDatabase.getReference("Clinic").child("queues");
        mPatients = mDatabase.getReference("Profile");
        mUserPatient = mDatabase.getReference("Users");


        checkCurrentDoctor();
        readRoomQueue();
        readClinicQueue();
        readAllQueue();
        readProfile();
        readChildAllQueue();
    }



    private void updatePatient(){

        if(isRoom && !patientID.isEmpty() && !patientID.isEmpty()) {
            for (int i = 0; i < patientKeys.size(); i++) {
                if (patientKeys.get(i).equals(patientID.get(0))) {
                    mRoom1.child("patient").setValue(profiles.get(i).getName());
                    name1.setText(profiles.get(i).getName());
                    //gen1.setText(profiles.get(i).getName());
                    if (profiles.get(i).getGender().equals("Male")) {
                        gen1.setText(" ♂ ");
                        gen1.setTextColor(Color.parseColor("#3388FF"));
                    } else if (profiles.get(i).getGender().equals("Female")) {
                        gen1.setText(" ♀ ");
                        gen1.setTextColor(Color.parseColor("#FF4499"));
                    }
                    String datetobd = profiles.get(i).getBdate();
                    if(datetobd.equals("NULL"))datetobd = " - ";
                    String monthtobd = profiles.get(i).getBmonth();
                    if(monthtobd.equals("NULL"))monthtobd = " - ";
                    String yeartobd = profiles.get(i).getByear();
                    if(yeartobd.equals("NULL"))yeartobd = " - ";

                    String bd = datetobd + "/"
                            + monthtobd + "/"
                            + yeartobd;

                    birth1.setText(bd);

                    blood1.setText(profiles.get(i).getBlood());
                    weigh1.setText(profiles.get(i).getWeight());
                    height1.setText(profiles.get(i).getHeight());
                    cau1.setText(profiles.get(i).getCaution());
                    phoneNumber.clear();
                    String phoneString;
                    phoneString = "("+profiles.get(i).getPhone()+")";
                    phoneNumber.add(profiles.get(i).getPhone());
                    phone1.setText(phoneString);
                    note1.setText(thisSymp.get(roomQ.get(0)-1).toString());
                    pstate1.setText(thisStatus.get(roomQ.get(0)-1).toString());
                    mAllQueue.child("0").setValue(null);
                    isInRoomSet = false;
                    //Log.d("updatePatient", thisStatus.get((roomQ.get(0))-1).toString()+"/"+String.valueOf(roomQ.get(0)));
                    // Log.d("waitingList", "updatePatient: "+thisStatus.get(roomQ.get(0)-1));
                    if (thisStatus.get(roomQ.get(0)-1).toString().equals("waiting") && isInRoomSet == false) {
                        manualReadAllQueue();
                        mAllQueue.child(String.valueOf(roomQ.get(0))).child("status").setValue("inRoom3");
                        Toast.makeText(this, "Patient in Room", Toast.LENGTH_SHORT).show();
                        isInRoomSet = true;

                    }
                }
            }
        }
    }

    private void updatePatient(Profile p){
        mRoom1.child("patient").setValue(p.getName());
        name1.setText(p.getName());
        //gen1.setText(profiles.get(i).getName());
        if(p.getGender().equals("Male")){
            gen1.setText(" ♂ ");
            gen1.setTextColor(Color.parseColor("#3388FF"));
        }else if(p.getGender().equals("Female")){
            gen1.setText(" ♀ ");
            gen1.setTextColor(Color.parseColor("#FF4499"));
        }
        String bd = p.getBdate() + "/"
                + p.getBmonth() + "/"
                + p.getByear();
        birth1.setText(bd);
        blood1.setText(p.getBlood());
        weigh1.setText(p.getWeight());
        height1.setText(p.getHeight());
        cau1.setText(p.getCaution());
        note1.setText(thisSymp.get(roomQ.get(0)));
    }

    private void resetRoom(){
        mRoom1.child("doctor").setValue("Not Available");
        mRoom1.child("status").setValue(Boolean.FALSE);
        mRoom1.child("patient").setValue("Empty");
        mRoom1.child("queue").setValue(0);

        mAllQueue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    if(node.child("status").exists()){
                        if(node.child("status").getValue().toString().equals("inRoom3")){
                            mAllQueue.child(node.getKey()).child("status").setValue("waiting");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void curedPatient(){
        isPatientID = false;
        mReadRoomQueue();
        Log.d("isCured", "curedPatient: "+String.valueOf(thisStatus.get(roomQ.get(0)-1)));
        if(thisStatus.get(roomQ.get(0)-1).toString().equals("inRoome")){
            mReadPatientID();
            mAllQueue.child(String.valueOf(roomQ.get(0))).child("status").setValue("Cured");
            if(isPatientID) {
                mUserPatient.child(patientID.get(0)).child("inQueue").setValue(Boolean.FALSE);
                mUserPatient.child(patientID.get(0)).child("queueNo").setValue(null);
                mUserPatient.child(patientID.get(0)).child("queueType").setValue(null);
            }
        }
    }

    private void readProfile(){
        mPatients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientKeys.clear();
                profiles.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    patientKeys.add(keyNode.getKey());
                    Profile profile = new Profile();
                    profile.setName(keyNode.child("name").getValue().toString());
                    profile.setGender(keyNode.child("gender").getValue().toString());
                    profile.setBlood(keyNode.child("blood").getValue().toString());
                    profile.setBdate(keyNode.child("bdate").getValue().toString());
                    profile.setBmonth(keyNode.child("bmonth").getValue().toString());
                    profile.setByear(keyNode.child("byear").getValue().toString());
                    profile.setPhone(keyNode.child("phone").getValue().toString());
                    profile.setWeight(keyNode.child("weight").getValue().toString());
                    profile.setHeight(keyNode.child("height").getValue().toString());
                    profile.setCaution(keyNode.child("caution").getValue().toString());
                    profiles.add(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mReadPatientProfile(){
        mPatients.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientKeys.clear();
                profiles.clear();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    patientKeys.add(keyNode.getKey());
                    Profile profile = new Profile();
                    profile.setName(keyNode.child("name").getValue().toString());
                    profile.setGender(keyNode.child("gender").getValue().toString());
                    profile.setBlood(keyNode.child("blood").getValue().toString());
                    profile.setBdate(keyNode.child("bdate").getValue().toString());
                    profile.setBmonth(keyNode.child("bmonth").getValue().toString());
                    profile.setByear(keyNode.child("byear").getValue().toString());
                    profile.setPhone(keyNode.child("phone").getValue().toString());
                    profile.setWeight(keyNode.child("weight").getValue().toString());
                    profile.setHeight(keyNode.child("height").getValue().toString());
                    profile.setCaution(keyNode.child("caution").getValue().toString());
                    profiles.add(profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readThisProfile(){
        mPatients.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thisProfile.clear();
                gotProfile.clear();
                for(DataSnapshot k : dataSnapshot.getChildren()) {
                    if (!roomQ.isEmpty()) {
                        if (k.getKey().equals(roomQ.get(0))) {
                            Profile tp = new Profile();
                            tp.setName(k.child("name").getValue().toString());
                            tp.setGender(k.child("gender").getValue().toString());
                            tp.setBlood(k.child("blood").getValue().toString());
                            tp.setBdate(k.child("bdate").getValue().toString());
                            tp.setBmonth(k.child("bmonth").getValue().toString());
                            tp.setByear(k.child("byear").getValue().toString());
                            tp.setPhone(k.child("phone").getValue().toString());
                            tp.setWeight(k.child("weight").getValue().toString());
                            tp.setHeight(k.child("height").getValue().toString());
                            tp.setCaution(k.child("caution").getValue().toString());
                            thisProfile.add(tp);
                            gotProfile.add(Boolean.TRUE);
                            //updatePatient();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAllQueue(){
        mAllQueue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isAllQueue = false;
                allCounter.clear();
                patientID.clear();
                thisSymp.clear();
                thisStatus.clear();
                if(isRoom) {
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        allCounter.add(keyNode.getKey());
                        if(keyNode.child("symptom").exists()){
                            thisSymp.add(keyNode.child("symptom").getValue(String.class));
                        }else{
                            thisSymp.add(" - ");
                        }

                        if(keyNode.child("status").exists()){
                            thisStatus.add(keyNode.child("status").getValue().toString());
                        }else{
                            thisStatus.add(" - ");
                        }
                    }
                    isAllQueue = true;
                    //updatePatient();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void manualReadAllQueue(){
        mAllQueue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isAllQueue = false;
                allCounter.clear();
                patientID.clear();
                thisSymp.clear();
                thisStatus.clear();
                if(isRoom) {
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        allCounter.add(keyNode.getKey());
                        if(keyNode.child("symptom").exists()){
                            thisSymp.add(keyNode.child("symptom").getValue(String.class));
                        }else{
                            thisSymp.add(" - ");
                        }

                        if(keyNode.child("status").exists()){
                            thisStatus.add(keyNode.child("status").getValue().toString());
                        }else{
                            thisStatus.add(" - ");
                        }
                    }
                    isAllQueue = true;
                    //updatePatient();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readChildAllQueue(){
        mAllQueue.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                manualReadAllQueue();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void clickReadThisQueue(){
        mAllQueue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isCurrentQueueID = false;
                if(isRoom) {
                    mReadPatientID();
                    mReadPatientProfile();
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        Log.d("isRoom", keyNode.getKey().toString()+" "+roomQ.get(0).toString());
                        if (keyNode.getKey().toString().equals(roomQ.get(0).toString())) {
                            patientID.add(keyNode.child("user").getValue(String.class));
                            isCurrentQueueID = true;
                            if(isAllQueue == true) {
                                updatePatient();
                                pstate1.setText(keyNode.child("status").getValue().toString());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readClinicQueue(){
        mClinic.child("queue_token").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isToken = false;
                tokenQ.clear();
                if(dataSnapshot.exists()) {
                    tokenQ.add(dataSnapshot.getValue(Integer.class));
                    isToken = (Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findWaitingQueue(){
        mAllQueue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                waitingList.clear();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    if(keyNode.child("status").getValue().toString().equals("waiting") && !keyNode.getKey().toString().equals("0")){
                        waitingList.add(keyNode.getKey().toString());
                    }
                }
                mAllQueue.child("0").setValue(null);
                Log.d("WaitingList", waitingList.toString());
                if(waitingList.isEmpty()){
                    Toast.makeText(InRoom3Activity.this, "Don't have any Waiting Patient now", Toast.LENGTH_SHORT).show();
                }else{
                    mRoom1.child("queue").setValue(Integer.valueOf(waitingList.get(0)));
                    mReadRoomQueue(); // to waiting 1
                    //mAllQueue.child(String.valueOf(roomQ.get(0))).child("status").setValue("inRoom1");
                    if(oldPatient > 0) {
                        if (thisStatus.get(oldPatient-1).toString().equals("inRoom3")) {
                            mAllQueue.child(String.valueOf(oldPatient)).child("status").setValue("waiting");
                            mAllQueue.child("0").setValue(null);
                        }
                    }
                    clickReadThisQueue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mReadClinicQueue(){
        mClinic.child("queue_token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isToken = false;
                tokenQ.clear();
                if(dataSnapshot.exists()) {
                    tokenQ.add(dataSnapshot.getValue(Integer.class));
                    isToken = (Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readRoomQueue(){
        mRoom1.child("queue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomQ.clear();
                isRoom = false;
                if(dataSnapshot.exists()){
                    queue1.setText(String.valueOf(dataSnapshot.getValue()));
                    roomQ.add(dataSnapshot.getValue(Integer.class));
                    isRoom = (Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mReadRoomQueue(){
        mRoom1.child("queue").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomQ.clear();
                isRoom = false;
                if(dataSnapshot.exists()){
                    queue1.setText(String.valueOf(dataSnapshot.getValue()));
                    roomQ.add(dataSnapshot.getValue(Integer.class));
                    isRoom = (Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkCurrentDoctor(){
        mRoom1.child("doctor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doc1.setText(dataSnapshot.getValue(String.class));
                if(dataSnapshot.getValue(String.class).equals(mUser.getDisplayName())) {
                    enterB.setVisibility(View.INVISIBLE);
                    enterB.setEnabled(false);
                    exitB.setVisibility(View.VISIBLE);
                    exitB.setEnabled(true);
                    curedB.setVisibility(View.VISIBLE);
                    findNextB.setVisibility(View.VISIBLE);
                    chooseB.setVisibility(View.VISIBLE);
                }else{
                    enterB.setVisibility(View.VISIBLE);
                    enterB.setEnabled(true);
                    exitB.setVisibility(View.INVISIBLE);
                    exitB.setEnabled(false);
                    curedB.setVisibility(View.INVISIBLE);
                    findNextB.setVisibility(View.INVISIBLE);
                    chooseB.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mReadPatientID(){
        mRoom1.child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isPatientID = false;
                patientID.clear();
                if(dataSnapshot.exists()){
                    patientID.add(dataSnapshot.getValue(String.class));
                    isPatientID = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setWaiting(){
        mAllQueue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot keyNode : dataSnapshot.getChildren()){
                    mAllQueue.child(keyNode.getKey()).child("status").setValue("waiting");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setOfficerIdle(){
        mOfficers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    if(node.getKey().equals(mUser.getUid())){
                        mOfficers.child(node.getKey()).child("status").setValue("idle");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setOfficerRoom(){
        mOfficers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot node : dataSnapshot.getChildren()){
                    if(node.child("status").exists()){
                        if(node.child("status").equals("Room3")){
                            mOfficers.child(node.getKey()).child("status").setValue("idle");
                        }
                    }
                    if(node.getKey().equals(mUser.getUid())){
                        mOfficers.child(node.getKey()).child("status").setValue("Room3");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
