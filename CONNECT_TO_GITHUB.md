# Подключение к GitHub (пошаговая инструкция)

## Ваш токен
```
YOUR_TOKEN_HERE
```

## Шаги

### 1. Создайте репозиторий на GitHub

1. Откройте https://github.com
2. Нажмите **"+"** (правый верхний угол) → **"New repository"**
3. Название: `egog` (или любое другое)
4. Выберите **Public**
5. **НЕ** добавляйте README, .gitignore или лицензию
6. Нажмите **"Create repository"**

### 2. Запомните ваш GitHub username

После создания репозитория URL будет:
```
https://github.com/YOUR_USERNAME/egog.git
```

**Найдите `YOUR_USERNAME` в URL** - это ваш GitHub username.

### 3. Выполните команды (замените YOUR_USERNAME)

Откройте терминал в папке проекта и выполните:

```bash
# Настройте Git (если еще не настроен)
git config user.name "Your Name"
git config user.email "your.email@example.com"

# Создайте первый коммит
git commit -m "Initial commit"

# Подключите к GitHub (ЗАМЕНИТЕ YOUR_USERNAME!)
git remote add origin https://YOUR_TOKEN_HERE@github.com/YOUR_USERNAME/egog.git

# Отправьте код
git branch -M main
git push -u origin main
```

**Важно:** Замените `YOUR_USERNAME` на ваш реальный GitHub username!

### 4. Альтернатива (без токена в URL)

Если не хотите встраивать токен в URL:

```bash
# Подключите к GitHub (ЗАМЕНИТЕ YOUR_USERNAME!)
git remote add origin https://github.com/YOUR_USERNAME/egog.git

# При запросе пароля:
# Username: YOUR_USERNAME
# Password: YOUR_TOKEN_HERE
git branch -M main
git push -u origin main
```

## Проверка

После успешного push:

1. Откройте ваш репозиторий на GitHub
2. Вы должны увидеть все файлы проекта
3. Перейдите в **Actions** - там должен появиться workflow

## Следующие шаги

После успешного подключения:

1. ✅ Добавьте секрет `SERVICE_ACCOUNT_KEY` в GitHub
2. ✅ Поместите JSON файлы в `data/questions/`
3. ✅ Отправьте на GitHub: `git add data/questions/ && git commit -m "Add questions" && git push`

## Проблемы?

### Не знаете ваш GitHub username?

1. Откройте https://github.com
2. Посмотрите в правый верхний угол - там ваш username
3. Или откройте любой ваш репозиторий - username в URL

### Ошибка при push?

Проверьте:
- Правильность username
- Правильность названия репозитория
- Что репозиторий создан на GitHub
- Что токен не истек

