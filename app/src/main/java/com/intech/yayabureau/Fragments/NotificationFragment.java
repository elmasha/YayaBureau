package com.intech.yayabureau.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.intech.yayabureau.Activities.MyCandidatesActivity;
import com.intech.yayabureau.Adapters.NotificationAdapter;
import com.intech.yayabureau.Models.Notification;
import com.intech.yayabureau.R;
import com.intech.yayabureau.TimeAgo;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.nikartm.support.BadgePosition;


public class NotificationFragment extends Fragment {
View root;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    private CircleImageView profileImage;

    private LinearLayout imageView,ErrorLayout;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private NotificationAdapter adapter;
    private RecyclerView mRecyclerView;
    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onStart() {
        super.onStart();
        FetchNotification();
    }

    @Override
    public void onResume() {
        super.onResume();
        FetchNotificationCount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       root = inflater.inflate(R.layout.fragment_notification, container, false);
       mAuth = FirebaseAuth.getInstance();
       swipeRefreshLayout = root.findViewById(R.id.SwipeRefresh_notify);
       progressBar = root.findViewById(R.id.progressNotification);
       ErrorLayout = root.findViewById(R.id.ErrorLayoutNotification);
       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               FetchNotification();
               FetchNotificationCount();
               final Handler handler = new Handler();
               handler.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       swipeRefreshLayout.setRefreshing(false);
                   }
               }, 1000);
           }
       });
       mRecyclerView = root.findViewById(R.id.recycler_notification);
        FetchNotification();
        FetchNotificationCount();
        return root;
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

                                if (sumNotify <= 0){
                                    progressBar.setVisibility(View.GONE);
                                    ErrorLayout.setVisibility(View.VISIBLE);
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    ErrorLayout.setVisibility(View.GONE);
                                }



                            }

                        } else {

                        }


                    }
                });

    }


    private void FetchNotification() {
        Query query =  BureauRef.document(mAuth.getCurrentUser().getUid())
                .collection("Notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);
        FirestoreRecyclerOptions<Notification> transaction = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new NotificationAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(LayoutManager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NotificationAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                deletePosition = position;
                Notification notification = documentSnapshot.toObject(Notification.class);
                title1 = notification.getTitle();
                desc1 = notification.getDesc();
                type1 = notification.getType();
                time1 = TimeAgo.getTimeAgo(notification.getTimestamp().getTime());
                ShowNotification();
                HashMap<String,Object> noty = new HashMap<>();
                noty.put("status","seen");
                BureauRef.document(mAuth.getCurrentUser().getUid())
                        .collection("Notifications").document(documentSnapshot.getId())
                        .update(noty).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FetchNotification();
                            FetchNotificationCount();
                        }
                    }
                });
            }
        });

    }



    ArrayList<Object> uniqueCount = new ArrayList<Object>();
    int sumCanidate ;
    private void NotifyCount() {
        CandidateRef.whereEqualTo("User_ID",mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                uniqueCount.add(document.getData());
                                sumCanidate = 0;
                                for ( sumCanidate = 0; sumCanidate < uniqueCount.size(); sumCanidate++) {

                                }
                            }

                            if (sumCanidate <= 0){
                                progressBar.setVisibility(View.GONE);
                                ErrorLayout.setVisibility(View.VISIBLE);
                            }else {
                                progressBar.setVisibility(View.GONE);
                                ErrorLayout.setVisibility(View.GONE);
                            }
                        } else {

                        }
                    }
                });

    }





    private TextView time, title,type,PayID,desc;
    private String time1, title1,type1,PayID1,desc1;
    private int deletePosition;
    private FloatingActionButton deletePay;
    private AlertDialog dialog3;
    public void ShowNotification() {

        AlertDialog.Builder mbuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_receipt, null);
        mbuilder.setView(mView);
        dialog3 = mbuilder.create();
        dialog3.setCancelable(true);
        dialog3.show();

        title = mView.findViewById(R.id.payhistName);
        time = mView.findViewById(R.id.payhistTime);
        desc = mView.findViewById(R.id.payhistUser);
        type = mView.findViewById(R.id.payhistType);
        deletePay = mView.findViewById(R.id.payhistDelete);

        time.setText(time1);
        title.setText(title1);
        type.setText(type1);
        desc.setText(desc1);



        deletePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete transaction");
                builder.setMessage("Are you sure you want to delete this notification ?");
                builder.setIcon(R.drawable.ic_delete_blue);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.deleteItem(deletePosition);
                                Toast.makeText(getContext(), "Notification deleted", Toast.LENGTH_SHORT).show();
                                FetchNotification();
                                dialog.dismiss();
                                if (dialog3 != null)dialog3.dismiss();
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FetchNotification();
                                dialog.dismiss();
                            }
                        });

                builder.setCancelable(false);
                builder.show();


            }
        });



    }

}