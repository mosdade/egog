# Настройка GitHub Actions для автоматической загрузки данных

## Обзор

GitHub Actions позволяет автоматически загружать вопросы в Firestore при коммите в Git или при ручном запуске. Это полностью бесплатно для публичных репозиториев!

## Шаги настройки

### Шаг 1: Инициализация Git репозитория

Если у вас еще нет Git репозитория:

```bash
# Инициализируйте Git
git init

# Добавьте все файлы
git add .

# Создайте первый коммит
git commit -m "Initial commit"
```

### Шаг 2: Создайте репозиторий на GitHub

1. Откройте [GitHub](https://github.com)
2. Нажмите **"New repository"**
3. Введите название (например, `egog`)
4. Выберите **Public** (для бесплатного использования Actions)
5. **НЕ** добавляйте README, .gitignore или лицензию (они уже есть)
6. Нажмите **"Create repository"**

### Шаг 3: Подключите локальный репозиторий к GitHub

```bash
# Добавьте удаленный репозиторий (замените YOUR_USERNAME и YOUR_REPO)
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git

# Отправьте код на GitHub
git branch -M main
git push -u origin main
```

### Шаг 4: Добавьте секрет в GitHub

1. Откройте ваш репозиторий на GitHub
2. Перейдите в **Settings** → **Secrets and variables** → **Actions**
3. Нажмите **"New repository secret"**
4. Имя: `SERVICE_ACCOUNT_KEY`
5. Значение: скопируйте **всё содержимое** файла `serviceAccountKey.json`
   - Откройте файл `serviceAccountKey.json` в текстовом редакторе
   - Скопируйте весь JSON (от `{` до `}`)
   - Вставьте в поле "Value"
6. Нажмите **"Add secret"**

**⚠️ ВАЖНО:** 
- Не добавляйте переносы строк вручную
- Скопируйте весь JSON как есть
- Убедитесь, что JSON валидный

### Шаг 5: Создайте папку для данных

```bash
# Создайте папку для вопросов
mkdir -p data/questions

# Поместите JSON файлы с вопросами в эту папку
# Например:
# data/questions/ege_questions.json
# data/questions/oge_questions.json
```

### Шаг 6: Добавьте файлы в Git

```bash
# Добавьте папку с данными
git add data/questions/

# Добавьте GitHub Actions workflow
git add .github/workflows/upload-questions.yml

# Создайте коммит
git commit -m "Add GitHub Actions workflow for automatic upload"

# Отправьте на GitHub
git push
```

## Использование

### Автоматическая загрузка при коммите

GitHub Actions автоматически запустится при коммите, если:
- Изменены файлы в `data/questions/`
- Изменен скрипт `scripts/upload_to_firestore.py`
- В сообщении коммита есть `ege` или `oge`

**Пример:**

```bash
# Добавьте новые вопросы
git add data/questions/ege_questions.json
git commit -m "Add EGE questions"
git push
```

GitHub Actions автоматически загрузит вопросы в Firestore!

### Ручной запуск через GitHub

1. Откройте ваш репозиторий на GitHub
2. Перейдите в **Actions**
3. Выберите workflow **"Upload Questions to Firestore"**
4. Нажмите **"Run workflow"**
5. Выберите:
   - **Subject:** Выберите предмет (или `all` для всех)
   - **JSON file:** (опционально) Имя файла в `data/questions/`
6. Нажмите **"Run workflow"**

### Ручной запуск через командную строку

```bash
# Добавьте файлы
git add data/questions/

# Создайте коммит с ключевым словом
git commit -m "Upload EGE questions"  # Автоматически загрузит EGE
# или
git commit -m "Upload OGE questions"  # Автоматически загрузит OGE

# Отправьте на GitHub
git push
```

## Структура файлов

```
EGOG/
├── .github/
│   └── workflows/
│       └── upload-questions.yml  # GitHub Actions workflow
├── data/
│   └── questions/
│       ├── ege_questions.json    # Вопросы для ЕГЭ
│       ├── oge_questions.json    # Вопросы для ОГЭ
│       ├── eng_ege_questions.json
│       └── eng_oge_questions.json
├── scripts/
│   └── upload_to_firestore.py    # Скрипт загрузки
└── serviceAccountKey.json        # НЕ коммитьте в Git!
```

## Проверка работы

1. Откройте ваш репозиторий на GitHub
2. Перейдите в **Actions**
3. Вы увидите список запусков workflow
4. Нажмите на последний запуск
5. Проверьте логи - должны быть сообщения об успешной загрузке

## Преимущества

- ✅ **Полностью бесплатно** для публичных репозиториев
- ✅ **Автоматическая загрузка** при коммите
- ✅ **История загрузок** - видно когда и что загружалось
- ✅ **Безопасность** - секреты хранятся в GitHub Secrets
- ✅ **Гибкость** - можно запускать вручную с выбором предмета

## Проблемы и решения

### Ошибка: "SERVICE_ACCOUNT_KEY secret not found"

**Решение:**
- Убедитесь, что секрет добавлен в GitHub (Шаг 4)
- Проверьте имя секрета: должно быть `SERVICE_ACCOUNT_KEY`
- Убедитесь, что JSON валидный

### Ошибка: "File not found"

**Решение:**
- Убедитесь, что файлы находятся в `data/questions/`
- Проверьте имена файлов (должны совпадать с workflow)
- Убедитесь, что файлы добавлены в Git

### Ошибка: "Permission denied"

**Решение:**
- Проверьте, что Firestore API включен
- Убедитесь, что `serviceAccountKey.json` имеет правильные права
- Проверьте правила безопасности Firestore

## Дополнительная информация

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [QUICK_START_FREE.md](QUICK_START_FREE.md) - быстрый старт
- [FREE_CLOUD_ALTERNATIVES.md](FREE_CLOUD_ALTERNATIVES.md) - другие альтернативы

