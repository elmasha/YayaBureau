package com.intech.yayabureau.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import com.intech.yayabureau.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView timer,onSendNo;
    private EditText InputCode,InputPhone;
    private Button ReqOTP,VeryCode;
    private CountryCodePicker CodePicker;
    private LinearLayout LayoutSendCode;
    private RelativeLayout LayoutVerifyCode;
    private String Phone_Number,number;

    private static final long START_TIME_IN_MILLIS_COUNT = 60000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;
    private ProgressBar progressBar,progressBar2;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mToken;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference BureauRef = db.collection("Yaya_Bureau");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        timer = findViewById(R.id.timer);
        onSendNo = findViewById(R.id.Pass_vcode);
        InputPhone = findViewById(R.id.Phone_number);
        InputCode = findViewById(R.id.VerifyCode);
        ReqOTP = findViewById(R.id.send_Otp);
        CodePicker = findViewById(R.id.Phone_picker);
        VeryCode = findViewById(R.id.send_Vcode);

        LayoutSendCode = findViewById(R.id.LayoutOTP);
        LayoutVerifyCode = findViewById(R.id.LayoutVerify);

        progressBar = findViewById(R.id.progress_VOtp);
        progressBar2 = findViewById(R.id.progress_Vcode);

        CodePicker.registerCarrierNumberEditText(InputPhone);
        CodePicker.getDefaultCountryCode();

        ReqOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phone_Number = CodePicker.getFullNumberWithPlus();
                number = InputPhone.getText().toString().trim();

                if (Phone_Number.isEmpty()){
                    ToastBack("Please provide your phone number");
                }else if (number.isEmpty()){
                    ToastBack("Please provide a valid phone number");
                }
                else if (number.length()<=3){
                    ToastBack("Please provide a valid phone number");
                }else if (number.length()<= 9){
                    ToastBack("Please provide a valid phone number");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    ReqOTP.setVisibility(View.GONE);
                    onSendNo.setText(Phone_Number);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            Phone_Number,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallBacks);

                }

            }
        });

        VeryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Verifycode = InputCode.getText().toString();
                if (Verifycode.isEmpty()){

                    ToastBack("Verification code required.");

                }else {

                    progressBar2.setVisibility(View.VISIBLE);
                    VeryCode.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,Verifycode);
                    signInWithPhoneAuthCredential(credential);

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

                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pauseTimer();
                resetTimer();
                progressBar.setVisibility(View.INVISIBLE);
                ReqOTP.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken Token) {

                mVerificationId = verificationId;
                mToken = Token;
                ReqOTP.setEnabled(true);
                ReqOTP.setVisibility(View.VISIBLE);
                LayoutVerifyCode.setVisibility(View.VISIBLE);
                startTimer();
                LayoutSendCode.setVisibility(View.GONE);



            }
        };



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
                mTimerRunning = false;
                pauseTimer();
                resetTimer();
                //Toast.makeText(PhoneAuthActivity.this, "Time out", Toast.LENGTH_SHORT).show();
                ToastBack("Verification time out");
                LayoutSendCode.setVisibility(View.VISIBLE);
                LayoutVerifyCode.setVisibility(View.GONE);
                timer.setVisibility(View.GONE);

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
        timer.setText(timeLeftFormatted);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {

        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            pauseTimer();
                            resetTimer();
                            UpdateDeviceToken();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            ReqOTP.setEnabled(true);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                ReqOTP.setEnabled(true);
                                pauseTimer();
                                resetTimer();
                                progressBar2.setVisibility(View.INVISIBLE);
                                VeryCode.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                });

    }

    private void UpdateDeviceToken() {
        String token_Id = FirebaseInstanceId.getInstance().getToken();
        HashMap<String,Object> updateToken = new HashMap<>();
        updateToken.put("device_token",token_Id);

        BureauRef.document(mAuth.getCurrentUser().getUid()).update(updateToken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                     if (task.isSuccessful()){
                         progressBar2.setVisibility(View.INVISIBLE);
                         VeryCode.setVisibility(View.VISIBLE);
                         startActivity(new Intent(getApplicationContext(),MyCandidatesActivity.class));
                     }else {

                         ToastBack(task.getException().getMessage());
                     }
                    }
                });
    }


    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = backToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.parseColor("#61EBED"), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#53ACEE"));
        backToast.show();
    }
}