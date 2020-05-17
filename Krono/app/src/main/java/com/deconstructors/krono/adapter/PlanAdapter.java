package com.deconstructors.krono.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.module.Plan;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

/************************************************************************
 * Class:           PlansListAdapter
 * Purpose:         To customize list view layout
 ************************************************************************/
public class PlanAdapter extends FirestoreRecyclerAdapter<Plan, PlanAdapter.PlanHolder> implements Filterable
{
    private PlanClickListener ClickListener;
    private List<Plan> FilteredList;
    public boolean filtering = false;

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
        this.FilteredList = new ArrayList<>(getSnapshots());
    }

    public void clearFilteredList()
    {
        FilteredList = new ArrayList<>();
    }

    @Override
    public void onDataChanged()
    {
        super.onDataChanged();
        if (!filtering)
            this.FilteredList = new ArrayList<>(getSnapshots());
    }

    @Override
    public int getItemCount()
    {
        onDataChanged();
        if (filtering)
            return FilteredList.size();
        return super.getItemCount();
    }

    @NonNull
    @Override
    public Plan getItem(int position) {
        if (filtering)
            return FilteredList.get(position);
        return super.getItem(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String pattern = constraint.toString().toLowerCase();
                if(pattern.isEmpty()){
                    FilteredList = new ArrayList<>(getSnapshots());
                    filtering = false;
                } else {
                    FilteredList = new ArrayList<>(getSnapshots());
                    List<Plan> filteredList = new ArrayList<>();
                    filtering = true;
                    for(Plan plan: FilteredList){
                        if(plan.getTitle().toLowerCase().contains(pattern)) {
                            filteredList.add(plan);
                        }
                    }
                    FilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = FilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                FilteredList = (ArrayList<Plan>) results.values;
                notifyDataSetChanged();
            }
        };
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
        //holder.Title.setText(model.getTitle());
        holder.Title.setText(FilteredList.get(position).getTitle());
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

