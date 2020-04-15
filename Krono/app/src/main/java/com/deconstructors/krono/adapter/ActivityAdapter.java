package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Activity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ActivityAdapter extends FirestoreRecyclerAdapter<Activity, ActivityAdapter.ActivityHolder>
{
    private ActivityClickListener ClickListener;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public ActivityAdapter(@NonNull FirestoreRecyclerOptions<Activity> options,
                           ActivityClickListener clickListener)
    {
        super(options);
        this.ClickListener = clickListener;
    }

    /************************************************************************
     * Purpose:         View Holder
     * Precondition:    .
     * Postcondition:   Inflate the layout to Recycler List View
     *                  Using the ui_drawer_listitem.XML File
     ************************************************************************/

    @NonNull
    @Override
    public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.activity_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);

        return new ActivityHolder(view, this.ClickListener);
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    protected void onBindViewHolder(@NonNull ActivityHolder holder,
                                    int position,
                                    @NonNull Activity model)
    {
        holder.Title.setText(model.getTitle());
        holder.Description.setText(model.getDescription());
    }

    public class ActivityHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener
    {
        TextView Title;
        TextView Description;
        ActivityClickListener ClickListener;

        public ActivityHolder(View itemView, ActivityClickListener clickListener)
        {
            super(itemView);

            this.Title = itemView.findViewById(R.id.activitylist_title_text);
            this.Description = itemView.findViewById(R.id.activitylist_description_text);
            this.ClickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            this.ClickListener.onActivitySelected(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v)
        {
            // Initiate an Active view multi selection
            return false;
        }
    }

    public interface ActivityClickListener
    {
        void onActivitySelected(int position);
    }
}
