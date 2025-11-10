# Настройка Firebase для проекта EGOG

## Шаги настройки

### 1. Создание проекта в Firebase

1. Перейдите на [Firebase Console](https://console.firebase.google.com/)
2. Нажмите "Добавить проект"
3. Введите название проекта (например, "EGOG")
4. Следуйте инструкциям для создания проекта

### 2. Добавление Android приложения

1. В Firebase Console выберите ваш проект
2. Нажмите на иконку Android
3. Введите:
   - **Package name**: `com.example.egog` (должен совпадать с `applicationId` в `app/build.gradle.kts`)
   - **App nickname** (опционально): EGOG
   - **Debug signing certificate SHA-1** (опционально, для тестирования)
4. Нажмите "Зарегистрировать приложение"

### 3. Загрузка google-services.json

1. Скачайте файл `google-services.json`
2. Поместите его в папку `app/` проекта (на том же уровне, что и `build.gradle.kts`)
3. Убедитесь, что файл добавлен в `.gitignore` (если не хотите коммитить его в репозиторий)

### 4. Включение Firestore API

**ВАЖНО:** Перед созданием базы данных нужно включить Firestore API!

1. Перейдите в [Google Cloud Console](https://console.cloud.google.com/)
2. Выберите ваш проект Firebase (egog-771fc)
3. Перейдите в раздел **APIs & Services** → **Library**
4. Найдите **Cloud Firestore API**
5. Нажмите **Enable** (Включить)
6. Дождитесь активации API (может занять несколько минут)

### 5. Настройка Firestore

1. В Firebase Console перейдите в раздел **Firestore Database**
2. Нажмите "Создать базу данных"
3. Выберите режим:
   - **Режим тестирования** (для разработки)
   - **Режим продакшена** (для продакшена, требует правила безопасности)
4. Выберите регион (например, `europe-west`)
5. Нажмите "Включить"

### 6. Структура данных в Firestore

Создайте коллекцию `questions` со следующими полями для каждого документа:

```
questions/
  └── {documentId}/
      ├── id: string (например, "4F5745")
      ├── guid: string (опционально)
      ├── hint: string
      ├── codifier: array<string>
      ├── question: string
      ├── problem: string (HTML контент)
      ├── img: array<string>
      ├── imgUrls: array<string>
      ├── audioUrls: array<string>
      ├── numberInGroup: string
      ├── answerType: string
      ├── answer: string
      └── examType: string ("ege" или "oge")
```

### 7. Правила безопасности Firestore (для тестирования)

В разделе **Правила** Firestore добавьте:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /questions/{document} {
      allow read: if true;  // Разрешить чтение всем
      allow write: if false; // Запретить запись (можно изменить для разработки)
    }
  }
}
```

**Внимание**: Для продакшена настройте правила безопасности правильно!

### 8. Загрузка данных из парсера

Если у вас есть JSON файлы из парсера [questions-parser](https://github.com/skies21/questions-parser):

1. Убедитесь, что каждый вопрос имеет поле `examType` ("ege" или "oge")
2. Используйте скрипт для загрузки данных в Firestore или загрузите вручную через Firebase Console

### 9. Проверка подключения

1. Запустите приложение
2. Выберите тип экзамена (ЕГЭ или ОГЭ)
3. Если вопросы загружаются из Firestore, значит всё настроено правильно

## Альтернатива: Локальные данные

Если не хотите использовать Firebase, можно работать с локальными JSON файлами:

1. Поместите JSON файлы в папку `app/src/main/assets/`:
   - `ege_questions.json`
   - `oge_questions.json`
2. Формат JSON - массив объектов с полями, соответствующими модели `Question`

## Проблемы и решения

### Ошибка: "Default FirebaseApp is not initialized"

- Убедитесь, что файл `google-services.json` находится в папке `app/`
- Проверьте, что в `app/build.gradle.kts` подключен плагин `google-services`
- Убедитесь, что в `AndroidManifest.xml` указан `EGOGApplication` как `android:name`

### Вопросы не загружаются / Приложение зависает

- **Проверьте, что Firestore API включен** в Google Cloud Console
- Проверьте подключение к интернету
- Проверьте правила безопасности Firestore
- Убедитесь, что в коллекции `questions` есть документы с полем `subjectCode`
- Проверьте логи в Logcat на наличие ошибок
- Если Firestore недоступен, приложение автоматически попытается загрузить данные из локальных файлов

### Ошибка: "PERMISSION_DENIED" или "Cloud Firestore API has not been used"

Это означает, что Firestore API не включен в проекте. Решение:
1. Перейдите в [Google Cloud Console](https://console.cloud.google.com/)
2. Выберите проект `egog-771fc`
3. Включите **Cloud Firestore API**
4. Подождите несколько минут для активации
5. Перезапустите приложение

