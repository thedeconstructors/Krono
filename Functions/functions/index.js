// Include Modules
const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Firebase Admin Initialize
admin.initializeApp();

// Global Variables
const database = admin.firestore();

/************************************************************************
 * Purpose:         Delete Activities on Plan Deletion
 * Type:            Trigger function
 * Precondition:    A plan document is deleted
 *                  snap = Deleted document data
 * Postcondition:   Delete Activities
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

/************************************************************************
 * Purpose:         Delete FriendsList on User Deletion
 * Type:            Trigger function
 * Precondition:    A user document is deleted
 *                  snap = Deleted document data
 * Postcondition:   Delete FriendsList
 ************************************************************************/
exports.deleteUser = functions.firestore
    .document('user/{user-id}')
    .onDelete((snap, context) => 
{
    const userID = snap.ownerIDs;

    const friendRef = database
        .collection('user')
        .where('ownerIDs.' + userID, '==', userID);

    var promises = [];
    
    return friendRef
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

/************************************************************************
 * Purpose:         Delete User Auth on User Deletion
 * Type:            Trigger function
 * Precondition:    A user document is deleted
 *                  snap = Deleted document data
 * Postcondition:   Delete User Auth
 ************************************************************************/
exports.deleteAuth = functions.firestore
    .document('user/{user-id}')
    .onDelete((snap, context) => 
{
    //
});