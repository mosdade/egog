const functions = require('firebase-functions');
const admin = require('firebase-admin');
const axios = require('axios');

admin.initializeApp();

const db = admin.firestore();

/**
 * Cloud Function для загрузки вопросов в Firestore
 * Вызывается по HTTP запросу с секретным ключом
 * 
 * Использование:
 * POST https://YOUR_REGION-YOUR_PROJECT.cloudfunctions.net/uploadQuestions
 * Headers: { "Authorization": "Bearer YOUR_SECRET_KEY" }
 * Body: { "subjectCode": "math_ege_profil", "questions": [...] }
 */
exports.uploadQuestions = functions.https.onRequest(async (req, res) => {
  // CORS поддержка
  res.set('Access-Control-Allow-Origin', '*');
  res.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.status(204).send('');
    return;
  }

  // Проверка авторизации через секретный ключ
  const secretKey = functions.config().app?.secret_key;
  const providedKey = req.headers.authorization?.replace('Bearer ', '');

  if (!secretKey || providedKey !== secretKey) {
    res.status(401).json({ error: 'Unauthorized. Invalid secret key.' });
    return;
  }

  try {
    const { subjectCode, questions } = req.body;

    if (!subjectCode || !questions || !Array.isArray(questions)) {
      res.status(400).json({ 
        error: 'Invalid request. Required: subjectCode (string), questions (array)' 
      });
      return;
    }

    // Валидация кодов предметов
    const validSubjects = [
      'math_ege_profil',
      'math_ege_base',
      'math_oge',
      'eng_ege',
      'eng_oge'
    ];

    if (!validSubjects.includes(subjectCode)) {
      res.status(400).json({ 
        error: `Invalid subjectCode. Valid codes: ${validSubjects.join(', ')}` 
      });
      return;
    }

    console.log(`Starting upload of ${questions.length} questions for ${subjectCode}`);

    // Загружаем вопросы батчами
    const batchSize = 500;
    let uploaded = 0;
    let errors = 0;

    for (let i = 0; i < questions.length; i += batchSize) {
      const batch = db.batch();
      const batchQuestions = questions.slice(i, i + batchSize);

      for (const question of batchQuestions) {
        try {
          // Подготавливаем вопрос для Firestore
          const firestoreQuestion = {
            id: question.id || '',
            guid: question.guid || null,
            hint: question.hint || '',
            codifier: question.codifier || [],
            question: question.question || '',
            problem: question.problem || '',
            img: question.img || [],
            imgUrls: question.imgUrls || [],
            audioUrls: question.audioUrls || [],
            numberInGroup: question.numberInGroup || '',
            answerType: question.answerType || '',
            answer: question.answer || '',
            subjectCode: subjectCode, // Обязательное поле!
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
          };

          // Используем id вопроса как document ID
          const docId = question.id || `question_${i}_${Date.now()}`;
          const docRef = db.collection('questions').doc(docId);
          
          batch.set(docRef, firestoreQuestion, { merge: true });
        } catch (error) {
          console.error(`Error preparing question ${question.id}:`, error);
          errors++;
        }
      }

      try {
        await batch.commit();
        uploaded += batchQuestions.length;
        console.log(`Uploaded batch: ${uploaded}/${questions.length}`);
      } catch (error) {
        console.error(`Error committing batch:`, error);
        errors += batchQuestions.length;
      }
    }

    res.status(200).json({
      success: true,
      message: `Uploaded ${uploaded} questions for ${subjectCode}`,
      uploaded,
      errors,
      total: questions.length
    });

  } catch (error) {
    console.error('Error uploading questions:', error);
    res.status(500).json({ 
      error: 'Internal server error', 
      message: error.message 
    });
  }
});

/**
 * Cloud Function для запуска парсера вопросов
 * Вызывается по HTTP запросу с секретным ключом
 * 
 * Использование:
 * POST https://YOUR_REGION-YOUR_PROJECT.cloudfunctions.net/parseAndUpload
 * Headers: { "Authorization": "Bearer YOUR_SECRET_KEY" }
 * Body: { "subjectCode": "math_ege_profil", "examType": "ege" }
 */
exports.parseAndUpload = functions.https.onRequest(async (req, res) => {
  // CORS поддержка
  res.set('Access-Control-Allow-Origin', '*');
  res.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.status(204).send('');
    return;
  }

  // Проверка авторизации
  const secretKey = functions.config().app?.secret_key;
  const providedKey = req.headers.authorization?.replace('Bearer ', '');

  if (!secretKey || providedKey !== secretKey) {
    res.status(401).json({ error: 'Unauthorized. Invalid secret key.' });
    return;
  }

  try {
    const { subjectCode, examType } = req.body;

    if (!subjectCode || !examType) {
      res.status(400).json({ 
        error: 'Invalid request. Required: subjectCode, examType' 
      });
      return;
    }

    // Здесь можно интегрировать парсер вопросов
    // Например, вызвать внешний API или использовать библиотеку для парсинга
    // Пока возвращаем заглушку
    
    res.status(200).json({
      success: true,
      message: 'Parser integration needed. Use uploadQuestions endpoint with parsed data.',
      note: 'To integrate parser, add parsing logic here or call external parsing service.'
    });

  } catch (error) {
    console.error('Error in parseAndUpload:', error);
    res.status(500).json({ 
      error: 'Internal server error', 
      message: error.message 
    });
  }
});

/**
 * Cloud Function для автоматического обновления вопросов по расписанию
 * Запускается каждый день в 3:00 UTC
 */
exports.scheduledUpdate = functions.pubsub
  .schedule('0 3 * * *') // Каждый день в 3:00 UTC
  .timeZone('UTC')
  .onRun(async (context) => {
    console.log('Scheduled update started');

    try {
      // Здесь можно добавить логику автоматического парсинга и загрузки
      // Например, парсить вопросы с FIPI и загружать в Firestore
      
      console.log('Scheduled update completed');
      return null;
    } catch (error) {
      console.error('Error in scheduled update:', error);
      throw error;
    }
  });

/**
 * Cloud Function для получения статистики вопросов
 * Публичный endpoint (без секретного ключа)
 */
exports.getQuestionsStats = functions.https.onRequest(async (req, res) => {
  // CORS поддержка
  res.set('Access-Control-Allow-Origin', '*');

  try {
    const stats = {};

    const subjects = [
      'math_ege_profil',
      'math_ege_base',
      'math_oge',
      'eng_ege',
      'eng_oge'
    ];

    for (const subjectCode of subjects) {
      const snapshot = await db.collection('questions')
        .where('subjectCode', '==', subjectCode)
        .count()
        .get();
      
      stats[subjectCode] = snapshot.data().count || 0;
    }

    const totalSnapshot = await db.collection('questions')
      .count()
      .get();
    
    stats.total = totalSnapshot.data().count || 0;

    res.status(200).json({
      success: true,
      stats,
      timestamp: new Date().toISOString()
    });

  } catch (error) {
    console.error('Error getting stats:', error);
    res.status(500).json({ 
      error: 'Internal server error', 
      message: error.message 
    });
  }
});

