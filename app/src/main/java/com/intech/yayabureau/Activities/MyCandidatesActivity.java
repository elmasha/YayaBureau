 package com.intech.yayabureau.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.intech.yayabureau.Adapters.CandidateAdapter;
import com.intech.yayabureau.Fragments.CandidatesFragment;
import com.intech.yayabureau.Fragments.NotificationFragment;
import com.intech.yayabureau.Fragments.ProfileFragment;
import com.intech.yayabureau.Models.Bureau;
import com.intech.yayabureau.Models.Candidates;
import com.intech.yayabureau.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.nikartm.support.BadgePosition;
import ru.nikartm.support.ImageBadgeView;

 public class MyCandidatesActivity extends AppCompatActivity  {
    private long backPressedTime;
    private ImageView imageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    private TextView textUser,textEmail,textBureauName,OpenDrawer,headerName,headerName2,headerEmail;
    private FloatingActionButton addCandidate;
    private CandidateAdapter adapter;
    private RecyclerView mRecyclerView;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private CircleImageView headerImage;
     private ImageBadgeView notificationBadge;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment SelectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_profile:

                    SelectedFragment = new ProfileFragment();

                    break;
                case R.id.navigation_candidate:

                    SelectedFragment = new CandidatesFragment();

                    break;

            }

            getSupportFragmentManager().beginTransaction().replace(R.id.Frame_profile,

                    SelectedFragment).commit();

            return true;
        }
    };


     @Override
     protected void onRestart() {
         super.onRestart();
         FetchNotificationCount();
     }

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_candidates);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_profile,new
                CandidatesFragment()).commit();
        mAuth = FirebaseAuth.getInstance();
        OpenDrawer = findViewById(R.id.drawerOpen);
        dl = (DrawerLayout) findViewById(R.id.drawer);
        notificationBadge = findViewById(R.id.badge);
//        dl.closeDrawer(GravityCompat.END);


        nv = (NavigationView) findViewById(R.id.navigation_menu);

        OpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dl.isDrawerVisible(GravityCompat.START)){
                    dl.openDrawer(GravityCompat.START);
                }else if (dl.isDrawerVisible(GravityCompat.START)){
                    dl.closeDrawer(GravityCompat.START);
                }
            }
        });
        notificationBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_profile,
                        new NotificationFragment()).commit();
            }
        });
        View header= nv.getHeaderView(0);
        headerName = (TextView)header.findViewById(R.id.header_name);
        headerEmail = (TextView)header.findViewById(R.id.header_email);
        headerName2 = (TextView)header.findViewById(R.id.header_name2);
        headerImage = (CircleImageView) header.findViewById(R.id.header_image);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.account:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_profile,
                                new ProfileFragment()).commit();
                        FetchNotificationCount();
                        break;

                    case R.id.notification:

                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_profile,
                                new NotificationFragment()).commit();
                        FetchNotificationCount();
                        break;
                    case R.id.share:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        // shareApp(getApplicationContext());
                        break;
                    case R.id.add_candidate:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                       startActivity(new Intent(getApplicationContext(),Add_Candidate.class));
                        FetchNotificationCount();
                        break;
                    case R.id.my_candidate:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_profile,
                                new CandidatesFragment()).commit();
                        FetchNotificationCount();
                        break;
                    case R.id.refer:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        FetchNotificationCount();
                        break;
                    case R.id.logOut:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        Logout_Alert();
                        break;

                    default:
                        return true;
                }


                return true;

            }
        });


        LoadDetails();
        FetchNotificationCount();

    }



     ArrayList<Object> uniqueNotify = new ArrayList<Object>();
     int sumNotify = 0;
     private void FetchNotificationCount() {
         BureauRef.document(mAuth.getCurrentUser().getUid()).collection("Notifications")
                 .whereEqualTo("to",mAuth.getCurrentUser().getUid())
                 .whereEqualTo("status","none")
                 .get()
                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()) {
                             uniqueNotify.clear();
                             sumNotify = 0;
                             for (QueryDocumentSnapshot document : task.getResult()) {
                                 uniqueNotify.add(document.getData());
                                 for (sumNotify = 0; sumNotify < uniqueNotify.size(); sumNotify++) {

                                 }
                                 if (sumNotify > 0){
                                     notificationBadge.setBadgeValue(sumNotify)
                                             .setMaxBadgeValue(999)
                                             .setBadgePosition(BadgePosition.TOP_RIGHT)
                                             .setBadgeTextStyle(Typeface.NORMAL)
                                             .setShowCounter(true);
                                 }else {
                                     notificationBadge.setBadgeValue(0)
                                             .setMaxBadgeValue(999)
                                             .setBadgePosition(BadgePosition.TOP_RIGHT)
                                             .setBadgeTextStyle(Typeface.NORMAL)
                                             .setShowCounter(true);
                                 }

                                 // ToastBack("Notifications: "+sum+"");
                             }

                         } else {

                         }


                     }
                 });

     }


     //----Load details---//
     private String userName,email,BureauName,ImageBureau;
     private long noOfCandidates;
     private void LoadDetails() {

         BureauRef.document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
             @Override
             public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                 @Nullable FirebaseFirestoreException e) {
                 if (e != null) {
                     return;
                 }
                 if (documentSnapshot.exists()){
                     Bureau bureau = documentSnapshot.toObject(Bureau.class);
                     userName = bureau.getName();
                     email = bureau.getEmail();
                     BureauName = bureau.getBureau_Name();
                     noOfCandidates = bureau.getNo_of_candidates();
                     ImageBureau = bureau.getBureau_Image();

                     if (ImageBureau != null){
                         Picasso.with(headerImage.getContext())
                                 .load(ImageBureau).placeholder(R.drawable.load)
                                 .error(R.drawable.user)
                                 .into(headerImage);
                     }

                     headerName.setText(BureauName);
                     headerName2.setText(userName);
                     headerEmail.setText(email);

                 }
             }
         });


     }
     //...end load details



     private AlertDialog dialog2;
    public void Logout_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog2 = builder.create();
        dialog2.show();
        builder.setIcon(R.drawable.logout);
        builder.setTitle("LogOut");
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
                    Intent logout = new Intent(getApplicationContext(), MainActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logout);
                    dialog2.dismiss();


                }else {

                    ToastBack( task.getException().getMessage());

                }

            }
        });

    }



    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        backToast.show();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2500 > System.currentTimeMillis()) {
            backToast.cancel();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();
            finish();
            return;
        } else {

              ToastBack("Double tap to exit");
              FetchNotificationCount();
            getSupportFragmentManager().beginTransaction().replace(R.id.Frame_profile,new
                    CandidatesFragment()).commit();
        }
        backPressedTime = System.currentTimeMillis();
    }





}