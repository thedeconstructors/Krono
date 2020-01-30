package com.deconstructors.krono;

public class Activity {

    private String _name;
    private String _description;
    private boolean _isPublic;
    private int _duration;

    public Activity(String name, String description, boolean isPublic, int duration){
        _name = name;
        _description = description;
        _isPublic = isPublic;
        _duration = duration;
    }


    //Getters and Setters
    public void SetName(String name) {
        _name = name;
    }

    public void SetDescription(String description) {
        _description = description;
    }

    public void SetIsPublic(boolean isPublic) {
        _isPublic = isPublic;
    }

    public void SetDuration(int duration) {
        _duration = duration;
    }

    public String GetName() {
        return _name;
    }

    public String GetDescription() {
        return _description;
    }

    public boolean GetIsPublic() {
        return _isPublic;
    }

    public int GetDuration() {
        return _duration;
    }
}