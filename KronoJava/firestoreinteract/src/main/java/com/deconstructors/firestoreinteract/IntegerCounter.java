package com.deconstructors.firestoreinteract;

public class IntegerCounter {
    int _count = 0;
    boolean error = false;

    public IntegerCounter(int count)
    {
        if (count < 0)
        {
            throw new IndexOutOfBoundsException();
        }
        _count = count;
    }

    public boolean Done()
    {
        return (_count == 0);
    }

    public void Decrement()
    {
        if (Done())
        {
            throw new IndexOutOfBoundsException();
        }
        --_count;
    }

    public void SetError(boolean set) {error = set;}

    public boolean HasError() { return error; }
}
