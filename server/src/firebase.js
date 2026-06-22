const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

function initializeFirebase() {
  if (admin.apps.length > 0) {
    return admin;
  }

  const localServiceAccountPath = path.join(__dirname, '..', 'serviceAccountKey.json');

  if (process.env.GOOGLE_APPLICATION_CREDENTIALS) {
    admin.initializeApp({
      credential: admin.credential.applicationDefault()
    });
  } else if (fs.existsSync(localServiceAccountPath)) {
    const serviceAccount = require(localServiceAccountPath);
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount)
    });
  } else {
    admin.initializeApp({
      credential: admin.credential.applicationDefault()
    });
  }

  return admin;
}

module.exports = initializeFirebase();
