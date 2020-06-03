// Include Modules
const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Firebase Admin Initialize
admin.initializeApp();

// Global Variable
const database = admin.firestore();
const activities = 'activities';
const plans = 'plans';
const users = 'users';
const friends = 'friends';

/************************************************************************
 * Purpose:         Delete Activities on Plan Deletion
 * Type:            Trigger function
 * Precondition:    When a client deletes a plan document
 * Postcondition:   Delete activity documents in small batches
 ************************************************************************/
exports.deleteActivityOnDeletePlan = functions.firestore
    .document('plans/{planID}')
    .onDelete((snap, context) => 
{
    const planID = snap.id;
    const promises = [];

    const activityRef = database
        .collection(activities)
        .where('planIDs', 'array-contains', planID);
    
    return activityRef
        .get()
        .then(querySnapshot => 
        {
            querySnapshot.forEach(docSnapshot => 
            {
                promises.push(deleteActivityPromise(docSnapshot));
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
 * Purpose:         Add Friend Relationship to Friend's Document
 * Type:            Trigger function
 * Precondition:    When a client add a new friend
 * Postcondition:   Add friend to the map
 ************************************************************************/
exports.addFriend = functions.https.onCall((data, context) => 
{
    const friendID = data.friendID;
    const uid = context.auth.uid.toString();

    return database
        .collection(users)
        .doc(friendID)
        .set(
        {
            friends: 
            {
                [uid]: false
            }
        },
        {
            merge: true
        })
        .catch(error => 
        {
            console.log(error);
            return false; 
        });
});

/************************************************************************
 * Purpose:         Delete Friend Relationship on Plan Deletion
 * Type:            Trigger function
 * Precondition:    When a client deletes a plan document
 * Postcondition:   Delete activity documents in small batches
 ************************************************************************/
exports.deleteFriend = functions.https.onCall((data, context) => 
{
    const friendID = data.friendID;
    const uid = context.auth.uid.toString();
    //console.log(friendID);
    //console.log(uid);

    return database
        .collection(users)
        .doc(friendID)
        .set(
        {
            friends : 
            {
                [uid] : admin.firestore.FieldValue.delete()
            }
        },
        {
            merge : true
        })
        .catch(error => 
        {
            console.log(error);
            return false; 
        });
});

/************************************************************************
 * Purpose:         Verify Email For Facebook Accounts
 * Type:            Trigger function
 * Precondition:    .
 * Postcondition:   .
 ************************************************************************/
exports.verifyEmail = functions.https.onCall((data, context) => 
{
    const uid = context.auth.uid.toString();

    return admin
        .auth()
        .updateUser(uid, { emailVerified : true })
        .then(function(userRecord) 
        {
            console.log('Successfully updated user', userRecord.toJSON());
            return true;
        })
        .catch(function(error) 
        {
            console.log('Error updating user:', error);
            return false; 
        });
});

/************************************************************************
 * Purpose:         Accept Friend Request
 * Type:            Trigger function
 * Precondition:    .
 * Postcondition:   .
 ************************************************************************/
exports.acceptFriendRequest = functions.https.onCall((data, context) => 
{
    const uid = context.auth.uid.toString();
    const friendID = data.friendID;

    return database
        .collection(users)
        .doc(friendID)
        .set(
        {
            friends : 
            {
                [uid] : true
            }
        },
        {
            merge : true
        })
        .catch(error => 
        {
            console.log(error);
            return false; 
        });
});

/************************************************************************
 * Purpose:         Delete & Modify Related Documents on User Deletion
 * Type:            Trigger function
 * Precondition:    When a client deletes it's own user document
 * Postcondition:   Delete and modify documents in small batches
 ************************************************************************/
// exports.deleteActivityOnDeleteUser = functions.firestore
//     .document('users/{userID}')
//     .onDelete((snap, context) => 
// {
//     const userID = snap.id;
//     const promises = [];

//     const activityRef = database
//         .collection(activities)
//         .where('ownerID', '==', userID);
    
//     return activityRef
//         .get()
//         .then(querySnapshot => 
//         {
//             querySnapshot.forEach(docSnapshot => 
//             {
//                 promises.push(deleteActivityPromise(docSnapshot));
//             });

//             return Promise.all(promises);
//         })
//         .catch(error => 
//         {
//             console.log(error);
//             return false; 
//         });
// });

// exports.deletePlanOnDeleteUser = functions.firestore
//     .document('users/{userID}')
//     .onDelete((snap, context) => 
// {
//     const userID = snap.id;
//     const promises = [];

//     const planRef = database
//         .collection(plans)
//         .where('ownerID', '==', userID);
    
//     return planRef
//         .get()
//         .then(querySnapshot => 
//         {
//             querySnapshot.forEach(docSnapshot => 
//             {
//                 promises.push(deletePlanPromise(docSnapshot));
//             });

//             return Promise.all(promises);
//         })
//         .catch(error => 
//         {
//             console.log(error);
//             return false; 
//         });
// });

/************************************************************************
 * Purpose:         Delete an activity
 * Type:            Manual function
 * Precondition:    Pass a document reference
 * Postcondition:   Delete an activity with the document id
 ************************************************************************/
function deleteActivityPromise(document) 
{
    return database
        .collection(activities)
        .doc(document.id)
        .delete();   
}

/************************************************************************
 * Purpose:         Delete an plan
 * Type:            Manual function
 * Precondition:    Pass a document reference
 * Postcondition:   Delete an plan with the document id
 ************************************************************************/
function deletePlanPromise(document) 
{
    return database
        .collection(plans)
        .doc(document.id)
        .delete();   
}