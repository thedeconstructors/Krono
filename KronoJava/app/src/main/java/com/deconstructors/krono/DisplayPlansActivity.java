package com.deconstructors.krono;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Map;

interface MyCallback {
    String userName = "";
    void onCallback(Map<String,Object> record);
    void onCallback();
}

public class DisplayPlansActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayplans);
        final TextView txtName = (TextView)findViewById(R.id.lblPersonName);
        final String user_name = getIntent().getStringExtra(MainActivity.USER_NAME);
        txtName.setText(user_name);

        //define what happens when we get the data
        OnCompleteListener<QuerySnapshot> getDataListener = new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    //docs is the collection of documents
                    QuerySnapshot docs = task.getResult();
                    Boolean found = false;
                    for (QueryDocumentSnapshot document : docs) {
                        // this is what the API said to do
                        //Log.d("debug", document.getId() + " => " + document.getData());
                        if ((document.getData().get("first_name")).toString().equals(user_name))
                        {
                            //found user
                            ShowUserData(document.getData());
                            found = true;
                            break;
                        }
                    }
                    if (!found){
                        //User not found
                        NoUserFound();
                    }
                }
            }
        };
        //get all the documents from the 'users' collection
        CollectionReference users = MainActivity.db.collection("users");

        //when users.get() finishes (and once the success is confirmed),
        //we will go through the documents
        users.get().addOnCompleteListener(getDataListener);
    }

    public void NoUserFound()
    {
        TextView nouser = new TextView(this);
        LinearLayout lay = findViewById(R.id.layoutPlans);
        nouser.setText("User not found");
        lay.addView(nouser);
    }

    public void ShowUserData(Map<String,Object> data)
    {
        TextView firstName = new TextView(this);
        TextView lastName = new TextView(this);
        TextView age = new TextView(this);

        firstName.setText("First Name: " + data.get("first_name").toString());
        lastName.setText("Last Name: " + data.get("last_name").toString());
        age.setText("Age: " + data.get("age").toString());

        LinearLayout lay = findViewById(R.id.layoutPlans);
        lay.addView(firstName);
        lay.addView(lastName);
        lay.addView(age);
    }
}
