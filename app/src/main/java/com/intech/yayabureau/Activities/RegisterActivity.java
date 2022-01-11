package com.intech.yayabureau.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.intech.yayabureau.Interface.RetrofitInterface;
import com.intech.yayabureau.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.intech.yayabureau.Activities.Add_Candidate.hasPermissions;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth  mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    private Button btnRegister;
    private EditText BureauName,FirstName,MiddleName,LastName,IDNumber,BuildingName,
            StreetName,City,County,Email,BoxNo,PostalCode,
    Telephone;
    private String firstName,middleName,lastName,bureauName,idNumber,buildingName,streetName,city,county,email,boxNO,
    postalCode,telephone;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mToken;
    private static final long START_TIME_IN_MILLIS_COUNT = 27000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;
    private ProgressDialog progressDialog2,progressDialog;
    private TextView toLogin,toMain;
    private CountryCodePicker CodePicker;
    private FloatingActionButton add_profile;
    private CircleImageView imageView;

    private Uri ImageUri;
    private UploadTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    int PERMISSION_ALL = 20003;
    private Bitmap compressedImageBitmap;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

    public  String CheckoutRequestID,ResponseCode,ResultCode,ResponseDescription,ResultDesc;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://yayampesapi.herokuapp.com/";

    private boolean preference_count;
    private String mpesa_receipt,checkOutReqID;
    private String payment_date;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        BureauName = findViewById(R.id.bureau_name);
        FirstName = findViewById(R.id.first_name);
        MiddleName = findViewById(R.id.middle_name);
        LastName = findViewById(R.id.last_name);
        IDNumber = findViewById(R.id.id_no);
        BuildingName = findViewById(R.id.building_name);
        StreetName = findViewById(R.id.street_name);
        City = findViewById(R.id.city);
        County = findViewById(R.id.county);
        Email = findViewById(R.id.email_address);
        BoxNo = findViewById(R.id.box_no);
        PostalCode = findViewById(R.id.postal_code);
        Telephone = findViewById(R.id.phone_no);
        btnRegister = findViewById(R.id.Btn_register);
        toLogin = findViewById(R.id.ToLogin);
        toMain = findViewById(R.id.BackToMain2);
        CodePicker = findViewById(R.id.Phone_picker1);
        add_profile = findViewById(R.id.add_Photo);
        imageView = findViewById(R.id.profileImage);

        CodePicker.registerCarrierNumberEditText(Telephone);
        CodePicker.getDefaultCountryCode();
        ///---initiate Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);


        add_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                    ActivityCompat.requestPermissions(RegisterActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(512,512)
                            .setAspectRatio(1,1)
                            .start(RegisterActivity.this);
                }
            }
        });

        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()){

                }else {
                    progressDialog2 = new ProgressDialog(RegisterActivity.this);
                    progressDialog2.setMessage("Please wait");
                    progressDialog2.show();
                    telephone = CodePicker.getFullNumberWithPlus();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            telephone,
                            60,
                            TimeUnit.SECONDS,
                            RegisterActivity.this,
                            mCallBacks);
//                    Register_Bureau();
                }
            }
        });


        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                ToastBack(e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken Token) {

                mVerificationId = verificationId;
                mToken = Token;
                progressDialog2.dismiss();
                Verify_Dialog();
                startTimer();

            }
        };



    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            progressDialog = new ProgressDialog(RegisterActivity.this);
                            progressDialog.setMessage("Please wait signing Up...");
                            progressDialog.show();

                            if (dialog_Verify != null)
                                dialog_Verify.dismiss();
                            pauseTimer();
                            resetTimer();
                            Register_Bureau(mAuth.getCurrentUser().getUid());
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            ToastBack("Sign in failed");
                            Btn_Verify.setVisibility(View.VISIBLE);
                            progressBar_verify.setVisibility(View.INVISIBLE);

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invali
                                ToastBack("The verification code entered was invalid");



                            }
                        }
                    }
                });
    }

    private void Register_Bureau(String uid) {
        File newimage = new File(ImageUri.getPath());
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
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String profileImage = downloadUri.toString();

                    String token_Id = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String,Object> registerB = new HashMap<>();
                    registerB.put("Name", firstName +" "+ middleName +" "+ lastName);
                    registerB.put("Bureau_Name",bureauName);
                    registerB.put("Bureau_Image",profileImage);
                    registerB.put("ID_no",idNumber);
                    registerB.put("Building",buildingName);
                    registerB.put("Street_name",streetName);
                    registerB.put("City",city);
                    registerB.put("County",county);
                    registerB.put("Email",email);
                    registerB.put("Box_No",boxNO);
                    registerB.put("Postal_code",postalCode);
                    registerB.put("Phone_NO",telephone);
                    registerB.put("device_token",token_Id);
                    registerB.put("No_of_candidates",0);
                    registerB.put("RegistrationFee","0");
                    registerB.put("timestamp", FieldValue.serverTimestamp());

                    BureauRef.document(uid).set(registerB).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                ToastBack("Registration successful");
                                Intent toreg = new Intent(getApplicationContext(),MyCandidatesActivity.class);
                                startActivity(toreg);
                                finish();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    //data.getData returns the content URI for the selected Image
                    ImageUri = result.getUri();
                    if (ImageUri != null){
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageURI(ImageUri);
                        add_profile.setVisibility(View.GONE);
                    }

                    break;
            }
    }

    private EditText code1;
    private ProgressBar progressBar_verify;
    private TextView resend,timer,sentTO,closeDia;
    private Button Btn_Verify;
    private AlertDialog dialog_Verify;
    private void Verify_Dialog(){

        final  AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_otp, null);
        mbuilder.setView(mView);
        mbuilder.setCancelable(true);
        dialog_Verify = mbuilder.create();
        dialog_Verify.show();

        code1 = mView.findViewById(R.id.otp_verify);

        Btn_Verify = mView.findViewById(R.id.btn_verify);
        resend = mView.findViewById(R.id.otp_resend);
        timer = mView.findViewById(R.id.otp_timer);
        progressBar_verify = mView.findViewById(R.id.progress_VScode);

        ThreeBounce rotatingCircle = new ThreeBounce();
        progressBar_verify.setIndeterminateDrawable(rotatingCircle);


        Btn_Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Btn_Verify.setVisibility(View.INVISIBLE);
                progressBar_verify.setVisibility(View.VISIBLE);
                String Verifycode = code1.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,Verifycode);
                signInWithPhoneAuthCredential(credential);


            }
        });




    }


    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {

                ToastBack("Time out");
                dialog_Verify.dismiss();

            }
        }.start();
        mTimerRunning = true;

    }

    private void pauseTimer() {
//        mCountDownTimer.cancel();
        mTimerRunning = false;

    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;
        updateCountDownText();

    }


    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

    }

    private boolean validation(){
        firstName = FirstName.getText().toString();
        middleName = MiddleName.getText().toString();
        lastName = LastName.getText().toString();
        idNumber = IDNumber.getText().toString();
        buildingName = BuildingName.getText().toString();
        streetName = StreetName.getText().toString();
        city = City.getText().toString();
        county = County.getText().toString();
        email = Email.getText().toString();
        boxNO = BoxNo.getText().toString();
        postalCode = PostalCode.getText().toString();
        telephone = Telephone.getText().toString();
        bureauName = BureauName.getText().toString();


        if (firstName.isEmpty()){
            ToastBack("Provide first name");
            return false;
        }else if (middleName.isEmpty()){
            ToastBack("Provide middle name");
            return false;
        }else if (lastName.isEmpty()){
            ToastBack("Provide last name");
            return false;
        }else if (idNumber.isEmpty()){
            ToastBack("Provide ID number");
            return false;
        }else if (streetName.isEmpty()){
            ToastBack("Provide street name");
            return false;
        }else if (city.isEmpty()){
            ToastBack("Provide City/Town");
            return false;
        }else if (county.isEmpty()){
            ToastBack("Provide county");
            return false;
        }
        else if (telephone.isEmpty()){
            ToastBack("Provide your phone number.");
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