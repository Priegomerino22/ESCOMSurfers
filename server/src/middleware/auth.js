const admin = require('../firebase');

async function authenticateFirebaseUser(req, res, next) {
  try {
    const authHeader = req.headers.authorization || '';

    if (!authHeader.startsWith('Bearer ')) {
      return res.status(401).json({
        ok: false,
        message: 'Falta token de autorización. Usa Authorization: Bearer <ID_TOKEN>.'
      });
    }

    const idToken = authHeader.substring('Bearer '.length).trim();
    const decodedToken = await admin.auth().verifyIdToken(idToken);

    req.user = {
      uid: decodedToken.uid,
      email: decodedToken.email || '',
      name: decodedToken.name || ''
    };

    next();
  } catch (error) {
    console.error('Error verificando token:', error.message);
    return res.status(401).json({
      ok: false,
      message: 'Token inválido o expirado.'
    });
  }
}

module.exports = authenticateFirebaseUser;
