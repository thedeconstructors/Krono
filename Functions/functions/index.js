// Include Modules
const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Firebase Admin Initialize
admin.initializeApp();

// Global Variables
const database = admin.firestore();

/************************************************************************
 * Purpose:         Activity Page - Delete Plan
 * Type:            HTTPS Callable function
 * Precondition:    Call from the app directly
 *                  snap = Plan ID
 * Postcondition:   Go to Activity Details or Plan Edit page
 ************************************************************************/
exports.deletePlan = functions.https.onCall((snap, context) => 
{
    const planID = snap.planID;
    console.log("deletePlan Debug: ");

    return database
        .collection('plans')
        .doc(planID)
        .delete()
        .catch(error => 
        {
            console.log(error);
            return false; 
        });
});

/************************************************************************
 * Purpose:         Activity Page - onDelete Plan
 * Type:            Trigger function
 * Precondition:    A plan document is deleted
 *                  snap = Deleted document data
 * Postcondition:   Go to Activity Details or Plan Edit page
 ************************************************************************/
exports.deleteActivities = functions.firestore
    .document('plans/{doc-id}')
    .onDelete((snap, context) => 
{
    const planID = snap.planID;

    const activityRef = database
        .collection('activities')
        .where('planIDs', 'array-contains', planID);

    // Promise.all() returns a single Promise that resolves when
    // all of the promises in the iterable argument have resolved
    // or when the iterable argument contains no promises.
    var promises = [];
    
    return activityRef
        .get()
        .then(querySnapshot => 
        {
            querySnapshot.forEach(docSnapshot => 
            {
                promises.push(docSnapshot.ref.delete());
            });

            return Promise.all(promises);
        })
        .catch(error => 
        {
            console.log(error);
            return false; 
        });
});