package com.intech.yayabureau.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.intech.yayabureau.Activities.MainActivity;
import com.intech.yayabureau.Activities.MyCandidatesActivity;
import com.intech.yayabureau.Models.Bureau;
import com.intech.yayabureau.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
View root;
    private String id,building,mobile,county,street,
            estate,boxNo;
    private long NoOf_Candidate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    private CircleImageView UserImage;

    private TextView logout;

    private TextView BureauName,UserName,BureauID,BuildingName,Street,Estate,NoOfCandidate,County,Email,BoxNo
            ,Telephone;

    private EditText EditBureauName,EditUserName,EditBuildingName,EditStreet,EditCounty,EditEmail,EditBoxNo,EditEstate
            ,EditTelephone;
    private Button BtnSaveChanges;

    private FirebaseAuth mAuth;
    private FloatingActionButton editProfile;
    private LinearLayout ProfileEdits,ProfileDetails;
    private int EditState = 0;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        BureauName = root.findViewById(R.id.B_bureauName);
        UserName = root.findViewById(R.id.B_name);
        BureauID = root.findViewById(R.id.B_id);
        BuildingName = root.findViewById(R.id.B_buildingName);
        Street = root.findViewById(R.id.B_street);
        Estate = root.findViewById(R.id.B_estate);
        County = root.findViewById(R.id.B_county);
        Email = root.findViewById(R.id.B_email);
        BoxNo = root.findViewById(R.id.B_boxNo);
        Telephone = root.findViewById(R.id.B_phone);
        NoOfCandidate = root.findViewById(R.id.B_CandidateCount);
        logout = root.findViewById(R.id.LogOut);
        UserImage = root.findViewById(R.id.userImage);


        ///Edit Variables
        ProfileDetails =root.findViewById(R.id.ProfileDetails);
        ProfileEdits = root.findViewById(R.id.ProfileEditDetails);
        editProfile = root.findViewById(R.id.edit_profile);
        BtnSaveChanges = root.findViewById(R.id.Btnsave_ProfileEdits);
        EditBureauName = root.findViewById(R.id.BEdit_BName);
        EditUserName = root.findViewById(R.id.BEdit_name);
        EditStreet = root.findViewById(R.id.BEdit_street);
        EditBuildingName = root.findViewById(R.id.BEdit_buildingName);
        EditEstate = root.findViewById(R.id.BEdit_estate);
        EditCounty = root.findViewById(R.id.BEdit_county);
        EditEmail = root.findViewById(R.id.BEdit_email);
        EditTelephone = root.findViewById(R.id.BEdit_phone);

        BtnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()){

                }else {

                    SaveEditedChanges();
                }
            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EditState == 0 ){
                    ProfileDetails.setVisibility(View.GONE);
                    ProfileEdits.setVisibility(View.VISIBLE);
                    EditState =1;
                }else if (EditState == 1){
                    ProfileDetails.setVisibility(View.VISIBLE);
                    ProfileEdits.setVisibility(View.GONE);
                    EditState = 0;
                }

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout_Alert();
            }
        });


        LoadDetails();
        return root;
    }

    private void SaveEditedChanges() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait saving changes..");
        progressDialog.show();

        firstName = EditUserName.getText().toString().trim();
        Bname = EditBureauName.getText().toString().trim();
        streets = EditStreet.getText().toString().trim();
        telepone = EditTelephone.getText().toString().trim();
        county = EditCounty.getText().toString().trim();
        mail = EditEmail.getText().toString().trim();
        Building = EditBuildingName.getText().toString().trim();
        Estates = EditEstate.getText().toString().trim();


        String token_Id = FirebaseInstanceId.getInstance().getToken();
        HashMap<String,Object> registerB = new HashMap<>();
        registerB.put("Name", firstName);
        registerB.put("Bureau_Name",Bname);
        registerB.put("Building",Building);
        registerB.put("Street_name",streets);
        registerB.put("County",county);
        registerB.put("Email",mail);
        registerB.put("Phone_NO",telepone);

        BureauRef.document(mAuth.getCurrentUser().getUid()).update(registerB).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ToastBack("Changes saved successfully");
                    progressDialog.dismiss();
                }else {
                    ToastBack(task.getException().getMessage());
                    progressDialog.dismiss();
                }
            }
        });

    }


    private  AlertDialog dialog2;
    public void Logout_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        dialog2 = builder.create();
        dialog2.show();
        builder.setMessage("Are you sure to Log out..\n");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Log_out();

                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog2.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    void Log_out(){

        String User_ID = mAuth.getCurrentUser().getUid();

        HashMap<String,Object> store = new HashMap<>();
        store.put("device_token", FieldValue.delete());

        BureauRef.document(User_ID).update(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()){

                    mAuth.signOut();
                    Intent logout = new Intent(getContext(), MainActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logout);
                    dialog2.dismiss();


                }else {

                    ToastBack( task.getException().getMessage());

                }

            }
        });

    }

    //----Load details---//
    private String userName,email,Bureau_Name,BureauImage,BureauWard;
    private long noOfCandidates;
    private void LoadDetails() {

        BureauRef.document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()){
                    Bureau bureau = documentSnapshot.toObject(Bureau.class);
                    userName = bureau.getName();
                    Bureau_Name = bureau.getBureau_Name();
                    county = bureau.getCounty();
                    email = bureau.getEmail();
                    id = bureau.getID_no();
                    mobile = bureau.getPhone_NO();
                    noOfCandidates = bureau.getNo_of_candidates();
                    street = bureau.getStreet_name();
                    building = bureau.getBuilding();
                    boxNo = bureau.getBox_No();
                    BureauImage = bureau.getBureau_Image();
                    BureauWard = bureau.getCity();


                    BureauName.setText(Bureau_Name);
                    UserName.setText(userName);
                    BureauID.setText(id);
                    County.setText(county);
                    Email.setText(" "+email);
                    BureauID.setText(id);
                    Telephone.setText(" "+mobile);
                    NoOfCandidate.setText(" "+noOfCandidates+"");
                    Street.setText(street);
                    BuildingName.setText(building);
                    BoxNo.setText(" "+boxNo);
                    Street.setText(street);

                    EditBureauName.setText(Bureau_Name);
                    EditUserName.setText(userName);
                    EditStreet.setText(street);
                    EditBuildingName.setText(building);
                    EditCounty.setText(county);
                    EditEmail.setText(email);
                    EditEstate.setText(BureauWard);
                    EditTelephone.setText(mobile);




                    if (BureauImage != null){
                        Picasso.with(getContext())
                                .load(BureauImage).placeholder(R.drawable.load)
                                .error(R.drawable.user)
                                .into(UserImage);
                    }




                }
            }
        });

    }
    //...end load details


    private String firstName,streets,Building,Estates,mail,telepone,Bname;
    private boolean validation() {
        firstName = EditUserName.getText().toString().trim();
        Bname = EditBureauName.getText().toString().trim();
        streets = EditStreet.getText().toString().trim();
        telepone = EditTelephone.getText().toString().trim();
        county = EditCounty.getText().toString().trim();
        mail = EditEmail.getText().toString().trim();
        Building = EditBuildingName.getText().toString().trim();
        Estates = EditEstate.getText().toString().trim();



        if (firstName.isEmpty()){
            ToastBack("Provide your full  name");
            return false;
        }
        else if (Bname.isEmpty()){
            ToastBack("Provide Bureau name");
            return false;
        }
        else if (streets.isEmpty()){
            ToastBack("Provide your street");
            return false;
        }
        else if (telepone.isEmpty()){
            ToastBack("Provide mobile no");
            return false;
        }
        else if (county.isEmpty()){
            ToastBack("Provide county.");
            return false;
        }
        else if (Estates.isEmpty()){
            ToastBack("Provide your estate.");
            return false;
        }

        else{
            return true;
        }
    }


    private Toast backToast;
    private void ToastBack(String message){
        backToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        backToast.show();
    }
}