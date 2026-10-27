const { setGlobalOptions } = require("firebase-functions/v2");
const { onDocumentUpdated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

setGlobalOptions({ maxInstances: 10 });

exports.sendQueueNotification = onDocumentUpdated(
  "patients/{patientId}",
  async (event) => {
    const before = event.data.before.data();
    const after = event.data.after.data();

    if (!before || !after) return;

    if (before.queueStatus === after.queueStatus) {
      return;
    }

    const fcmToken = after.fcmToken;

    if (!fcmToken) {
      console.log("FCM token not found.");
      return;
    }

    let title = "NextTurn";
    let body = "";

    switch (after.queueStatus) {
      case "Called":
        body = "It's your turn! Please proceed to the consultation room.";
        break;

      case "Serving":
        body = "Your consultation has started.";
        break;

      case "Completed":
        body = "Your consultation has been completed.";
        break;

      default:
        return;
    }

    const message = {
      token: fcmToken,
      notification: {
        title: title,
        body: body,
      },
    };

    try {
      const response = await admin.messaging().send(message);

      console.log("Notification sent:", response);
    } catch (error) {
      console.error(error);
    }
  },
);
