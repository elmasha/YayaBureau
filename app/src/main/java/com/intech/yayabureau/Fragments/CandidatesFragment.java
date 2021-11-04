package com.intech.yayabureau.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.intech.yayabureau.Activities.Add_Candidate;
import com.intech.yayabureau.Activities.UpdateCandidateActivity;
import com.intech.yayabureau.Adapters.CandidateAdapter;
import com.intech.yayabureau.Models.Candidates;
import com.intech.yayabureau.R;

import javax.annotation.Nullable;


public class CandidatesFragment extends Fragment {
View root;
    private LinearLayout imageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    private TextView textUser,textEmail,textBureauName,countCandidate;
    private FloatingActionButton addCandidate;
    private CandidateAdapter adapter;
    private RecyclerView mRecyclerView;
    public CandidatesFragment() {
        // Required empty public constructor
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
        countCandidate = root.findViewById(R.id.C_Count);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchProduct();
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

    return root;
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
    private String userName,email,BureauName;
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
                    userName = documentSnapshot.getString("Name");
                    email = documentSnapshot.getString("Email");
                    BureauName = documentSnapshot.getString("Bureau_Name");
                    noOfCandidates = documentSnapshot.getLong("No_of_candidates");


                    if (noOfCandidates == 0){
                        imageView.setVisibility(View.VISIBLE);
                    }else {
                        imageView.setVisibility(View.GONE);
                    }

                    textUser.setText(userName);
                    textEmail.setText(email);
                    textBureauName.setText(BureauName);
                    countCandidate.setText(noOfCandidates+"");
                }
            }
        });



    }
    //...end load details
}