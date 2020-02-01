package com.deconstructors.firestoreinteract;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class IntegerCounter {
    int _count = 0;

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
}

public class FirestoreDB {

    private static FirebaseFirestore db;

    //sets database in focus
    public static void SetDB(FirebaseFirestore d)
    {
        FirestoreDB.db = d;
    }

    //interaction methods for database
    public static void GetUserInfo(String userid, final MapHandler handler)
    {
        //get all the documents from the 'users' collection
        DocumentReference user = db.collection("users").document(userid);
        OnCompleteListener<DocumentSnapshot> getDataListener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    handler.handle(doc.getData());
                }
            }
        };
        user.get().addOnCompleteListener(getDataListener);
    }

    public static void GetUserActivities(String userid, final ListHandler handler)
    {
        //get all the documents from the 'users' collection with userid
        Query user = db.collection("userstoactivities").whereArrayContains("userid",userid);
        OnCompleteListener<QuerySnapshot> getDataListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    QuerySnapshot q = task.getResult();
                    final int numOfActivities = q.size();
                    String[] ids = new String[numOfActivities];
                    int ids_index = 0;

                    //go through each record and save the id of the attached activity id
                    for (Iterator<QueryDocumentSnapshot> iter = q.iterator();iter.hasNext();iter.next())
                    {
                        ids[ids_index++] = (String)((QueryDocumentSnapshot)iter).get("activityid");
                    }

                    //for each activity, get the activity record and save it to a list for handling
                    final LinkedList<Map<String,Object>> results = new LinkedList<Map<String,Object>>();
                    final IntegerCounter resCount = new IntegerCounter(numOfActivities);
                    for (ids_index = 0; ids_index < numOfActivities; ++ids_index)
                    {
                        DocumentReference activity = db.collection("useractivities").document(ids[ids_index]);
                        OnCompleteListener<DocumentSnapshot> getActivityListener = new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    results.add(task.getResult().getData());
                                    resCount.Decrement();
                                }
                            }
                        };
                        activity.get().addOnCompleteListener(getActivityListener);
                    }
                    //block code while asynchronous calls are made
                    while(!resCount.Done()){}
                    //let consumer handle results
                    handler.handle(results);
                }
            }
        };
        user.get().addOnCompleteListener(getDataListener);
    }

    public static void GetUserPlans(String userid, final ListHandler handler)
    {
        //get all the documents from the 'users' collection with userid
        Query user = db.collection("userstoplans").whereArrayContains("userid",userid);
        OnCompleteListener<QuerySnapshot> getDataListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    QuerySnapshot q = task.getResult();
                    final int numOfPlans = q.size();
                    String[] ids = new String[numOfPlans];
                    int ids_index = 0;

                    //go through each record and save the id of the attached plan id
                    for (Iterator<QueryDocumentSnapshot> iter = q.iterator();iter.hasNext();iter.next())
                    {
                        ids[ids_index++] = (String)((QueryDocumentSnapshot)iter).get("planid");
                    }

                    //for each activity, get the activity record and save it to a list for handling
                    final LinkedList<Map<String,Object>> results = new LinkedList<Map<String,Object>>();
                    final IntegerCounter resCount = new IntegerCounter(numOfPlans);
                    for (ids_index = 0; ids_index < numOfPlans; ++ids_index)
                    {
                        DocumentReference activity = db.collection("userplans").document(ids[ids_index]);
                        OnCompleteListener<DocumentSnapshot> getActivityListener = new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    results.add(task.getResult().getData());
                                    resCount.Decrement();
                                }
                            }
                        };
                        activity.get().addOnCompleteListener(getActivityListener);
                    }
                    //block code while asynchronous calls are made
                    while(!resCount.Done()){}
                    //let consumer handle results
                    handler.handle(results);
                }
            }
        };
        user.get().addOnCompleteListener(getDataListener);
    }

    public static void GetPlan(String planid, final MapHandler handler)
    {
        //get all the documents from the 'users' collection
        DocumentReference user = db.collection("userplans").document(planid);
        OnCompleteListener<DocumentSnapshot> getDataListener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    handler.handle(doc.getData());
                }
            }
        };
        user.get().addOnCompleteListener(getDataListener);
    }

    public static void GetActivity(String activityid, final MapHandler handler)
    {
        //get all the documents from the 'users' collection
        DocumentReference user = db.collection("useractivities").document(activityid);
        OnCompleteListener<DocumentSnapshot> getDataListener = new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    handler.handle(doc.getData());
                }
            }
        };
        user.get().addOnCompleteListener(getDataListener);
    }
}
