# Инструкция по использованию Firebase Cloud Functions

## Обзор

Firebase Cloud Functions позволяют автоматически загружать данные в Firestore на сервере, что обеспечивает безопасность и не требует хранения секретных ключей в приложении.

## Преимущества

- ✅ **Безопасность** - секретные ключи хранятся только на сервере
- ✅ **Автоматизация** - можно настроить автоматическое обновление по расписанию
- ✅ **Масштабируемость** - Firebase автоматически масштабирует функции
- ✅ **Надежность** - функции выполняются в изолированной среде

## Установка и настройка

### 1. Установите Firebase CLI

```bash
npm install -g firebase-tools
```

### 2. Войдите в Firebase

```bash
firebase login
```

### 3. Инициализируйте проект

```bash
firebase init functions
```

Выберите:
- Использовать существующий проект: `egog-771fc`
- Язык: JavaScript
- ESLint: Да (опционально)

### 4. Установите зависимости

```bash
cd functions
npm install
```

### 5. Установите секретный ключ

Для защиты функций установите секретный ключ:

```bash
firebase functions:config:set app.secret_key="YOUR_SECRET_KEY_HERE"
```

**⚠️ ВАЖНО:** Используйте сложный случайный ключ! Например:
```bash
# Генерация случайного ключа (Linux/Mac)
openssl rand -base64 32

# Или используйте онлайн генератор
```

### 6. Разверните функции

```bash
firebase deploy --only functions
```

## Использование функций

### Функция: uploadQuestions

Загружает вопросы в Firestore по HTTP запросу.

**Endpoint:**
```
POST https://YOUR_REGION-egog-771fc.cloudfunctions.net/uploadQuestions
```

**Headers:**
```
Authorization: Bearer YOUR_SECRET_KEY
Content-Type: application/json
```

**Body:**
```json
{
  "subjectCode": "math_ege_profil",
  "questions": [
    {
      "id": "4F5745",
      "guid": "00011EA767B997BD431096F9DE1CA7F1",
      "hint": "Впишите правильный ответ.",
      "codifier": ["7.3 Многоугольники"],
      "question": "Один из углов прямоугольной трапеции равен 64°...",
      "problem": "<table>...</table>",
      "img": [],
      "imgUrls": [],
      "audioUrls": [],
      "numberInGroup": "",
      "answerType": "Краткий ответ",
      "answer": "116"
    }
  ]
}
```

**Пример использования с Python:**

```python
import requests
import json

# URL вашей функции (после деплоя)
FUNCTION_URL = "https://YOUR_REGION-egog-771fc.cloudfunctions.net/uploadQuestions"
SECRET_KEY = "YOUR_SECRET_KEY"

# Загрузите вопросы из JSON файла
with open('ege_questions.json', 'r', encoding='utf-8') as f:
    questions = json.load(f)

# Отправьте запрос
response = requests.post(
    FUNCTION_URL,
    headers={
        "Authorization": f"Bearer {SECRET_KEY}",
        "Content-Type": "application/json"
    },
    json={
        "subjectCode": "math_ege_profil",
        "questions": questions
    }
)

print(response.json())
```

**Пример использования с curl:**

```bash
curl -X POST \
  https://YOUR_REGION-egog-771fc.cloudfunctions.net/uploadQuestions \
  -H "Authorization: Bearer YOUR_SECRET_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "subjectCode": "math_ege_profil",
    "questions": [...]
  }'
```

### Функция: getQuestionsStats

Получает статистику по количеству вопросов (публичный endpoint).

**Endpoint:**
```
GET https://YOUR_REGION-egog-771fc.cloudfunctions.net/getQuestionsStats
```

**Пример ответа:**
```json
{
  "success": true,
  "stats": {
    "math_ege_profil": 150,
    "math_ege_base": 120,
    "math_oge": 200,
    "eng_ege": 100,
    "eng_oge": 150,
    "total": 720
  },
  "timestamp": "2025-11-10T15:30:00.000Z"
}
```

### Функция: scheduledUpdate

Автоматически запускается каждый день в 3:00 UTC. Можно настроить для автоматического парсинга и загрузки вопросов.

## Интеграция с парсером

Для интеграции с парсером вопросов можно:

1. **Вариант 1:** Вызывать парсер локально и отправлять данные в Cloud Function
2. **Вариант 2:** Интегрировать парсер в Cloud Function (если парсер на Node.js)
3. **Вариант 3:** Использовать Cloud Function для вызова внешнего API парсера

**Пример интеграции парсера в Cloud Function:**

```javascript
// В functions/index.js
const { parseQuestions } = require('./parser'); // Ваш парсер

exports.parseAndUpload = functions.https.onRequest(async (req, res) => {
  // ... проверка авторизации ...
  
  const { subjectCode, examType } = req.body;
  
  // Парсим вопросы
  const questions = await parseQuestions(examType, subjectCode);
  
  // Загружаем в Firestore
  // ... код загрузки ...
});
```

## Обновление скрипта загрузки

Обновите `scripts/upload_to_firestore.py` для использования Cloud Functions:

```python
import requests
import json

def upload_via_cloud_function(json_path: str, subject_code: str):
    """Загружает вопросы через Cloud Function"""
    
    FUNCTION_URL = "https://YOUR_REGION-egog-771fc.cloudfunctions.net/uploadQuestions"
    SECRET_KEY = "YOUR_SECRET_KEY"  # Храните в переменных окружения!
    
    # Загрузите вопросы
    with open(json_path, 'r', encoding='utf-8') as f:
        questions = json.load(f)
    
    # Отправьте запрос
    response = requests.post(
        FUNCTION_URL,
        headers={
            "Authorization": f"Bearer {SECRET_KEY}",
            "Content-Type": "application/json"
        },
        json={
            "subjectCode": subject_code,
            "questions": questions
        }
    )
    
    if response.status_code == 200:
        print(f"✅ {response.json()['message']}")
    else:
        print(f"❌ Ошибка: {response.json()}")
```

## Безопасность

### 1. Секретный ключ

- ✅ Храните секретный ключ в Firebase Functions Config
- ✅ Не коммитьте ключ в Git
- ✅ Используйте сложный случайный ключ
- ✅ Регулярно обновляйте ключ

### 2. Правила Firestore

Правила Firestore настроены так, что:
- ✅ Чтение разрешено всем (для приложения)
- ✅ Запись запрещена через клиент (только через Cloud Functions)

### 3. CORS

Cloud Functions настроены с поддержкой CORS для веб-запросов.

## Мониторинг и логи

### Просмотр логов

```bash
firebase functions:log
```

### Просмотр в консоли

1. Откройте [Firebase Console](https://console.firebase.google.com/)
2. Выберите проект `egog-771fc`
3. Перейдите в **Functions**
4. Просмотрите логи и метрики

## Стоимость

Firebase Cloud Functions имеют бесплатный тариф:
- ✅ 2 миллиона вызовов в месяц бесплатно
- ✅ 400,000 GB-секунд в месяц бесплатно
- ✅ 200,000 CPU-секунд в месяц бесплатно

Для большинства случаев этого достаточно.

## Следующие шаги

1. ✅ Разверните Cloud Functions
2. ✅ Установите секретный ключ
3. ✅ Обновите скрипт загрузки для использования Cloud Functions
4. ✅ Протестируйте загрузку данных
5. ✅ Настройте автоматическое обновление (если нужно)

## Полезные команды

```bash
# Развернуть все функции
firebase deploy --only functions

# Развернуть конкретную функцию
firebase deploy --only functions:uploadQuestions

# Просмотр логов
firebase functions:log

# Просмотр конфигурации
firebase functions:config:get

# Удалить функцию
firebase functions:delete uploadQuestions
```

## Проблемы и решения

### Ошибка: "Function failed to deploy"

- Проверьте синтаксис JavaScript
- Убедитесь, что все зависимости установлены
- Проверьте логи: `firebase functions:log`

### Ошибка: "Unauthorized"

- Проверьте, что секретный ключ установлен: `firebase functions:config:get`
- Убедитесь, что ключ передается правильно в заголовке Authorization

### Ошибка: "CORS"

- Cloud Functions уже настроены с поддержкой CORS
- Если проблема сохраняется, проверьте настройки CORS в коде функции

