package com.intech.yayabureau.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.intech.yayabureau.Models.Bureau;
import com.intech.yayabureau.Models.Candidates;
import com.intech.yayabureau.R;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
View root;
    private String id,building,mobile,county,street,
            estate,boxNo;
    private long NoOf_Candidate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference BureauRef = db.collection("Yaya_Bureau");


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


        LoadDetails();
        return root;
    }



    //----Load details---//
    private String userName,email,Bureau_Name;
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

                    BureauName.setText(Bureau_Name);
                    County.setText(county);
                    Email.setText(" "+email);
                    BureauID.setText(id);
                    Telephone.setText(" "+mobile);
                    NoOfCandidate.setText(" "+noOfCandidates+"");
                    Street.setText(street);
                    BuildingName.setText(building);
                    BoxNo.setText(" "+boxNo);
                    Street.setText(street);
                 //   Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.load).error(R.drawable.user).into(candidateProfile);





                }
            }
        });

    }
    //...end load details
}