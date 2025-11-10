# Быстрый старт: Загрузка данных в Firestore

## Шаги для загрузки данных в Firebase Firestore

### 1. Включите Firestore API (5 минут)

1. Откройте: https://console.developers.google.com/apis/api/firestore.googleapis.com/overview?project=egog-771fc
2. Нажмите **"Enable"** (Включить)
3. Дождитесь активации (1-2 минуты)

### 2. Создайте базу данных Firestore (2 минуты)

1. Откройте: https://console.firebase.google.com/project/egog-771fc/firestore
2. Нажмите **"Создать базу данных"**
3. Выберите **"Режим тестирования"**
4. Выберите регион (например, `europe-west`)
5. Нажмите **"Включить"**

### 3. Настройте правила безопасности (1 минута)

В разделе **"Правила"** добавьте:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /questions/{document} {
      allow read: if true;
      allow write: if false;
    }
  }
}
```

Нажмите **"Опубликовать"**.

### 4. Получите serviceAccountKey.json (2 минуты)

1. Откройте: https://console.firebase.google.com/project/egog-771fc/settings/serviceaccounts/adminsdk
2. Нажмите **"Generate new private key"**
3. Сохраните файл как `serviceAccountKey.json` в корень проекта
4. **⚠️ ВАЖНО:** Не коммитьте этот файл в Git!

### 5. Установите зависимости Python (1 минута)

```bash
pip install firebase-admin
```

Или используйте файл requirements.txt:

```bash
pip install -r scripts/requirements.txt
```

### 6. Загрузите данные из парсера (5-10 минут)

Если у вас есть JSON файлы из парсера:

```bash
# Для ЕГЭ по математике (профиль)
python scripts/upload_to_firestore.py \
  --json path/to/ege_questions.json \
  --subject math_ege_profil

# Для ОГЭ по математике
python scripts/upload_to_firestore.py \
  --json path/to/oge_questions.json \
  --subject math_oge

# Для ЕГЭ по английскому языку
python scripts/upload_to_firestore.py \
  --json path/to/eng_ege_questions.json \
  --subject eng_ege
```

**Коды предметов:**
- `math_ege_profil` - Математика (профиль) ЕГЭ
- `math_ege_base` - Математика (база) ЕГЭ
- `math_oge` - Математика ОГЭ
- `eng_ege` - Английский язык ЕГЭ
- `eng_oge` - Английский язык ОГЭ

### 7. Проверьте загрузку (1 минута)

1. Откройте: https://console.firebase.google.com/project/egog-771fc/firestore
2. Убедитесь, что коллекция `questions` создана
3. Проверьте, что документы содержат поле `subjectCode`
4. Запустите приложение и проверьте загрузку вопросов

## Готово! 🎉

Теперь приложение будет загружать вопросы из Firebase Firestore!

## Проблемы?

См. подробные инструкции:
- [FIREBASE_UPLOAD_GUIDE.md](FIREBASE_UPLOAD_GUIDE.md) - полная инструкция по загрузке
- [FIREBASE_SETUP.md](FIREBASE_SETUP.md) - настройка Firebase
- [DATA_SETUP.md](DATA_SETUP.md) - альтернативные способы настройки данных

