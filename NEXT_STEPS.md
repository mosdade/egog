# Следующие шаги после подключения к GitHub

## ✅ Что уже сделано

1. ✅ Git репозиторий инициализирован
2. ✅ Репозиторий создан на GitHub: https://github.com/mosdade/egog
3. ✅ Код отправлен на GitHub
4. ✅ GitHub Actions workflow настроен

## 📋 Что нужно сделать дальше

### 1. Добавьте секрет SERVICE_ACCOUNT_KEY в GitHub

1. Откройте: https://github.com/mosdade/egog/settings/secrets/actions
2. Нажмите **"New repository secret"**
3. Имя: `SERVICE_ACCOUNT_KEY`
4. Значение: скопируйте **всё содержимое** файла `serviceAccountKey.json`
   - Откройте файл `serviceAccountKey.json` в текстовом редакторе
   - Скопируйте весь JSON (от `{` до `}`)
   - Вставьте в поле "Value"
5. Нажмите **"Add secret"**

### 2. Поместите JSON файлы с вопросами

Поместите ваши JSON файлы из парсера в папку `data/questions/`:

```bash
# Пример структуры:
data/questions/
  ├── ege_questions.json
  ├── oge_questions.json
  ├── eng_ege_questions.json
  └── eng_oge_questions.json
```

### 3. Отправьте файлы на GitHub

```bash
# Добавьте файлы
git add data/questions/

# Создайте коммит
git commit -m "Add questions data"

# Отправьте на GitHub
git push
```

GitHub Actions автоматически загрузит данные в Firestore!

### 4. Проверьте работу GitHub Actions

1. Откройте: https://github.com/mosdade/egog/actions
2. Вы увидите запуск workflow "Upload Questions to Firestore"
3. Нажмите на запуск, чтобы увидеть логи
4. Проверьте, что данные успешно загружены

### 5. Проверьте данные в Firestore

1. Откройте: https://console.firebase.google.com/project/egog-771fc/firestore
2. Убедитесь, что коллекция `questions` создана
3. Проверьте, что документы содержат поле `subjectCode`

## 🎉 Готово!

Теперь при каждом коммите с вопросами в `data/questions/` GitHub Actions автоматически загрузит их в Firestore!

## Полезные ссылки

- **Репозиторий:** https://github.com/mosdade/egog
- **Actions:** https://github.com/mosdade/egog/actions
- **Secrets:** https://github.com/mosdade/egog/settings/secrets/actions
- **Firestore:** https://console.firebase.google.com/project/egog-771fc/firestore

## Документация

- [GITHUB_ACTIONS_SETUP.md](GITHUB_ACTIONS_SETUP.md) - подробная инструкция по GitHub Actions
- [QUICK_START_FREE.md](QUICK_START_FREE.md) - быстрый старт без Blaze
- [FIREBASE_UPLOAD_GUIDE.md](FIREBASE_UPLOAD_GUIDE.md) - загрузка данных в Firestore

