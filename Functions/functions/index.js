const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firestore);

// Activity Page - Delete Plan
exports.deletePlan = functions.https.onCall((data, context) => 
{
    const planID = data.planID;
    console.log("addMessage Debug ");

    return admin.firestore()
                .collection('plans')
                .doc(planID)
                .delete();
});