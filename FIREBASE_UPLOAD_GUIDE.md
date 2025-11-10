# Инструкция по загрузке данных в Firebase Firestore

## Быстрый старт

### Шаг 1: Включение Firestore API

**ВАЖНО:** Перед загрузкой данных нужно включить Firestore API!

1. Откройте [Google Cloud Console](https://console.cloud.google.com/)
2. Выберите проект **egog-771fc**
3. Перейдите в **APIs & Services** → **Library**
4. Найдите **Cloud Firestore API**
5. Нажмите **Enable** (Включить)
6. Дождитесь активации (может занять несколько минут)

**Прямая ссылка:**
```
https://console.developers.google.com/apis/api/firestore.googleapis.com/overview?project=egog-771fc
```

### Шаг 2: Создание базы данных Firestore

1. Откройте [Firebase Console](https://console.firebase.google.com/)
2. Выберите проект **egog-771fc**
3. Перейдите в раздел **Firestore Database**
4. Нажмите **"Создать базу данных"**
5. Выберите режим:
   - **Режим тестирования** (для разработки) - рекомендуется для начала
6. Выберите регион (например, `europe-west`)
7. Нажмите **"Включить"**

### Шаг 3: Настройка правил безопасности

В разделе **Правила** Firestore добавьте:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /questions/{document} {
      allow read: if true;  // Разрешить чтение всем (для тестирования)
      allow write: if false; // Запретить запись через клиент (загрузка через скрипт)
    }
  }
}
```

**⚠️ Внимание:** Эти правила разрешают чтение всем. Для продакшена настройте аутентификацию!

### Шаг 4: Получение учетных данных для скрипта

Для загрузки данных через скрипт нужен файл `serviceAccountKey.json`:

1. Откройте [Firebase Console](https://console.firebase.google.com/)
2. Выберите проект **egog-771fc**
3. Перейдите в **Project Settings** (⚙️) → **Service Accounts**
4. Нажмите **"Generate new private key"**
5. Сохраните файл как `serviceAccountKey.json`
6. **⚠️ ВАЖНО:** Не коммитьте этот файл в Git! Добавьте его в `.gitignore`

### Шаг 5: Подготовка данных из парсера

Если у вас есть данные из парсера [questions-parser](https://github.com/skies21/questions-parser):

1. Убедитесь, что JSON файл содержит массив вопросов
2. Каждый вопрос должен иметь следующие поля:
   - `id` - уникальный идентификатор вопроса
   - `question` - текст вопроса
   - `answer` - правильный ответ
   - `answerType` - тип ответа (например, "Краткий ответ", "Единичный выбор")
   - Остальные поля опциональны

### Шаг 6: Загрузка данных через скрипт

#### Установка зависимостей

```bash
pip install firebase-admin
```

#### Использование скрипта

```bash
python scripts/upload_to_firestore.py \
  --json path/to/questions.json \
  --subject math_ege_profil \
  --credentials serviceAccountKey.json
```

**Параметры:**
- `--json` - путь к JSON файлу с вопросами
- `--subject` - код предмета (обязательно!):
  - `math_ege_profil` - Математика (профиль) ЕГЭ
  - `math_ege_base` - Математика (база) ЕГЭ
  - `math_oge` - Математика ОГЭ
  - `eng_ege` - Английский язык ЕГЭ
  - `eng_oge` - Английский язык ОГЭ
- `--credentials` - путь к `serviceAccountKey.json` (по умолчанию `serviceAccountKey.json`)
- `--batch-size` - размер батча для загрузки (по умолчанию 500)

**Примеры:**

```bash
# Загрузка вопросов ЕГЭ по математике (профиль)
python scripts/upload_to_firestore.py \
  --json ege_questions.json \
  --subject math_ege_profil

# Загрузка вопросов ОГЭ по математике
python scripts/upload_to_firestore.py \
  --json oge_questions.json \
  --subject math_oge

# Загрузка вопросов ЕГЭ по английскому языку
python scripts/upload_to_firestore.py \
  --json eng_ege_questions.json \
  --subject eng_ege
```

### Шаг 7: Проверка загрузки

1. Откройте [Firebase Console](https://console.firebase.google.com/)
2. Перейдите в **Firestore Database**
3. Убедитесь, что коллекция `questions` создана
4. Проверьте, что документы содержат поле `subjectCode`
5. Запустите приложение и проверьте загрузку вопросов

## Структура данных в Firestore

Каждый документ в коллекции `questions` должен иметь следующую структуру:

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
  "subjectCode": "math_ege_profil"  // ОБЯЗАТЕЛЬНО!
}
```

### Коды предметов

| Предмет | Код |
|---------|-----|
| Математика (профиль) ЕГЭ | `math_ege_profil` |
| Математика (база) ЕГЭ | `math_ege_base` |
| Математика ОГЭ | `math_oge` |
| Английский язык ЕГЭ | `eng_ege` |
| Английский язык ОГЭ | `eng_oge` |

## Альтернатива: Загрузка вручную через Firebase Console

Если не хотите использовать скрипт, можно загрузить данные вручную:

1. Откройте [Firebase Console](https://console.firebase.google.com/)
2. Перейдите в **Firestore Database**
3. Нажмите **"Начать коллекцию"**
4. Введите имя коллекции: `questions`
5. Добавьте документы вручную или используйте импорт

**⚠️ Внимание:** При ручной загрузке не забудьте добавить поле `subjectCode` к каждому документу!

## Проблемы и решения

### Ошибка: "Cloud Firestore API has not been used"

**Решение:**
1. Включите Firestore API в Google Cloud Console (см. Шаг 1)
2. Подождите несколько минут для активации
3. Перезапустите приложение

### Ошибка: "PERMISSION_DENIED"

**Решение:**
1. Проверьте правила безопасности Firestore (см. Шаг 3)
2. Убедитесь, что правила разрешают чтение
3. Проверьте, что Firestore API включен

### Ошибка: "serviceAccountKey.json not found"

**Решение:**
1. Получите файл `serviceAccountKey.json` (см. Шаг 4)
2. Поместите его в корень проекта или укажите путь через `--credentials`

### Вопросы не загружаются в приложении

**Проверьте:**
1. ✅ Firestore API включен
2. ✅ База данных Firestore создана
3. ✅ В коллекции `questions` есть документы
4. ✅ У каждого документа есть поле `subjectCode`
5. ✅ Правила безопасности разрешают чтение
6. ✅ Приложение имеет доступ к интернету

### Скрипт не может загрузить данные

**Проверьте:**
1. ✅ Файл `serviceAccountKey.json` существует и валиден
2. ✅ JSON файл имеет правильный формат (массив объектов)
3. ✅ У каждого вопроса есть поле `id`
4. ✅ Указан правильный код предмета (`--subject`)

## Безопасность

**⚠️ ВАЖНО:**
- Никогда не коммитьте `serviceAccountKey.json` в Git!
- Добавьте его в `.gitignore`:
  ```
  serviceAccountKey.json
  ```
- Для продакшена настройте правила безопасности Firestore с аутентификацией
- Ограничьте права записи в правилах Firestore

## Следующие шаги

После успешной загрузки данных:

1. ✅ Проверьте загрузку вопросов в приложении
2. ✅ Убедитесь, что вопросы отображаются правильно
3. ✅ Проверьте работу проверки ответов
4. ✅ Настройте правила безопасности для продакшена
5. ✅ Рассмотрите возможность добавления индексов для оптимизации запросов

