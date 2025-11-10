# Настройка GitHub с токеном

## ⚠️ ВАЖНО: Безопасность токена

**Токен уже показан в сообщении!** Если вы планируете делиться этим репозиторием, **немедленно отзовите этот токен** и создайте новый:

1. Откройте: https://github.com/settings/tokens
2. Найдите ваш токен
3. Нажмите **"Revoke"** (Отозвать)
4. Создайте новый токен

## Шаги настройки

### 1. Инициализация Git репозитория

```bash
# Инициализируйте Git
git init

# Настройте имя пользователя (замените на ваше)
git config user.name "Your Name"
git config user.email "your.email@example.com"

# Или глобально для всех репозиториев
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 2. Создайте репозиторий на GitHub

1. Откройте https://github.com
2. Нажмите **"New repository"** (или **"+"** → **"New repository"**)
3. Введите название (например, `egog`)
4. Выберите **Public** (для бесплатного использования Actions)
5. **НЕ** добавляйте README, .gitignore или лицензию
6. Нажмите **"Create repository"**

### 3. Подключите локальный репозиторий к GitHub

После создания репозитория GitHub покажет инструкции. Используйте токен вместо пароля:

```bash
# Добавьте все файлы
git add .

# Создайте первый коммит
git commit -m "Initial commit"

# Подключите к GitHub (замените YOUR_USERNAME, YOUR_REPO и YOUR_TOKEN)
git remote add origin https://YOUR_TOKEN@github.com/YOUR_USERNAME/YOUR_REPO.git

# Или используйте стандартный формат (Git запросит токен)
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git

# Отправьте код
git branch -M main
git push -u origin main
```

**При запросе пароля:**
- Username: ваш GitHub username
- Password: вставьте ваш токен

### 4. Альтернатива: Использование токена в URL

Можно встроить токен прямо в URL (менее безопасно, но удобно):

```bash
git remote add origin https://YOUR_TOKEN@github.com/YOUR_USERNAME/YOUR_REPO.git
```

### 5. Сохранение токена в Git Credential Manager

Чтобы не вводить токен каждый раз:

**Windows:**
```bash
# Git Credential Manager сохранит токен
git config --global credential.helper manager-core
```

При первом push Git запросит:
- Username: ваш GitHub username
- Password: вставьте токен

Git Credential Manager сохранит его для будущих операций.

## Проверка подключения

```bash
# Проверьте подключенные репозитории
git remote -v

# Должно показать:
# origin  https://github.com/YOUR_USERNAME/YOUR_REPO.git (fetch)
# origin  https://github.com/YOUR_USERNAME/YOUR_REPO.git (push)
```

## Следующие шаги

После подключения к GitHub:

1. ✅ Добавьте секрет `SERVICE_ACCOUNT_KEY` в GitHub (см. [GITHUB_ACTIONS_SETUP.md](GITHUB_ACTIONS_SETUP.md))
2. ✅ Поместите JSON файлы в `data/questions/`
3. ✅ Отправьте код на GitHub
4. ✅ GitHub Actions автоматически загрузит данные в Firestore!

## Проблемы и решения

### Ошибка: "remote: Invalid username or password"

**Решение:**
- Убедитесь, что используете токен, а не пароль
- Проверьте, что токен не истек
- Убедитесь, что выбран правильный scope (`repo` и `workflow`)

### Ошибка: "Permission denied"

**Решение:**
- Проверьте, что токен имеет права `repo`
- Убедитесь, что репозиторий существует
- Проверьте, что вы используете правильный username

### Ошибка: "Repository not found"

**Решение:**
- Убедитесь, что репозиторий создан на GitHub
- Проверьте правильность URL (username и название репозитория)
- Убедитесь, что у токена есть доступ к репозиторию

## Безопасность

- ⚠️ **НЕ коммитьте токен в Git!**
- ⚠️ **НЕ делитесь токеном публично!**
- ⚠️ Если токен показан публично, немедленно отзовите его
- ✅ Используйте Git Credential Manager для безопасного хранения
- ✅ Установите срок действия токена
- ✅ Регулярно обновляйте токены

