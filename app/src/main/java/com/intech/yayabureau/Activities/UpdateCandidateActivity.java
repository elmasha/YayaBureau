package com.intech.yayabureau.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.intech.yayabureau.Models.Candidates;
import com.intech.yayabureau.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class UpdateCandidateActivity extends AppCompatActivity {

    String ID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference AdminRef = db.collection("Admin");
    CollectionReference BureauRef = db.collection("Yaya_Bureau");

    FirebaseAuth mAuth;

    private TextView
            DOB,FirstName,Mobile,County
            ,Ward,Status,Employer,EmployerCounty,EmployerCity,EmployerNo,City,Salary,ID_no,Residence;
    private Spinner CandidateStatus;
    private EditText EditName,EditSalary,EditSecName,EditCounty,EditWard,EditNo,EditResidence;

    private CircleImageView candidateProfile;
    private String firstName,image,status,mobile,county,ward,UpdateStatus,
          salary,residence,idNo,age,eCounty,eCity,ePhone,eName;
    private FloatingActionButton  deleteCandidate,updateCandidate,callCandidate,editCandidate;
    private LinearLayout StatusLayout,EditMyDetails,MyDetails;
    int PERMISSION_ALL = 20003;
    private Bitmap compressedImageBitmap;
    String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS};
    private LinearLayout linearLayoutDeal;
    private Button Btn_SaveChanges;
    private int EditState = 0;
    private TextView BackToCandidate;
    private CardView employerDetails;


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
      deleteCandidate = findViewById(R.id.delete_candidate);
      Residence = findViewById(R.id.candidate_residence);
      employerDetails = findViewById(R.id.LayoutEmployerDetails);
      EmployerCounty = findViewById(R.id.employer_county);
      EmployerCity = findViewById(R.id.employer_city);
      StatusLayout = findViewById(R.id.StatusLayout);
      callCandidate = findViewById(R.id.call_candidate);
      editCandidate = findViewById(R.id.edit_candidate);
      BackToCandidate = findViewById(R.id.BackToCandidate);
      ///Edits
        EditMyDetails = findViewById(R.id.EditMyDetails);
        MyDetails = findViewById(R.id.MyDetails);
        EditName = findViewById(R.id.edit_name);
        EditSecName = findViewById(R.id.edit_second);
        EditCounty = findViewById(R.id.edit_county);
        EditWard = findViewById(R.id.edit_ward);
        EditNo = findViewById(R.id.edit_phone);
        EditResidence = findViewById(R.id.edit_residence);
        EditSalary = findViewById(R.id.edit_salary);
        Btn_SaveChanges = findViewById(R.id.Btn_saveEdits);




        BackToCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(),MyCandidatesActivity.class));
            }
        });
        Btn_SaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validation()){

                }else {
                    SaveEditChanges();
                }
            }
        });


        editCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(EditState == 0){
                    MyDetails.setVisibility(View.GONE);
                    EditMyDetails.setVisibility(View.VISIBLE);
                    EditState = 1;
                }else if (EditState == 1){
                    MyDetails.setVisibility(View.VISIBLE);
                    EditMyDetails.setVisibility(View.GONE);
                    EditState = 0;
                }
            }
        });


        callCandidate.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {


              if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                  ActivityCompat.requestPermissions(UpdateCandidateActivity.this, PERMISSIONS, PERMISSION_ALL);
              }else {
                  Intent callIntent = new Intent(Intent.ACTION_CALL);
                  callIntent.setData(Uri.parse("tel:" +mobile));
                  startActivity(callIntent);
              }


          }

      });


      updateCandidate.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Status_Alert();
          }
      });

        deleteCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Delete_Alert();
            }
        });


      LoadDetails();
      LoadCount();

    }

    private void SaveEditChanges() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait saving changes..");
        progressDialog.show();
        firstName = EditName.getText().toString().trim();
        middleName = EditSecName.getText().toString().trim();
        Editssalary = EditSalary.getText().toString().trim();
        mobile = EditNo.getText().toString().trim();
        county = EditCounty.getText().toString().trim();
        ward= EditWard.getText().toString().trim();


        HashMap<String,Object> store = new HashMap<>();
        store.put("Candidate_name",firstName +" " + middleName);
        store.put("ID_no",idNo);
        store.put("Mobile_no",mobile);
        store.put("County",county);
        store.put("Ward",ward);
        store.put("Salary",salary);

        CandidateRef.document(ID).update(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    ToastBack("Changes saved successfully");
                    progressDialog.dismiss();
                }else {

                    ToastBack("Registration failed try Again.");
                    progressDialog.dismiss();
                }
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {


                    return false;
                }

            }
        }
        return true;
    }

    private AlertDialog dialog2;
    public void Delete_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog2 = builder.create();
        dialog2.show();
        builder.setIcon(R.drawable.ic_delete);
        builder.setTitle("Delete Candidate");
        builder.setMessage("Are you sure you want to delete this candidate..\n");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Delete();

                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog2.dismiss();
            }
        });
        builder.show();
    }

    private void Delete() {
        CandidateRef.document(ID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                            double total = noOfCandidates - 1;
                            WriteBatch batch;
                            batch = db.batch();
                            DocumentReference doc1 = BureauRef.document(mAuth.getCurrentUser().getUid());
                            batch.update(doc1, "No_of_candidates", total);
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent logout = new Intent(getApplicationContext(), MyCandidatesActivity.class);
                                        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(logout);
                                        dialog2.dismiss();
                                    }else {
                                        ToastBack(task.getException().getMessage());
                                    }

                                }
                            });


                    }else {

                        ToastBack(task.getException().getMessage());
                    }
            }
        });
    }

    private long TotalCount;
    private void LoadCount(){

        AdminRef.document("No_of_candidates").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    return;
                }
                TotalCount = documentSnapshot.getLong("Total_number");

            }
        });
    }



private String middleName,Editssalary;
    private boolean validation() {
        firstName = EditName.getText().toString().trim();
        middleName = EditSecName.getText().toString().trim();
        Editssalary = EditSalary.getText().toString().trim();
        mobile = EditNo.getText().toString().trim();
        county = EditCounty.getText().toString().trim();
        ward= EditWard.getText().toString().trim();

        if (firstName.isEmpty()){
            ToastBack("Provide first name");
            return false;
        }
        else if (middleName.isEmpty()){
            ToastBack("Provide second name");
            return false;
        }
        else if (Editssalary.isEmpty()){
            ToastBack("Provide salary");
            return false;
        }
        else if (mobile.isEmpty()){
            ToastBack("Provide mobile no");
            return false;
        }
        else if (county.isEmpty()){
            ToastBack("Provide county.");
            return false;
        }
        else if (ward.isEmpty()){
            ToastBack("Provide ward.");
            return false;
        }

        else{
            return true;
        }
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
                    eName = candidates.getEmployer_name();
                    ePhone = candidates.getEmployer_no();
                    eCounty = candidates.getEmployer_county();
                    eCity = candidates.getEmployer_city();

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
                    Employer.setText(eName);
                    EmployerNo.setText(ePhone);
                    EmployerCounty.setText(eCounty);
                    EmployerCity.setText(eCity);

                    EditName.setText(firstName);
                    EditCounty.setText(county);
                    EditNo.setText(mobile);
                    EditResidence.setText(residence);
                    EditWard.setText(ward);
                    if (image != null){
                        Picasso.with(candidateProfile.getContext())
                                .load(image)
                                .placeholder(R.drawable.load).error(R.drawable.user).into(candidateProfile);
                    }

                    if (status.equals("UnAvailable")){
                        if(eName.equals("") | ePhone.equals("") ){
                            employerDetails.setVisibility(View.GONE);
                        }else {
                            employerDetails.setVisibility(View.VISIBLE);
                        }
                        Status.setTextColor(getResources().getColor(R.color.ColorRed));
                    }else if (status.equals("Available")){
                        employerDetails.setVisibility(View.GONE);
                        Status.setTextColor(getResources().getColor(R.color.ColorGreen));
                    }


                }
            }
        });

    }
    //...end load details

    //-----Update Status ----
    private void UpdateStatus(){

        if(status.equals("Available")){
        UpdateStatus = "UnAvailable";
            HashMap<String, Object> update = new HashMap<>();
            update.put("Status", UpdateStatus);
            CandidateRef.document(ID).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        ToastBack("Status updated to " + UpdateStatus);
                        getAvailableCounts(UpdateStatus);
                    } else {

                        ToastBack(task.getException().getMessage());
                    }
                }
            });
        }else if (status.equals("UnAvailable")){
        UpdateStatus = "Available";
            HashMap<String, Object> update = new HashMap<>();
            update.put("Status", UpdateStatus);
            CandidateRef.document(ID).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        ToastBack("Status updated to " + UpdateStatus);
                        if (dialog_status!= null)dialog_status.dismiss();
                        getAvailableCounts(UpdateStatus);
                    } else {

                        ToastBack(task.getException().getMessage());
                        if (dialog_status!= null)dialog_status.dismiss();
                    }
                }
            });

        }


    }

    private AlertDialog dialog_status;
    public void Status_Alert() {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            dialog_status = builder.create();
            dialog_status.show();
            builder.setIcon(R.drawable.ic_sync);
            builder.setTitle("Update status");
            builder.setMessage("Are you sure you want to update this status ..");
            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            UpdateStatus();

                        }
                    });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog_status.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();




    }

    private void getAvailableCounts(String status){
        final DocumentReference sfDocRef = db.collection("Admin").document("No_of_candidates");

        if (status.equals("Available")){

            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);

                    // Note: this could be done without a transaction
                    //       by updating the population using FieldValue.increment()
                    double newPopulation = snapshot.getLong("Total_number") + 1;
                    transaction.update(sfDocRef, "Total_number", newPopulation);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Transaction success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Transaction failure.", e);
                        }
                    });

        }else if (status.equals("UnAvailable")){

            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);

                    // Note: this could be done without a transaction
                    //       by updating the population using FieldValue.increment()
                    double newPopulation = snapshot.getLong("Total_number") - 1;
                    transaction.update(sfDocRef, "Total_number", newPopulation);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Transaction success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                }
            });


        }else {

        }

    }

    private Toast backToast;
    private void ToastBack(String message){
        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        backToast.show();
    }

}