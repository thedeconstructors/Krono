package com.deconstructors.krono.helpers;

import com.deconstructors.krono.activities.plans.Menu1_Plans;
import com.deconstructors.krono.activities.plans.Plans;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.plans.PlansDetails;

import java.util.ArrayList;
import java.util.List;

/************************************************************************
 * Class:           PlansListAdapter
 * Purpose:         To customize list view layout
 ************************************************************************/
public class PlansListAdapter extends RecyclerView.Adapter<PlansListAdapter.ViewHolder> implements Filterable
{
    private List<Plans> _PlansList;
    private List<Plans> _PlansFilterList; // For Search Filter

    /************************************************************************
     * Purpose:         1 Arg Constructor
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    public PlansListAdapter(List<Plans> PlanList)
    {
        this._PlansList = PlanList;
        this._PlansFilterList = PlanList;
    }

    /************************************************************************
     * Purpose:         resetFilterList
     * Precondition:    onQueryTextChange
     * Postcondition:   Reset _PlansFilterList after search
     ************************************************************************/
    public void resetFilterList()
    {
        this._PlansFilterList = new ArrayList<>(_PlansList);
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
        holder._titleText.setText(_PlansList.get(position).getTitle());
        holder._descriptionText.setText(_PlansList.get(position).getDescription());
        holder._startTimeText.setText(_PlansList.get(position).getStartTime());
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public int getItemCount()
    {
        return _PlansList == null ? 0 : _PlansList.size();
    }

    @Override
    public Filter getFilter() {
        return _PlansListFilter;
    }

    /************************************************************************
     * Purpose:         List Item Size Getter
     * Precondition:    .
     * Postcondition:   Archive the element from the single list item
     ************************************************************************/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private View _view;
        private TextView _titleText;
        private TextView _descriptionText;
        private TextView _startTimeText;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            _view = itemView;

            _titleText = (TextView) _view.findViewById(R.id.planlist_title_text);
            _descriptionText = (TextView) _view.findViewById(R.id.planlist_description_text);
            _startTimeText = (TextView) _view.findViewById(R.id.planlist_starttime_text);

            _view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            ViewPlanDetails();
        }

        public void ViewPlanDetails()
        {
            Intent intent = new Intent(_view.getContext(), PlansDetails.class);
            intent.putExtra("plan_name", _PlansList.get(getAdapterPosition()).getPlanId());
            ((Menu1_Plans)_view.getContext()).startActivityForResult(intent, 0);
        }
    }

    /************************************************************************
     * Purpose:         Private Filter Class Override
     * Precondition:    .
     * Postcondition:   This filter class allows "performFiltering"
     *                  automatically executed in the background thread.
     *                  In this way, our app won't freeze even if the logic
     *                  is complicated, and if results are huge.
     *                  The result will automatically published to
     *                  "publishResults" in the end.
     ************************************************************************/
    private Filter _PlansListFilter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            List<Plans> filteredList = new ArrayList<>();
            String filterPattern = constraint.toString().toLowerCase().trim();

            if (constraint == null || constraint.length() == 0)
            {
                filteredList = new ArrayList<>(_PlansFilterList);
            }
            else
            {
                for (Plans plan : _PlansFilterList)
                {
                    if (plan.getTitle().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(plan);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            _PlansFilterList.clear();
            _PlansFilterList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
