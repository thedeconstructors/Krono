package com.deconstructors.krono.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.deconstructors.krono.R;
import com.deconstructors.krono.adapter.ActivityAdapter;
import com.deconstructors.krono.module.Activity;
import com.deconstructors.krono.module.Plan;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ActivityPage extends AppCompatActivity implements ActivityAdapter.ActivityClickListener, View.OnClickListener
{
    //static
    static public enum EditMode { OWNER, COLLAB, PUBLIC }
    // Logcat
    private static final String TAG = "ActivityPage";

    //activity results
    final int AR_COLLAB = 5;
    final String EXTRA_COLLAB = "COLLAB";

    // Data vars
    List<String> Collaborators;

    // XML Widgets
    private Toolbar Toolbar;
    private TextView ToolbarDescription;
    private RecyclerView RecyclerView;
    private FloatingActionButton FAB;
    private FloatingActionButton FAB_Collaborators;
    private ActivityPage_New ActivityPage_New;

    // Var
    private Plan Plan;
    private EditMode Editable;

    // Database
    private FirebaseFunctions DBFunctions;
    private FirebaseFirestore DBInstance;
    private Query ActivityQuery;
    private FirestoreRecyclerOptions<Activity> ActivityOptions;
    private ActivityAdapter ActivityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setToolbar();
        this.checkIntent();
        this.setDatabase();
        this.setContents();
    }

    private void saveCollaborators()
    {
        DocumentReference planDoc = DBInstance.collection(getString(R.string.collection_plans))
                .document(this.Plan.getPlanID());

        Map<String,Object> planData = new HashMap<>();

        planData.put("collaborators", Collaborators);

        planDoc.update(planData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    /************************************************************************
     * Purpose:         Set Toolbar & Inflate Toolbar Menu
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setToolbar()
    {
        this.Toolbar = findViewById(R.id.ActivityPage_Toolbar);
        this.ToolbarDescription = findViewById(R.id.ActivityPage_Description);
        this.setSupportActionBar(this.Toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);

        return true;
    }

    /************************************************************************
     * Purpose:         Parcelable Plan Interaction
     * Precondition:    .
     * Postcondition:   Get Plan Intent from MainActivity
     ************************************************************************/
    private void checkIntent()
    {
        if(getIntent().hasExtra(getString(R.string.intent_plans)) && getIntent().hasExtra(getString(R.string.intent_editable)))
        {
            this.Plan = getIntent().getParcelableExtra(getString(R.string.intent_plans));
            this.getSupportActionBar().setTitle(this.Plan.getTitle());
            this.ToolbarDescription.setText(this.Plan.getDescription());
            this.Editable = (EditMode)getIntent().getSerializableExtra(getString(R.string.intent_editable));
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Plan not found");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();

            finish();
        }
    }

    /************************************************************************
     * Purpose:         Database & Query Initialization
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setDatabase()
    {
        this.DBInstance = FirebaseFirestore.getInstance();
        this.DBFunctions = FirebaseFunctions.getInstance();

        this.ActivityQuery = this.DBInstance
                .collection(getString(R.string.collection_activities))
                .whereArrayContains(getString(R.string.collection_planIDs), this.Plan.getPlanID());
        this.ActivityOptions = new FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(this.ActivityQuery, Activity.class)
                .build();
        this.ActivityAdapter = new ActivityAdapter(this.ActivityOptions, this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (this.ActivityAdapter != null) { this.ActivityAdapter.startListening(); }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (this.ActivityAdapter != null) { this.ActivityAdapter.stopListening(); }
    }

    private void deletePlan()
    {
        if (Editable == EditMode.PUBLIC) {
            Toast.makeText(this, "This plan is not editable", Toast.LENGTH_SHORT).show();
            return;
        }
        // This should be done in Firebase Functions and not fully dependant on the user side
        // Not only because we changed the database, it's just the general practice we should've
        // Implemented before
        onDeletePlan(this.Plan.getPlanID())
                /*.addOnSuccessListener(new OnSuccessListener<String>()
                {
                    @Override
                    public void onSuccess(String s)
                    {
                        finish();
                    }
                })*/
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseFunctionsException) {
                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                            FirebaseFunctionsException.Code code = ffe.getCode();
                            Object details = ffe.getDetails();
                        }
                    }
                });

        finish();
    }

    private Task<String> onDeletePlan(String planID)
    {
        // Create the arguments to the callable function.
        Map<String, Object> snap = new HashMap<>();
        snap.put("planID", planID);
        snap.put("push", true);

        return this.DBFunctions
                .getHttpsCallable("deletePlan")
                .call(snap)
                .continueWith(new Continuation<HttpsCallableResult, String>()
                {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception
                    {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    /************************************************************************
     * Purpose:         XML Contents
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    private void setContents()
    {
        // RecyclerView
        this.RecyclerView = findViewById(R.id.ActivityPage_RecyclerView);
        this.RecyclerView.setHasFixedSize(true);
        this.RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.RecyclerView.setAdapter(this.ActivityAdapter);

        //buttons
        this.FAB = findViewById(R.id.ActivityPage_FAB);
        this.FAB_Collaborators = findViewById(R.id.ActivityPage_FAB_Collaborators);
        this.FAB_Collaborators.setOnClickListener(this);


        //Bottom Sheet and Collaborators
        if (Editable == EditMode.OWNER) {
            this.Collaborators = new ArrayList<>();
            List<String> planCollabs = this.Plan.getCollaborators();
            if (planCollabs != null)
            {
                this.Collaborators = new ArrayList<>(planCollabs);
            }
            this.ActivityPage_New = new ActivityPage_New(this, this.Plan);
            this.ActivityPage_New.setSheetState(BottomSheetBehavior.STATE_HIDDEN);
        }
        else if (Editable == EditMode.COLLAB)
        {
            this.ActivityPage_New = new ActivityPage_New(this, this.Plan);
            this.ActivityPage_New.setSheetState(BottomSheetBehavior.STATE_HIDDEN);
            FAB_Collaborators.setVisibility(View.GONE);
        }
        else
        {
            FAB.setVisibility(View.GONE);
            FAB_Collaborators.setVisibility(View.GONE);
            findViewById(R.id.ActivityPageNew_BottomSheet).setVisibility(View.GONE);
        }
    }

    /************************************************************************
     * Purpose:         Parcelable Plan & Activity Interaction
     * Precondition:    .
     * Postcondition:   Go to Activity Details or Plan Edit page
     ************************************************************************/
    @Override
    public void onActivitySelected(int position)
    {
        Intent intent = new Intent(ActivityPage.this, ActivityPage_Detail.class);
        intent.putExtra(getString(R.string.intent_activity), this.ActivityAdapter.getItem(position));
        intent.putExtra(getString(R.string.intent_editable),Editable);
        startActivity(intent);
    }

    private void editPlan()
    {
        if (Editable == EditMode.PUBLIC)
        {
            Toast.makeText(this,"This plan is not editable", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(ActivityPage.this, MainPage_Detail.class);
        intent.putExtra(getString(R.string.intent_plans), this.Plan);
        startActivity(intent);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ActivityPage_FAB_Collaborators:
            {
                Intent intent = new Intent(this, Friend_Select.class);
                intent.putExtra(EXTRA_COLLAB, new ArrayList<String>(Collaborators));
                startActivityForResult(intent, AR_COLLAB);
                break;
            }
        }
    }

    /************************************************************************
     * Purpose:         Toolbar Menu Selection
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            // Back Button Animation Override
            case android.R.id.home:
            {
                finish();
                break;
            }
            case R.id.activity_menu_sortBy:
            {
                //
                break;
            }
            case R.id.activity_menu_editPlan:
            {
                this.editPlan();
                break;
            }
            case R.id.activity_menu_deletePlan:
            {
                this.deletePlan();
                break;
            }
        }
        return true;
    }

    /************************************************************************
     * Purpose:         BottomSheet BackButton Overrides
     * Precondition:    .
     * Postcondition:   .
     ************************************************************************/
    @Override
    public void onBackPressed()
    {
        if ((Editable != EditMode.PUBLIC)
                && this.ActivityPage_New.getSheetState() != BottomSheetBehavior.STATE_HIDDEN)
        {
            this.ActivityPage_New.setSheetState(BottomSheetBehavior.STATE_HIDDEN);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (Editable != EditMode.PUBLIC)
            this.ActivityPage_New.ActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case AR_COLLAB:
                Collaborators = data.getStringArrayListExtra(EXTRA_COLLAB);
                saveCollaborators();
                break;
        }
    }
}
