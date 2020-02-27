package com.demo.planactivityuserdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.planactivityuserdemo.R;
import com.demo.planactivityuserdemo.model.Activity;
import java.util.ArrayList;

public class ActivitiesRecyclerAdapter extends RecyclerView.Adapter<ActivitiesRecyclerAdapter.ViewHolder>
{
    private ArrayList<Activity> ActivityList;
    private ActivitiesRecyclerClickListener ClickListener;

    public ActivitiesRecyclerAdapter(ArrayList<Activity> activityList, ActivitiesRecyclerClickListener clickListener)
    {
        this.ActivityList = activityList;
        this.ClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.ui_activities_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
        final ViewHolder holder = new ViewHolder(view, this.ClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.Title.setText(ActivityList.get(position).getTitle());
        holder.Description.setText(ActivityList.get(position).getDescription());
    }

    @Override
    public int getItemCount()
    {
        return this.ActivityList == null ? 0 : this.ActivityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener
    {
        TextView Title;
        TextView Description;
        ActivitiesRecyclerClickListener ClickListener;

        public ViewHolder(View itemView, ActivitiesRecyclerClickListener clickListener)
        {
            super(itemView);
            this.Title = itemView.findViewById(R.id.activities_title);
            this.Description = itemView.findViewById(R.id.activities_description);
            this.ClickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            this.ClickListener.onActiviySelected(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v)
        {
            // Initiate an Active view multi selection
            return false;
        }
    }

    public interface ActivitiesRecyclerClickListener
    {
        void onActiviySelected(int position);
    }
}
