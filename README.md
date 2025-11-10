# EGOG - Подготовка к ЕГЭ и ОГЭ

Android приложение для подготовки к экзаменам ЕГЭ и ОГЭ с проверкой ответов.

## Возможности

- ✅ Выбор типа экзамена (ЕГЭ/ОГЭ)
- ✅ Выбор предмета (Математика, Английский язык и др.)
- ✅ Список вопросов по выбранному предмету
- ✅ Отображение вопросов с поддержкой HTML и изображений
- ✅ Проверка ответов пользователя
- ✅ Интеграция с Firebase Firestore для хранения вопросов
- ✅ Поддержка локальных JSON файлов для офлайн работы
- ✅ **Автоматическое кеширование данных** (24 часа)
- ✅ **Оптимизированная загрузка** с использованием корутин
- ✅ **Параллельная загрузка** из Firebase и локальных файлов

## Технологии

- **Kotlin** - основной язык программирования
- **Jetpack Compose** - современный UI фреймворк
- **Firebase Firestore** - облачное хранилище данных
- **Navigation Compose** - навигация между экранами
- **Coil** - загрузка изображений
- **Gson** - парсинг JSON

## Настройка Firebase

1. Создайте проект в [Firebase Console](https://console.firebase.google.com/)
2. Добавьте Android приложение в проект
3. Скачайте файл `google-services.json`
4. Поместите файл в `app/` директорию проекта
5. Убедитесь, что в `app/build.gradle.kts` подключен плагин `google-services`

**📖 Подробная инструкция:** 
- [FIREBASE_SETUP.md](FIREBASE_SETUP.md) - настройка Firebase
- [FIREBASE_UPLOAD_GUIDE.md](FIREBASE_UPLOAD_GUIDE.md) - загрузка данных в Firestore (прямой доступ)
- [CLOUD_FUNCTIONS_GUIDE.md](CLOUD_FUNCTIONS_GUIDE.md) - **рекомендуется** безопасная загрузка через Cloud Functions
- [QUICK_START_CLOUD_FUNCTIONS.md](QUICK_START_CLOUD_FUNCTIONS.md) - быстрый старт с Cloud Functions

## Структура данных в Firestore

Вопросы хранятся в коллекции `questions` со следующей структурой:

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
  "subjectCode": "math_ege_profil" // код предмета (обязательно!)
}
```

### Коды предметов:
- `math_ege_profil` - Математика (профиль)
- `math_ege_base` - Математика (база)
- `math_oge` - Математика (ОГЭ)
- `eng_ege` - Английский язык (ЕГЭ)
- `eng_oge` - Английский язык (ОГЭ)

## Оптимизация и кеширование

Приложение использует многоуровневую систему загрузки данных:

1. **Кеш** (SharedPreferences) - данные кешируются на 24 часа
2. **Firebase Firestore** - облачное хранилище (приоритет)
3. **Локальные файлы** - резервный источник данных

### Преимущества:
- ⚡ **Быстрая загрузка** - данные сначала показываются из кеша
- 🔄 **Автоматическое обновление** - данные обновляются в фоне
- 📱 **Офлайн работа** - работает без интернета благодаря кешу
- 🚀 **Параллельная загрузка** - Firebase и локальные файлы загружаются одновременно
- 💾 **Экономия трафика** - данные загружаются только при необходимости

## Локальные данные

Для работы без Firebase можно поместить JSON файлы с вопросами в папку `app/src/main/assets/`:
- `ege_questions.json` - вопросы для ЕГЭ
- `oge_questions.json` - вопросы для ОГЭ

Формат JSON файла - массив объектов с полями, соответствующими модели `Question`.

**📖 Подробная инструкция:** См. [DATA_SETUP.md](DATA_SETUP.md) для детальных инструкций по настройке данных.

## Загрузка данных из парсера

Для загрузки данных из парсера [questions-parser](https://github.com/skies21/questions-parser):

### Вариант 1: Cloud Functions (рекомендуется) 🔒

**Безопасный способ** - секретные ключи хранятся только на сервере:

1. Настройте Cloud Functions (см. [QUICK_START_CLOUD_FUNCTIONS.md](QUICK_START_CLOUD_FUNCTIONS.md))
2. Используйте скрипт `scripts/upload_via_cloud_function.py`:

```bash
python scripts/upload_via_cloud_function.py \
  --json path/to/questions.json \
  --subject math_ege_profil \
  --function-url "YOUR_FUNCTION_URL" \
  --secret-key "YOUR_SECRET_KEY"
```

### Вариант 2: Прямой доступ к Firestore

**Для разработки** - требует serviceAccountKey.json:

1. Используйте скрипт `scripts/upload_to_firestore.py`:

```bash
python scripts/upload_to_firestore.py \
  --json path/to/questions.json \
  --subject math_ege_profil
```

### Вариант 3: Локальные файлы

Для работы без Firebase:

1. Поместите JSON файлы в папку `app/src/main/assets/`:
   - `ege_questions.json` - вопросы для ЕГЭ
   - `oge_questions.json` - вопросы для ОГЭ

**📖 Инструкции:**
- [CLOUD_FUNCTIONS_GUIDE.md](CLOUD_FUNCTIONS_GUIDE.md) - Cloud Functions (рекомендуется)
- [FIREBASE_UPLOAD_GUIDE.md](FIREBASE_UPLOAD_GUIDE.md) - прямой доступ к Firestore
- [DATA_SETUP.md](DATA_SETUP.md) - локальные файлы

**📝 Примечание:** В папке `app/src/main/assets/` уже есть примеры тестовых JSON файлов для быстрого тестирования приложения.

## Сборка проекта

```bash
./gradlew assembleDebug
```

## Лицензия

Проект создан для образовательных целей.

