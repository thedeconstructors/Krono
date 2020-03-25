package com.deconstructors.kronoui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.kronoui.R;
import com.deconstructors.kronoui.module.Plan;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/************************************************************************
 * Class:           PlansListAdapter
 * Purpose:         To customize list view layout
 ************************************************************************/
public class PlanAdapter extends FirestoreRecyclerAdapter<Plan, PlanAdapter.PlanHolder>
{
    private PlanClickListener ClickListener;

    /************************************************************************
     * Purpose:         2 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public PlanAdapter(@NonNull FirestoreRecyclerOptions<Plan> options,
                       PlanClickListener clickListener)
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
    public PlanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int res = R.layout.main_listitem;
        View view = LayoutInflater.from(parent.getContext()).inflate(res, parent, false);

        return new PlanHolder(view, this.ClickListener);
    }

    /************************************************************************
     * Purpose:         On Bind View Holder
     * Precondition:    .
     * Postcondition:   Assign Values from ViewHolder class to the Fields
     ************************************************************************/
    @Override
    protected void onBindViewHolder(@NonNull PlanHolder holder,
                                    int position,
                                    @NonNull Plan model)
    {
        holder.Title.setText(model.getTitle());
    }

    /************************************************************************
     * Purpose:         PlanHolder
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public class PlanHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView Title;
        PlanClickListener ClickListener;

        public PlanHolder(@NonNull View itemView, PlanClickListener clickListener)
        {
            super(itemView);

            this.Title = itemView.findViewById(R.id.planlist_title_text);
            this.ClickListener = clickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            this.ClickListener.onPlanSelected(getAdapterPosition());
        }
    }

    public interface PlanClickListener
    {
        void onPlanSelected(int position);
    }
}

