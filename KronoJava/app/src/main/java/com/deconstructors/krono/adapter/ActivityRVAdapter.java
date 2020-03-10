package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Activity;
import java.util.List;

public class ActivityRVAdapter extends RecyclerView.Adapter<ActivityRVAdapter.ViewHolder>
{
    private List<Activity> ActivityList;
    private ActivityRVClickListener ClickListener;

    public ActivityRVAdapter(List<Activity> activityList, ActivityRVClickListener clickListener)
    {
        this.ActivityList = activityList;
        this.ClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.ui_activity_listitem;
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
        ActivityRVClickListener ClickListener;

        public ViewHolder(View itemView, ActivityRVClickListener clickListener)
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
            this.ClickListener.onActiviySelected(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v)
        {
            // Initiate an Active view multi selection
            return false;
        }
    }

    public interface ActivityRVClickListener
    {
        void onActiviySelected(int position);
    }
}
