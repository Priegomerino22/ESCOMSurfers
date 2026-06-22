const express = require('express');
const admin = require('../firebase');
const authenticateFirebaseUser = require('../middleware/auth');

const router = express.Router();
const db = admin.firestore();

const VALID_DIFFICULTIES = ['FACIL', 'MEDIO', 'DIFICIL', 'EXTRAORDINARIO'];

function toInt(value) {
  const number = Number(value);

  if (!Number.isFinite(number)) {
    return null;
  }

  return Math.floor(number);
}

function validateScorePayload(body) {
  const score = toInt(body.score);
  const credits = toInt(body.credits);
  const level = toInt(body.level);
  const difficulty = String(body.difficulty || '').trim().toUpperCase();

  if (score === null || score < 0) {
    return { ok: false, message: 'El score debe ser un número mayor o igual a 0.' };
  }

  if (credits === null || credits < 0) {
    return { ok: false, message: 'Los créditos deben ser un número mayor o igual a 0.' };
  }

  if (level === null || level < 1 || level > 99) {
    return { ok: false, message: 'El nivel debe estar entre 1 y 99.' };
  }

  if (!VALID_DIFFICULTIES.includes(difficulty)) {
    return { ok: false, message: 'Dificultad inválida.' };
  }

  const maxReasonableScore = level * 45000;

  if (score > maxReasonableScore) {
    return {
      ok: false,
      message: 'El score parece demasiado alto para el nivel alcanzado.'
    };
  }

  return {
    ok: true,
    data: {
      score,
      credits,
      level,
      difficulty
    }
  };
}

async function getUsername(uid, fallbackName, fallbackEmail) {
  try {
    const userDoc = await db.collection('users').doc(uid).get();

    if (userDoc.exists) {
      const data = userDoc.data();

      if (data.username) {
        return data.username;
      }
    }
  } catch (error) {
    console.warn('No se pudo leer perfil del usuario:', error.message);
  }

  if (fallbackName) {
    return fallbackName;
  }

  if (fallbackEmail && fallbackEmail.includes('@')) {
    return fallbackEmail.split('@')[0];
  }

  return 'Jugador ESCOM';
}

router.post('/scores', authenticateFirebaseUser, async (req, res) => {
  try {
    const validation = validateScorePayload(req.body);

    if (!validation.ok) {
      return res.status(400).json({
        ok: false,
        message: validation.message
      });
    }

    const { score, credits, level, difficulty } = validation.data;
    const uid = req.user.uid;
    const email = req.user.email;
    const playerName = await getUsername(uid, req.user.name, email);

    const scoreData = {
      uid,
      playerName,
      email,
      score,
      credits,
      level,
      difficulty,
      source: 'server',
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    };

    const docRef = await db.collection('scores').add(scoreData);

    await db.collection('users').doc(uid).set({
      uid,
      username: playerName,
      email,
      lastScore: score,
      lastDifficulty: difficulty,
      lastPlayedAt: admin.firestore.FieldValue.serverTimestamp()
    }, { merge: true });

    return res.status(201).json({
      ok: true,
      message: 'Puntaje guardado correctamente.',
      id: docRef.id,
      data: {
        score,
        credits,
        level,
        difficulty,
        playerName
      }
    });
  } catch (error) {
    console.error('Error guardando score:', error);
    return res.status(500).json({
      ok: false,
      message: 'Error interno al guardar puntaje.'
    });
  }
});

router.get('/ranking', authenticateFirebaseUser, async (req, res) => {
  try {
    const difficulty = String(req.query.difficulty || '').trim().toUpperCase();
    const limit = Math.min(toInt(req.query.limit) || 10, 50);

    let snapshot;

    if (difficulty && VALID_DIFFICULTIES.includes(difficulty)) {
      snapshot = await db.collection('scores')
        .where('difficulty', '==', difficulty)
        .limit(200)
        .get();
    } else {
      snapshot = await db.collection('scores')
        .limit(300)
        .get();
    }

    const ranking = snapshot.docs
      .map((doc) => ({ id: doc.id, ...doc.data() }))
      .sort((a, b) => (b.score || 0) - (a.score || 0))
      .slice(0, limit)
      .map((item, index) => ({
        position: index + 1,
        id: item.id,
        playerName: item.playerName || 'Jugador ESCOM',
        score: item.score || 0,
        credits: item.credits || 0,
        level: item.level || 1,
        difficulty: item.difficulty || 'MEDIO'
      }));

    return res.json({
      ok: true,
      difficulty: difficulty || 'TODAS',
      count: ranking.length,
      ranking
    });
  } catch (error) {
    console.error('Error consultando ranking:', error);
    return res.status(500).json({
      ok: false,
      message: 'Error interno al consultar ranking.'
    });
  }
});

router.get('/scores/me', authenticateFirebaseUser, async (req, res) => {
  try {
    const snapshot = await db.collection('scores')
      .where('uid', '==', req.user.uid)
      .limit(100)
      .get();

    const scores = snapshot.docs
      .map((doc) => ({ id: doc.id, ...doc.data() }))
      .sort((a, b) => (b.score || 0) - (a.score || 0))
      .slice(0, 20)
      .map((item) => ({
        id: item.id,
        playerName: item.playerName || 'Jugador ESCOM',
        score: item.score || 0,
        credits: item.credits || 0,
        level: item.level || 1,
        difficulty: item.difficulty || 'MEDIO'
      }));

    return res.json({
      ok: true,
      count: scores.length,
      scores
    });
  } catch (error) {
    console.error('Error consultando scores del usuario:', error);
    return res.status(500).json({
      ok: false,
      message: 'Error interno al consultar tus puntajes.'
    });
  }
});

module.exports = router;
