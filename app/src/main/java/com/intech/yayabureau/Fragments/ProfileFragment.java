package com.intech.yayabureau.Fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.intech.yayabureau.Activities.MainActivity;
import com.intech.yayabureau.Models.Bureau;
import com.intech.yayabureau.Models.Candidates;
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

    private FirebaseAuth mAuth;


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


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout_Alert();
            }
        });


        LoadDetails();
        return root;
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
    private String userName,email,Bureau_Name,BureauImage;
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

    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        View view = backToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.parseColor("#0BF4DE"), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#1C1B2B"));
        backToast.show();
    }
}