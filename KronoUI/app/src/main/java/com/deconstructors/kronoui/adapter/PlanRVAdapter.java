package com.deconstructors.kronoui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.module.Plan;

import java.util.List;

/************************************************************************
 * Class:           PlansListAdapter
 * Purpose:         To customize list view layout
 ************************************************************************/
public class PlanRVAdapter extends RecyclerView.Adapter<PlanRVAdapter.ViewHolder>
{
    public List<Plan> PlanList;
    private PlanRVClickListener ClickListener;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public PlanRVAdapter(List<Plan> planList, PlanRVClickListener clickListener)
    {
        this.PlanList = planList;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.plan_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
        ViewHolder viewholder = new ViewHolder(view, this.ClickListener);
        return viewholder;
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.titleText.setText(this.PlanList.get(position).getTitle());
        //holder.descriptionText.setText(this.PlanList.get(position).getDescription());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public int getItemCount()
    {
        return this.PlanList == null ? 0 : this.PlanList.size();
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView titleText;
        //TextView descriptionText;
        PlanRVClickListener ClickListener;

        public ViewHolder(@NonNull View itemView, PlanRVClickListener clickListener)
        {
            super(itemView);

            this.titleText = (TextView) itemView.findViewById(R.id.planlist_title_text);
            //this.descriptionText = (TextView) itemView.findViewById(R.id.planlist_description_text);
            this.ClickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            this.ClickListener.onPlanSelected(getAdapterPosition());
        }
    }

    public interface PlanRVClickListener
    {
        void onPlanSelected(int position);
    }
}

