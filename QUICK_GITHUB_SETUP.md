# Быстрая настройка GitHub (3 команды)

## ⚠️ ВАЖНО: Безопасность токена

**Ваш токен:** `YOUR_TOKEN_HERE`

Если планируете делиться репозиторием публично, **отзовите этот токен** и создайте новый!

## Шаги

### 1. Создайте репозиторий на GitHub

1. Откройте https://github.com
2. Нажмите **"+"** → **"New repository"**
3. Название: `egog`
4. Выберите **Public**
5. **НЕ** добавляйте README, .gitignore или лицензию
6. Нажмите **"Create repository"**

### 2. Найдите ваш GitHub username

После создания репозитория URL будет:
```
https://github.com/YOUR_USERNAME/egog.git
```

**Найдите `YOUR_USERNAME`** - это ваш GitHub username.

### 3. Выполните команды в терминале

Откройте PowerShell в папке проекта и выполните (замените `YOUR_USERNAME` и ваши данные):

```powershell
# Настройте Git (замените на ваши данные)
git config user.name "Ваше Имя"
git config user.email "your.email@example.com"

# Создайте первый коммит
git commit -m "Initial commit"

# Подключите к GitHub (ЗАМЕНИТЕ YOUR_USERNAME!)
git remote add origin https://YOUR_TOKEN_HERE@github.com/YOUR_USERNAME/egog.git

# Отправьте код
git branch -M main
git push -u origin main
```

**Важно:** Замените:
- `YOUR_USERNAME` - на ваш GitHub username
- `Ваше Имя` - на ваше имя
- `your.email@example.com` - на ваш email

## Готово! 🎉

После успешного push:

1. Откройте ваш репозиторий на GitHub
2. Перейдите в **Settings** → **Secrets and variables** → **Actions**
3. Добавьте секрет `SERVICE_ACCOUNT_KEY` (см. [GITHUB_ACTIONS_SETUP.md](GITHUB_ACTIONS_SETUP.md))

## Проблемы?

### Не знаете ваш GitHub username?

1. Откройте https://github.com
2. Посмотрите в правый верхний угол
3. Или откройте любой ваш репозиторий - username в URL

### Ошибка: "Author identity unknown"

Выполните:
```powershell
git config user.name "Ваше Имя"
git config user.email "your.email@example.com"
```

### Ошибка: "Repository not found"

- Убедитесь, что репозиторий создан на GitHub
- Проверьте правильность username в URL

