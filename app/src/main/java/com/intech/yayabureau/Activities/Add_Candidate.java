package com.intech.yayabureau.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intech.yayabureau.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.content.ContentValues.TAG;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class Add_Candidate extends AppCompatActivity {
    private EditText InputDOB,InputFirstName,InputMiddleName,InputSurName,InputMobile,InputCounty
            ,InputWard,InputVillage,InputNextOfKin,InputKinMobile,InputSalary,InputResidence,InputID;
    private String firstName,middleName,surName,mobile,county,ward,
            village,nextOfKin,kinMobile,experience,salary,residence,dob,idNo,gender,age;
    private Spinner InputGender,InputExperience;
    private CircleImageView profilePhoto;
    private FloatingActionButton addProfilePhoto;
    DatePickerDialog datePickerDialog;
    private Button UploadDetails;

    private Uri ImageUri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference BureauRef = db.collection("Yaya_Bureau");

    FirebaseAuth mAuth;
    private UploadTask uploadTask;
    private ProgressDialog progressDialog;
    FirebaseStorage storage;
    StorageReference storageReference;
    int PERMISSION_ALL = 20003;
    private Bitmap compressedImageBitmap;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private TextView AgeText,ToManiView;
    private LinearLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__candidate);
        mAuth = FirebaseAuth.getInstance();
        InputDOB = findViewById(R.id.dob);
        InputFirstName = findViewById(R.id.Cfirst_name);
        InputMiddleName = findViewById(R.id.Cmiddle_name);
        InputSurName = findViewById(R.id.surname);
        InputID = findViewById(R.id.Cid_no);
        InputMobile = findViewById(R.id.mobile_no);
        InputCounty = findViewById(R.id.home_county);
        InputVillage = findViewById(R.id.village);
        InputGender = findViewById(R.id.gender);
        InputNextOfKin = findViewById(R.id.kin_name);
        InputKinMobile = findViewById(R.id.kin_number);
        InputExperience = findViewById(R.id.experience);
        InputSalary = findViewById(R.id.salary);
        InputResidence = findViewById(R.id.residence);
        addProfilePhoto = findViewById(R.id.add_profilePhoto);
        profilePhoto = findViewById(R.id.profile);
        UploadDetails = findViewById(R.id.upload_candidate);
        InputWard = findViewById(R.id.home_ward);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        AgeText = findViewById(R.id.age);
        relativeLayout = findViewById(R.id.LinearLayoutAdd);
        ToManiView = findViewById(R.id.BackToMainView);


        ToManiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MyCandidatesActivity.class));
            }
        });


        UploadDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validation()){

                }else {

                    if (ImageUri!=null){
                        Store_Image_and_Details();

                    }else {
                        ToastBack("Please select an Image");
                    }

                }
            }
        });

        addProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                    ActivityCompat.requestPermissions(Add_Candidate.this, PERMISSIONS, PERMISSION_ALL);
                }else {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(512,512)
                            .setAspectRatio(1,1)
                            .start(Add_Candidate.this);
                }
            }
        });


        final Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog  StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                dob = formatter.format(newDate.getTime());
                Date now = new Date();
                age = String.valueOf(getYear(newDate.getTime(),now));
                AgeText.setText(age);
                InputDOB.setText(formatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        InputDOB.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            StartTime.show();
        }
    });


        LoadDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    //data.getData returns the content URI for the selected Image
                    ImageUri = result.getUri();
                    profilePhoto.setImageURI(ImageUri);
                    break;
            }
    }



    private void Notify(String id){
        HashMap<String ,Object> notify = new HashMap<>();
        notify.put("title","New candidate");
        notify.put("desc","You have successful added "+firstName +" " + middleName +" "+surName);
        notify.put("to",id);
        notify.put("type","Added a new candidate.");
        notify.put("from",mAuth.getCurrentUser().getUid());
        notify.put("timestamp",FieldValue.serverTimestamp());


        BureauRef.document(mAuth.getCurrentUser().getUid()).collection("Notifications")
                .document().set(notify)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }else {

                        }
                    }
                });


    }


    int getYear(Date date1,Date date2){
        SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy");
        Integer.parseInt(simpleDateformat.format(date1));

        return Integer.parseInt(simpleDateformat.format(date2))- Integer.parseInt(simpleDateformat.format(date1));

    }

    private void Store_Image_and_Details() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait uploading details...");
        progressDialog.show();
        File newimage = new File(ImageUri.getPath());
        firstName = InputFirstName.getText().toString().trim();
        middleName = InputMiddleName.getText().toString().trim();
        surName = InputSurName.getText().toString().trim();
        idNo= InputID.getText().toString().trim();
        gender = InputGender.getSelectedItem().toString().trim();
        dob = InputDOB.getText().toString().trim();
        mobile = InputMobile.getText().toString().trim();
        county = InputCounty.getText().toString().trim();
        ward= InputWard.getText().toString().trim();
        village = InputVillage.getText().toString().trim();
        nextOfKin = InputNextOfKin.getText().toString().trim();
        kinMobile = InputKinMobile.getText().toString().trim();
        experience = InputExperience.getSelectedItem().toString().trim();
        residence = InputResidence.getText().toString().trim();
        salary = InputSalary.getText().toString().trim();

        InputGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = InputGender.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        try {
            Compressor compressor = new Compressor(this);
            compressor.setMaxHeight(200);
            compressor.setMaxWidth(200);
            compressor.setQuality(10);
            compressedImageBitmap = compressor.compressToBitmap(newimage);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        final byte[] data = baos.toByteArray();


        final StorageReference ref = storageReference.child("Users/thumbs" + UUID.randomUUID().toString());
        uploadTask = ref.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();
                String profileImage = downloadUri.toString();


                String CandidateID = CandidateRef.document().getId();

                String token_Id = FirebaseInstanceId.getInstance().getToken();

                HashMap<String,Object> store = new HashMap<>();
                store.put("Candidate_name",firstName +" "+ middleName +" "+ surName);
                store.put("Gender",gender);
                store.put("ID_no",idNo);
                store.put("DOB",dob);
                store.put("Mobile_no",mobile);
                store.put("device_token",token_Id);
                store.put("Profile_image",profileImage);
                store.put("County",county);
                store.put("Ward",ward);
                store.put("Village",village);
                store.put("Next_of_kin",nextOfKin);
                store.put("Kin_phone_no",kinMobile);
                store.put("Experience",experience);
                store.put("Status","Available");
                store.put("timestamp",FieldValue.serverTimestamp());
                store.put("User_ID",mAuth.getCurrentUser().getUid());
                store.put("Salary",salary);
                store.put("Age",age);
                store.put("BureauName",BureauName);
                store.put("BureauNo",BureauNo);
                store.put("CandidateID",CandidateID);
                store.put("Residence",residence);
                store.put("Employer_name", "");
                store.put("Employer_no", "");
                store.put("Employer_county", "");
                store.put("Employer_city", "");
                store.put("Working_status", "");

                CandidateRef.document(CandidateID).set(store).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                           CountUser();
                        }else {

                            ToastBack("Registration failed try Again.");
                            progressDialog.dismiss();

                        }
                    }
                });



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                ToastBack(e.getMessage());

            }
        });


    }

    //----Load details---//
    private String userName,email,BureauName,BureauNo;
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
                    userName = documentSnapshot.getString("Name");
                    email = documentSnapshot.getString("Email");
                    BureauName = documentSnapshot.getString("Bureau_Name");
                    noOfCandidates = documentSnapshot.getLong("No_of_candidates");
                    BureauNo = documentSnapshot.getString("Phone_NO");

                }
            }
        });



    }
    //...end load details

    private Snackbar snackbar;
    private void CountUser() {
        double total = noOfCandidates + 1;
        WriteBatch batch;
        batch = db.batch();
        DocumentReference doc1 = BureauRef.document(mAuth.getCurrentUser().getUid());
        batch.update(doc1, "No_of_candidates", total);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Notify(mAuth.getCurrentUser().getUid());
                    snackbar = Snackbar.make(relativeLayout, "Request sent successful", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("NOTIFY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });

                    snackbar.show();
                    progressDialog.dismiss();
                    getAvailableCounts("Available");

                }else {
                    ToastBack(task.getException().getMessage());
                    progressDialog.dismiss();
                }
            }
        });
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
                    ToastBack("Candidate successful registered");
                    Intent logout = new Intent(getApplicationContext(), MyCandidatesActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logout);
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
                    ToastBack("Candidate successful registered");
                    Intent logout = new Intent(getApplicationContext(), MyCandidatesActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logout);
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


    private boolean validation() {
        firstName = InputFirstName.getText().toString().trim();
        middleName = InputMiddleName.getText().toString().trim();
        surName = InputSurName.getText().toString().trim();
        idNo= InputID.getText().toString().trim();
        gender = InputGender.getSelectedItem().toString().trim();
        dob = InputDOB.getText().toString().trim();
        mobile = InputMobile.getText().toString().trim();
        county = InputCounty.getText().toString().trim();
        ward= InputWard.getText().toString().trim();
        village = InputVillage.getText().toString().trim();
        nextOfKin = InputNextOfKin.getText().toString().trim();
        kinMobile = InputKinMobile.getText().toString().trim();
        experience = InputExperience.getSelectedItem().toString().trim();
        residence = InputResidence.getText().toString().trim();
        salary = InputSalary.getText().toString().trim();

        if (firstName.isEmpty()){
            ToastBack("Provide first name");
            return false;
        }
        else if (middleName.isEmpty()){
            ToastBack("Provide middle name");
            return false;
        }
        else if (surName.isEmpty()){
            ToastBack("Provide Surname");
            return false;
        }
        else if (idNo.isEmpty()){
            ToastBack("Provide an ID number");
            return false;
        }
        else if (gender.equals("Select gender")){
            ToastBack("Provide a gender");
            return false;
        }
        else if (dob.isEmpty()){
            ToastBack("Provide date of birth");
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
        else if (village.isEmpty()){
            ToastBack("Provide village.");
            return false;
        }
        else if (nextOfKin.isEmpty()){
            ToastBack("Provide your next of kin.");
            return false;
        }
        else if (kinMobile.isEmpty()){
            ToastBack("Provide next of kin number.");
            return false;
        }
        else if (experience.equals("Select experience")){
            ToastBack("Provide experience.");
            return false;
        }
        else if (residence.isEmpty()){
            ToastBack("Provide residence.");
            return false;
        }
        else{
            return true;
        }
    }


    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        backToast.show();
    }

}