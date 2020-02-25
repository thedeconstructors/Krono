package com.deconstructors.krono.activities.friends;

/*********************************
 * This class is meant to separate
 * friend logic from GUI logic.
 */

public class FriendHolder
{
    private Friend _friend;
    private boolean _selected = false;

    public FriendHolder(Friend friend)
    {
        _friend = friend;
    }

    public Friend GetFriend()
    {
        return _friend;
    }

    public boolean IsSelected()
    {
        return _selected;
    }

    public void Select()
    {
        _selected = true;
    }

    public void Deselect()
    {
        _selected = false;
    }

    public void ToggleSelected()
    {
        _selected = !_selected;
    }
}
