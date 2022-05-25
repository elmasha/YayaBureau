package com.intech.yayabureau.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.intech.yayabureau.Activities.Add_Candidate;
import com.intech.yayabureau.Activities.UpdateCandidateActivity;
import com.intech.yayabureau.Adapters.CandidateAdapter;
import com.intech.yayabureau.Models.Bureau;
import com.intech.yayabureau.Models.Candidates;
import com.intech.yayabureau.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;


public class  CandidatesFragment extends Fragment {
View root;
    private LinearLayout imageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    private TextView textUser,textEmail,textBureauName,countCandidate,AvailableCountText,BureauNameText,UnAvailableCountText;
    private FloatingActionButton addCandidate;
    private CandidateAdapter adapter;
    private RecyclerView mRecyclerView;
    private CircleImageView profileImage;

    public CandidatesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchProduct();
        LoadDetails();
        FetchCandidateCount();
        FetchAvailableCount();
        FetchUnAvailableCount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_candidates, container, false);
        mAuth = FirebaseAuth.getInstance();
        textUser = root.findViewById(R.id.user_name);
        textEmail = root.findViewById(R.id.user_email);
        addCandidate = root.findViewById(R.id.Add_candidate);
        textBureauName = root.findViewById(R.id.bureau_name);
        imageView = root.findViewById(R.id.files);
        swipeRefreshLayout = root.findViewById(R.id.SwipeRefresh);
        mRecyclerView = root.findViewById(R.id.recycler_candidate);
        countCandidate = root.findViewById(R.id.CountCandidate);
        profileImage = root.findViewById(R.id.userImageCandidate);
        AvailableCountText = root.findViewById(R.id.AvailableCount);
        BureauNameText = root.findViewById(R.id.MyBureauName);
        UnAvailableCountText = root.findViewById(R.id.UnAvailableCount);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchProduct();
                LoadDetails();
                FetchAvailableCount();
                FetchUnAvailableCount();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });


        addCandidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Add_Candidate.class));
            }
        });
        FetchProduct();
        LoadDetails();
        FetchCandidateCount();
        FetchAvailableCount();
       FetchUnAvailableCount();
    return root;
    }


    ArrayList<Object> uniqueDates = new ArrayList<Object>();
    int sum ;
    private void FetchCandidateCount() {
        CandidateRef.whereEqualTo("User_ID",mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                uniqueDates.add(document.getData());
                                 sum = 0;
                                for (sum = 0; sum < uniqueDates.size(); sum++) {
                                }

                            }

                        } else {

                        }
                    }
                });

    }

    private void StoreCount(int sum) {
        HashMap<String,Object> updateCount = new HashMap<>();
        updateCount.put("No_of_candidates",sum);
        BureauRef.document(mAuth.getCurrentUser().getUid()).update(updateCount);
    }


    private void FetchProduct() {

        Query query = CandidateRef.whereEqualTo("User_ID",mAuth.getCurrentUser().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING).limit(30);
        FirestoreRecyclerOptions<Candidates> transaction = new FirestoreRecyclerOptions.Builder<Candidates>()
                .setQuery(query, Candidates.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new CandidateAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CandidateAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Candidates  candidates = documentSnapshot.toObject(Candidates.class);
                String id = documentSnapshot.getId();
                Intent toUpdate = new Intent(getContext(), UpdateCandidateActivity.class);
                toUpdate.putExtra("ID",id);
                startActivity(toUpdate);

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
                    Bureau  bureau = documentSnapshot.toObject(Bureau.class);
                    userName = bureau.getName();
                    email = bureau.getEmail();
                    BureauName = bureau.getBureau_Name();
                    noOfCandidates = bureau.getNo_of_candidates();
                    ImageBureau = bureau.getBureau_Image();

                    if (noOfCandidates == 0){
                        imageView.setVisibility(View.VISIBLE);
                    }else {
                        imageView.setVisibility(View.GONE);
                    }


                    if (ImageBureau != null){
                        Picasso.with(profileImage.getContext())
                                .load(ImageBureau).placeholder(R.drawable.load)
                                .error(R.drawable.user)
                                .into(profileImage);
                    }


                    textUser.setText(userName);
                    textEmail.setText(email);
                    BureauNameText.setText(BureauName);
                    countCandidate.setText(noOfCandidates+"");

                }
            }
        });



    }
    //...end load details




    ArrayList<Object> uniqueAvailable = new ArrayList<Object>();
    int sumAvail2 = 0;
    private void FetchAvailableCount() {
        CandidateRef.whereEqualTo("User_ID",mAuth.getCurrentUser().getUid())
                .whereEqualTo("Status","Available")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            uniqueAvailable.clear();
                            sumAvail2 = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                uniqueAvailable.add(document.getData());
                                for (sumAvail2 = 0; sumAvail2 < uniqueAvailable.size(); sumAvail2++) {

                                }
                                AvailableCountText.setText(sumAvail2+"");
                                // ToastBack("Notifications: "+sum+"");
                            }

                        } else {

                        }


                    }
                });

    }


    ArrayList<Object> uniqueAvailable2 = new ArrayList<Object>();
    int sumAvail = 0;
    private void FetchUnAvailableCount() {
        CandidateRef.whereEqualTo("User_ID",mAuth.getCurrentUser().getUid())
                .whereEqualTo("Status","UnAvailable")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            uniqueAvailable2.clear();
                            sumAvail = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                uniqueAvailable2.add(document.getData());
                                for (sumAvail = 0; sumAvail < uniqueAvailable2.size(); sumAvail++) {

                                }
                                UnAvailableCountText.setText(sumAvail+"");
                                // ToastBack("Notifications: "+sum+"");
                            }

                        } else {

                        }


                    }
                });

    }






}