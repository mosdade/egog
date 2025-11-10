# Бесплатные альтернативы для загрузки данных

## Вариант 1: Прямой доступ к Firestore (рекомендуется) ✅

**Работает на бесплатном плане Spark!**

Используйте скрипт `scripts/upload_to_firestore.py` с `serviceAccountKey.json`:

```bash
# Установите зависимости
pip install firebase-admin

# Загрузите данные
python scripts/upload_to_firestore.py \
  --json path/to/questions.json \
  --subject math_ege_profil \
  --credentials serviceAccountKey.json
```

**Преимущества:**
- ✅ Работает на бесплатном плане Spark
- ✅ Не требует обновления плана
- ✅ Бесплатно
- ✅ Безопасно (ключ хранится локально)

**Недостатки:**
- ⚠️ Нужно запускать вручную или через планировщик задач
- ⚠️ Ключ хранится локально (не коммитьте в Git!)

## Вариант 2: GitHub Actions (бесплатно) 🆓

Автоматическая загрузка при коммите в Git:

1. **Создайте файл `.github/workflows/upload-questions.yml`:**

```yaml
name: Upload Questions to Firestore

on:
  push:
    branches: [ main ]
    paths:
      - 'data/questions/**'
  workflow_dispatch:

jobs:
  upload:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.11'
      
      - name: Install dependencies
        run: |
          pip install firebase-admin
      
      - name: Upload questions
        env:
          SERVICE_ACCOUNT_KEY: ${{ secrets.SERVICE_ACCOUNT_KEY }}
        run: |
          echo "$SERVICE_ACCOUNT_KEY" > serviceAccountKey.json
          python scripts/upload_to_firestore.py \
            --json data/questions/ege_questions.json \
            --subject math_ege_profil \
            --credentials serviceAccountKey.json
```

2. **Добавьте секрет в GitHub:**
   - Settings → Secrets → Actions
   - Добавьте `SERVICE_ACCOUNT_KEY` с содержимым `serviceAccountKey.json`

**Преимущества:**
- ✅ Полностью бесплатно
- ✅ Автоматическая загрузка при коммите
- ✅ Не требует локального запуска

## Вариант 3: Vercel Serverless Functions (бесплатно) 🆓

Создайте серверную функцию на Vercel:

1. **Создайте файл `api/upload-questions.js`:**

```javascript
const admin = require('firebase-admin');

// Инициализация Firebase Admin
const serviceAccount = JSON.parse(process.env.SERVICE_ACCOUNT_KEY);
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

export default async function handler(req, res) {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  // Проверка авторизации
  const secretKey = process.env.SECRET_KEY;
  const providedKey = req.headers.authorization?.replace('Bearer ', '');
  
  if (providedKey !== secretKey) {
    return res.status(401).json({ error: 'Unauthorized' });
  }

  // Загрузка данных в Firestore
  // ... код загрузки ...
}
```

2. **Добавьте переменные окружения в Vercel:**
   - `SERVICE_ACCOUNT_KEY` - содержимое serviceAccountKey.json
   - `SECRET_KEY` - секретный ключ

**Преимущества:**
- ✅ Бесплатный тариф: 100GB-часов в месяц
- ✅ Автоматическое масштабирование
- ✅ Простое развертывание

## Вариант 4: Railway (бесплатно) 🆓

Разверните простой Node.js сервер:

1. **Создайте файл `server.js`:**

```javascript
const express = require('express');
const admin = require('firebase-admin');
const app = express();

// Инициализация Firebase
admin.initializeApp({
  credential: admin.credential.cert(
    JSON.parse(process.env.SERVICE_ACCOUNT_KEY)
  )
});

app.post('/upload', async (req, res) => {
  // Проверка авторизации и загрузка данных
  // ...
});

app.listen(process.env.PORT || 3000);
```

2. **Разверните на Railway:**
   - Подключите GitHub репозиторий
   - Добавьте переменные окружения
   - Railway автоматически развернет

**Преимущества:**
- ✅ Бесплатный тариф: $5 кредитов в месяц
- ✅ Простое развертывание
- ✅ Автоматическое масштабирование

## Вариант 5: Render (бесплатно) 🆓

Аналогично Railway, но с другими лимитами:

- Бесплатный тариф: 750 часов в месяц
- Автоматическое развертывание из Git
- Простая настройка

## Рекомендация

**Для начала:** Используйте **Вариант 1** (прямой доступ к Firestore) - это самый простой и быстрый способ.

**Для автоматизации:** Используйте **Вариант 2** (GitHub Actions) - полностью бесплатно и автоматически.

## Сравнение вариантов

| Вариант | Стоимость | Автоматизация | Сложность |
|---------|-----------|---------------|-----------|
| Прямой доступ | Бесплатно | Ручной запуск | ⭐ Легко |
| GitHub Actions | Бесплатно | Автоматически | ⭐⭐ Средне |
| Vercel | Бесплатно | По запросу | ⭐⭐ Средне |
| Railway | Бесплатно | Автоматически | ⭐⭐⭐ Сложно |
| Render | Бесплатно | Автоматически | ⭐⭐⭐ Сложно |

## Следующие шаги

1. **Для быстрого старта:** Используйте `scripts/upload_to_firestore.py`
2. **Для автоматизации:** Настройте GitHub Actions
3. **Для продакшена:** Рассмотрите Vercel или Railway

