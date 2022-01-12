package com.intech.yayabureau.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.intech.yayabureau.Models.Notification;
import com.intech.yayabureau.R;
import com.intech.yayabureau.TimeAgo;

import java.util.List;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.ProviderViewHolder> {

    private OnItemCickListener listener;
    public Context context;


    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProviderViewHolder holder, int position, @NonNull Notification model) {

        if (model.getTitle() != null | model.getDesc() != null |model.getTimestamp() != null ){
            holder.title.setText(model.getTitle());
            holder.desc.setText(model.getDesc());
            holder.time.setText(TimeAgo.getTimeAgo(model.getTimestamp().getTime()));
        }


    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row,parent,false);
        this.context = parent.getContext();
        return new ProviderViewHolder(v);
    }


    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder{
       private TextView title, desc, time;

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notify_title);
            desc = itemView.findViewById(R.id.notify_description);
            time = itemView.findViewById(R.id.notify_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);


                    }
                }
            });



        }
    }


    public interface  OnItemCickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemCickListener listener){

        this.listener = listener;

    }




}
