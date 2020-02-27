package com.demo.planactivityuserdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.planactivityuserdemo.R;
import com.demo.planactivityuserdemo.model.Plan;
import java.util.ArrayList;

public class PlansRecyclerAdapter extends RecyclerView.Adapter<PlansRecyclerAdapter.ViewHolder>
{
    private ArrayList<Plan> PlanList;
    private PlansRecyclerClickListener ClickListener;

    public PlansRecyclerAdapter(ArrayList<Plan> planList, PlansRecyclerClickListener clickListener)
    {
        this.PlanList = planList;
        this.ClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.ui_plans_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
        final ViewHolder holder = new ViewHolder(view, this.ClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.Title.setText(PlanList.get(position).getTitle());
        holder.Description.setText(PlanList.get(position).getDescription());
    }

    @Override
    public int getItemCount()
    {
        return this.PlanList == null ? 0 : this.PlanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener
    {
        TextView Title;
        TextView Description;
        PlansRecyclerClickListener ClickListener;

        public ViewHolder(@NonNull View itemView, PlansRecyclerClickListener clickListener)
        {
            super(itemView);
            this.Title = itemView.findViewById(R.id.plans_title);
            this.Description = itemView.findViewById(R.id.plans_description);
            this.ClickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            this.ClickListener.onPlanSelected(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v)
        {
            // Initiate an Active view multi selection
            return false;
        }
    }

    public interface PlansRecyclerClickListener
    {
        void onPlanSelected(int position);
    }
}
