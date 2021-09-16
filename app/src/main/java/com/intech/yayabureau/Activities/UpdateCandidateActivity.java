package com.intech.yayabureau.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.intech.yayabureau.Models.Candidates;
import com.intech.yayabureau.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateCandidateActivity extends AppCompatActivity {

    String ID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    FirebaseAuth mAuth;

    private TextView
            DOB,FirstName,Mobile,County
            ,Ward,Status,Employer,EmployerNo,City,Salary,ID_no,Residence;
    private Spinner CandidateStatus;

    private CircleImageView candidateProfile;
    private String firstName,image,status,mobile,county,ward,UpdateStatus,
            village,nextOfKin,kinMobile,experience,salary,residence,dob,idNo,gender,age;
    private Button updateCandidate;
    private LinearLayout employerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_candidate);

      ID = getIntent().getStringExtra("ID");
      DOB = findViewById(R.id.candidate_age);
      FirstName = findViewById(R.id.candidate_name);
      Mobile = findViewById(R.id.candidate_phone);
      County = findViewById(R.id.candidate_county);
      Ward = findViewById(R.id.candidate_ward);
      ID_no = findViewById(R.id.candidate_id);
      Status = findViewById(R.id.candidate_status);
      Employer = findViewById(R.id.candidate_employer);
      EmployerNo = findViewById(R.id.employer_contact);
      Salary = findViewById(R.id.candidate_salary);
      candidateProfile = findViewById(R.id.candidate_profile);
      City = findViewById(R.id.candidate_residence);
      CandidateStatus = findViewById(R.id.select_status);
      updateCandidate = findViewById(R.id.upload_candidate);
      Residence = findViewById(R.id.candidate_residence);
      employerDetails = findViewById(R.id.LayoutEmployer);


      updateCandidate.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              UpdateStatus();
          }
      });


      LoadDetails();

    }

    //----Load details---//
    private String userName,email,BureauName;
    private long noOfCandidates;
    private void LoadDetails() {

        CandidateRef.document(ID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()){
                    Candidates candidates = documentSnapshot.toObject(Candidates.class);
                    firstName = candidates.getCandidate_name();
                    age = candidates.getAge();
                    county = candidates.getCounty();
                    ward = candidates.getWard();
                    idNo = candidates.getID_no();
                    mobile = candidates.getMobile_no();
                    status = candidates.getStatus();
                    residence = candidates.getResidence();
                    salary = candidates.getSalary();
                    image =candidates.getProfile_image();
                    residence = candidates.getResidence();

                    FirstName.setText(firstName);
                    DOB.setText(age+" yrs");
                    County.setText(county);
                    Ward.setText(ward);
                    ID_no.setText(idNo);
                    Mobile.setText(mobile);
                    Status.setText(status);
                    City.setText(residence);
                    Salary.setText(salary);
                    Residence.setText(residence);
                    Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.load).error(R.drawable.user).into(candidateProfile);


                    if (status.equals("UnAvailable")){
                        employerDetails.setVisibility(View.VISIBLE);
                        Status.setTextColor(getResources().getColor(R.color.blue));
                    }else if (status.equals("Available")){
                        employerDetails.setVisibility(View.GONE);
                        Status.setTextColor(getResources().getColor(R.color.main2));
                    }


                }
            }
        });

    }
    //...end load details

    //-----Update Status ----
    private void UpdateStatus(){
        UpdateStatus = CandidateStatus.getSelectedItem().toString().trim();
        if (UpdateStatus.equals("Select status")){
            ToastBack("Select status");
        }else {

            HashMap<String, Object> update = new HashMap<>();
            update.put("Status", UpdateStatus);
            CandidateRef.document(ID).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        ToastBack("Status updated to " + UpdateStatus);
                    } else {

                        ToastBack(task.getException().getMessage());
                    }
                }
            });
        }

    }

    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = backToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.parseColor("#61EBED"), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#FF000000"));
        backToast.show();
    }

}