# Инструкция по настройке данных

## Проблема: "Вопросы не найдены"

Если вы видите сообщение об ошибке "Вопросы не найдены", это означает, что:
1. ❌ Firestore API не включен в Google Cloud Console
2. ❌ Нет JSON файлов в папке `app/src/main/assets/`
3. ❌ Нет данных в Firebase Firestore

## Решение 1: Использование локальных JSON файлов (быстро)

### Шаг 1: Получение данных из парсера

У вас есть парсер вопросов в папке:
```
C:\Users\User\Downloads\questions-parser-main\questions-parser-main
```

1. Запустите парсер согласно его инструкциям
2. Получите JSON файлы с вопросами

### Шаг 2: Подготовка JSON файлов

Создайте два файла:
- `ege_questions.json` - для вопросов ЕГЭ
- `oge_questions.json` - для вопросов ОГЭ

**Формат JSON файла** - массив объектов:

```json
[
  {
    "id": "4F5745",
    "guid": "00011EA767B997BD431096F9DE1CA7F1",
    "hint": "Впишите правильный ответ.",
    "codifier": ["7.3 Многоугольники", "7.5 Измерение геометрических величин"],
    "question": "Один из углов прямоугольной трапеции равен 64°...",
    "problem": "<table>...</table>",
    "img": ["4F5745/0.png"],
    "imgUrls": ["https://oge.fipi.ru/.../image.png"],
    "audioUrls": [],
    "numberInGroup": "",
    "answerType": "Краткий ответ",
    "answer": "116"
  },
  {
    "id": "5A1234",
    "guid": "00011EA767B997BD431096F9DE1CA7F2",
    "hint": "Выберите правильный ответ.",
    "codifier": ["1.1 Алгебра"],
    "question": "Решите уравнение...",
    "problem": "<div>...</div>",
    "img": [],
    "imgUrls": [],
    "audioUrls": [],
    "numberInGroup": "",
    "answerType": "Единичный выбор",
    "answer": "2"
  }
]
```

### Шаг 3: Разделение по предметам

Если у вас один большой JSON файл, разделите вопросы по предметам:

**Для ЕГЭ:**
- Математика (профиль) - `math_ege_profil`
- Математика (база) - `math_ege_base`
- Английский язык - `eng_ege`

**Для ОГЭ:**
- Математика - `math_oge`
- Английский язык - `eng_oge`

**Важно:** Приложение автоматически определяет тип экзамена (ЕГЭ/ОГЭ) из кода предмета, но для локальных файлов использует общие файлы `ege_questions.json` и `oge_questions.json`.

### Шаг 4: Размещение файлов

Поместите файлы в папку:
```
app/src/main/assets/
```

Структура должна быть:
```
app/src/main/assets/
  ├── ege_questions.json
  └── oge_questions.json
```

### Шаг 5: Пересборка приложения

После добавления файлов:
1. Синхронизируйте проект (Sync Project with Gradle Files)
2. Пересоберите приложение (Build > Rebuild Project)
3. Запустите приложение

## Решение 2: Использование Firebase Firestore (рекомендуется)

### Шаг 1: Включение Firestore API

1. Откройте [Google Cloud Console](https://console.cloud.google.com/)
2. Выберите проект `egog-771fc` (или ваш проект)
3. Перейдите в **APIs & Services** > **Library**
4. Найдите **Cloud Firestore API**
5. Нажмите **Enable**

Или перейдите по прямой ссылке:
```
https://console.developers.google.com/apis/api/firestore.googleapis.com/overview?project=egog-771fc
```

### Шаг 2: Настройка правил доступа

В Firebase Console:
1. Перейдите в **Firestore Database**
2. Откройте вкладку **Rules**
3. Установите правила для чтения (для тестирования):

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /questions/{document=**} {
      allow read: if true;  // Разрешить чтение всем (для тестирования)
      allow write: if false; // Запретить запись (настройте позже)
    }
  }
}
```

**⚠️ Внимание:** Эти правила разрешают чтение всем. Для продакшена настройте аутентификацию!

### Шаг 3: Загрузка данных в Firestore

#### Вариант A: Через Firebase Console (вручную)

1. Откройте Firebase Console
2. Перейдите в **Firestore Database**
3. Создайте коллекцию `questions`
4. Добавьте документы с вопросами

**Структура документа:**
```json
{
  "id": "4F5745",
  "guid": "00011EA767B997BD431096F9DE1CA7F1",
  "hint": "Впишите правильный ответ.",
  "codifier": ["7.3 Многоугольники", "7.5 Измерение геометрических величин"],
  "question": "Один из углов прямоугольной трапеции равен 64°...",
  "problem": "<table>...</table>",
  "img": ["4F5745/0.png"],
  "imgUrls": ["https://oge.fipi.ru/.../image.png"],
  "audioUrls": [],
  "numberInGroup": "",
  "answerType": "Краткий ответ",
  "answer": "116",
  "subjectCode": "math_ege_profil"  // ОБЯЗАТЕЛЬНО! Код предмета
}
```

#### Вариант B: Через скрипт (автоматически)

Создайте скрипт для загрузки данных из JSON в Firestore. Пример на Python:

```python
import json
import firebase_admin
from firebase_admin import credentials, firestore

# Инициализация Firebase
cred = credentials.Certificate("path/to/serviceAccountKey.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

# Загрузка JSON
with open('ege_questions.json', 'r', encoding='utf-8') as f:
    questions = json.load(f)

# Загрузка в Firestore
for question in questions:
    # Добавьте subjectCode на основе данных вопроса
    question['subjectCode'] = determine_subject_code(question)
    
    db.collection('questions').add(question)

print(f"Загружено {len(questions)} вопросов")
```

### Шаг 4: Проверка данных

После загрузки данных:
1. Убедитесь, что в каждом документе есть поле `subjectCode`
2. Проверьте, что данные загружены в коллекцию `questions`
3. Перезапустите приложение

## Коды предметов

Для правильной фильтрации вопросов используйте следующие коды:

| Предмет | Код |
|---------|-----|
| Математика (профиль) ЕГЭ | `math_ege_profil` |
| Математика (база) ЕГЭ | `math_ege_base` |
| Английский язык ЕГЭ | `eng_ege` |
| Математика ОГЭ | `math_oge` |
| Английский язык ОГЭ | `eng_oge` |

## Проверка работы

После настройки данных:

1. Запустите приложение
2. Выберите тип экзамена (ЕГЭ или ОГЭ)
3. Примите предупреждение
4. Выберите предмет
5. Должен появиться список вопросов

Если вопросы не загружаются:
- Проверьте логи в Android Studio (Logcat)
- Убедитесь, что файлы находятся в правильной папке
- Проверьте формат JSON (должен быть валидным)
- Убедитесь, что Firestore API включен

## Пример минимального JSON файла

Для тестирования можно создать минимальный файл `ege_questions.json`:

```json
[
  {
    "id": "TEST001",
    "guid": null,
    "hint": "Впишите правильный ответ.",
    "codifier": ["Тест"],
    "question": "Сколько будет 2 + 2?",
    "problem": "",
    "img": [],
    "imgUrls": [],
    "audioUrls": [],
    "numberInGroup": "",
    "answerType": "Краткий ответ",
    "answer": "4"
  }
]
```

Поместите этот файл в `app/src/main/assets/ege_questions.json` для быстрого тестирования.

