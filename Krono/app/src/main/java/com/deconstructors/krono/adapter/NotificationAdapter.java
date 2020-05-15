package com.deconstructors.krono.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder>
{
    //Can add more notification types here later if/when we expand notifications
    private final int FRIEND_REQUEST = 0;

    //Database
    private FirebaseFirestore DBInstance;

    //Data Members
    private String HeaderText;
    private String DescriptionText;
    private ArrayList<String> friendRequestNames;

    public NotificationAdapter(ArrayList<String> Names, int NotificationType)
    {
        this.DBInstance = FirebaseFirestore.getInstance();
        this.friendRequestNames = Names;

        switch(NotificationType)
        {
            case FRIEND_REQUEST:
            {
                this.HeaderText = "Friend Request";
                this.DescriptionText = "You have a new friend request from ";
                break;
            }
            default:
            {
                this.HeaderText = "";
                this.DescriptionText = "";
                break;
            }
        }
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        int res = R.layout.notif_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);

        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.NotificationHolder NotifHolder, int position)
    {
        String CurrentDescriptionText = DescriptionText + friendRequestNames.get(position);

        NotifHolder.HeaderTextView.setText(HeaderText);
        NotifHolder.DescriptionTextView.setText(CurrentDescriptionText);
    }

    @Override
    public int getItemCount()
    {
        return friendRequestNames.size();
    }

    public class NotificationHolder extends RecyclerView.ViewHolder
    {
        TextView HeaderTextView, DescriptionTextView;

        public NotificationHolder(View itemView)
        {
            super(itemView);

            HeaderTextView = (TextView) itemView.findViewById(R.id.notif_Header);
            DescriptionTextView = (TextView) itemView.findViewById(R.id.notif_Description);
        }
    }
}
