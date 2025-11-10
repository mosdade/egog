# Быстрый старт: GitHub Actions (5 минут)

## Шаги для настройки автоматической загрузки

### 1. Инициализируйте Git репозиторий (если еще не сделано)

```bash
git init
git add .
git commit -m "Initial commit"
```

### 2. Создайте репозиторий на GitHub

1. Откройте https://github.com
2. Нажмите **"New repository"**
3. Название: `egog` (или любое другое)
4. Выберите **Public** (для бесплатного использования Actions)
5. **НЕ** добавляйте README, .gitignore или лицензию
6. Нажмите **"Create repository"**

### 3. Подключите к GitHub

```bash
# Замените YOUR_USERNAME и YOUR_REPO на ваши данные
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git branch -M main
git push -u origin main
```

### 4. Добавьте секрет в GitHub

1. Откройте ваш репозиторий на GitHub
2. **Settings** → **Secrets and variables** → **Actions**
3. Нажмите **"New repository secret"**
4. Имя: `SERVICE_ACCOUNT_KEY`
5. Значение: скопируйте **всё содержимое** файла `serviceAccountKey.json`
   - Откройте `serviceAccountKey.json` в текстовом редакторе
   - Скопируйте весь JSON (от `{` до `}`)
   - Вставьте в поле "Value"
6. Нажмите **"Add secret"**

### 5. Поместите JSON файлы в папку

```bash
# Поместите ваши JSON файлы в папку data/questions/
# Например:
# data/questions/ege_questions.json
# data/questions/oge_questions.json
```

### 6. Отправьте на GitHub

```bash
git add data/questions/
git add .github/workflows/
git commit -m "Add GitHub Actions workflow"
git push
```

## Использование

### Автоматическая загрузка

GitHub Actions автоматически запустится при коммите, если:
- Изменены файлы в `data/questions/`
- В сообщении коммита есть `ege` или `oge`

**Пример:**

```bash
git add data/questions/ege_questions.json
git commit -m "Add EGE questions"
git push
```

### Ручной запуск через GitHub

1. Откройте ваш репозиторий на GitHub
2. Перейдите в **Actions**
3. Выберите **"Upload Questions to Firestore"**
4. Нажмите **"Run workflow"**
5. Выберите предмет (или `all` для всех)
6. Нажмите **"Run workflow"**

## Готово! 🎉

Теперь данные будут автоматически загружаться в Firestore!

## Дополнительная информация

См. [GITHUB_ACTIONS_SETUP.md](GITHUB_ACTIONS_SETUP.md) для подробной документации.

