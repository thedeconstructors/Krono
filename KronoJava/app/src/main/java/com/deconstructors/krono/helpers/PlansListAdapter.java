package com.deconstructors.krono.helpers;

import com.deconstructors.krono.activities.plans.Plans;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;

import java.util.List;

/************************************************************************
 * Class:           PlansListAdapter
 * Purpose:         To customize list view layout
 ************************************************************************/
public class PlansListAdapter extends RecyclerView.Adapter<PlansListAdapter.ViewHolder>
{
    public List<Plans> m_PlansList;

    /************************************************************************
     * Purpose:         1 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public PlansListAdapter(List<Plans> PlanList)
    {
        this.m_PlansList = PlanList;
    }

    /************************************************************************
     * Purpose:         View Holder
     * Precondition:    .
     * Postcondition:   Inflate the layout to Recycler List View
     *                  Using the menu1_planlist_item.XML File
     ************************************************************************/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu1_planlist_item, parent, false);
        return new ViewHolder(view);
    }

    /************************************************************************
     * Purpose:         Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.titleText.setText(m_PlansList.get(position).getTitle());
        holder.descriptionText.setText(m_PlansList.get(position).getDescription());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public int getItemCount()
    {
        return m_PlansList.size();
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        View m_view;
        public TextView titleText;
        public TextView descriptionText;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            m_view = itemView;

            titleText = (TextView) m_view.findViewById(R.id.planlist_title_text);
            descriptionText = (TextView) m_view.findViewById(R.id.planlist_description_text);
        }
    }
}
