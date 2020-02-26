package com.deconstructors.krono.helpers;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deconstructors.krono.R;
import com.deconstructors.krono.activities.friends.FriendHolder;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;


/************************************
 * CLASS
 *
 * Manages the list of friends used
 * wherever friends are viewed.
 *
 * Some functionality
 *      - Contains class for what viewholders
 *          are used by recyclerview
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder>
{
    List<FriendHolder> _friendsList;
    RecyclerView _recycler;

    //keep track of current selection
    // -1 means nothing is selected
    int _currentSelectedIndex = -1;

    /**************************
     * Dictates what happens when
     * this adapter is attached to
     * a recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        _recycler = recyclerView;
    }

    /***************************
     * Constructor for when adapter
     * is created with an outside list
     */
    public FriendsListAdapter(List<FriendHolder> friends)
    {
        _friendsList = friends;
    }

    public int GetSelectedIndex() { return _currentSelectedIndex; }

    public void ClearSelectedIndex() { _currentSelectedIndex = -1; }

    /********************************
     * Dictates what happens when a viewholder
     * is created, typically via a dataset
     * change and update.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu3_friendlist_item, parent, false);
        return new FriendsListAdapter.ViewHolder(view);
    }

    /********************************
     * Dictates what happens when a viewholder
     * is bound to recyclerview.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.firstName.setText(_friendsList.get(position).GetFriend().GetFirstName());
        holder.lastName.setText(_friendsList.get(position).GetFriend().GetLastName());
    }

    @Override
    public int getItemCount() {
        return _friendsList.size();
    }

    /********************************
     * CLASS
     *
     * holds view that displays friend
     * information.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener
    {
        View _item;
        TextView firstName;
        TextView lastName;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            _item = itemView;
            firstName = (TextView) _item.findViewById(R.id.friendlist_item_firstname);
            lastName = (TextView) _item.findViewById(R.id.friendlist_item_lastname);
            _item.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            FriendHolder item = _friendsList.get(getAdapterPosition());
            item.ToggleSelected();
            if (item.IsSelected())
            {
                if (_currentSelectedIndex != -1)
                {
                    //deselects FriendHolder in list
                    _friendsList.get(_currentSelectedIndex).Deselect();
                    //changes background of old selected view
                    _recycler.getChildAt(_currentSelectedIndex).setBackgroundColor(Color.WHITE);
                }
                //change current view's background
                _item.setBackgroundColor(Color.rgb(208,208,208));
                //set current selected index to this one
                _currentSelectedIndex = getAdapterPosition();
            }
            else
            {
                //change current view's background
                _item.setBackgroundColor(Color.WHITE);
                //unset current selected index
                _currentSelectedIndex = -1;
            }
        }
    }
}
