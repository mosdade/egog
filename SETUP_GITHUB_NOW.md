# Быстрая настройка GitHub (с вашим токеном)

## ⚠️ ВАЖНО: Безопасность

**Ваш токен:** `YOUR_TOKEN_HERE`

**Если вы планируете делиться репозиторием публично, немедленно отзовите этот токен и создайте новый!**

## Шаги (выполните по порядку)

### 1. Создайте репозиторий на GitHub

1. Откройте https://github.com
2. Нажмите **"+"** (правый верхний угол) → **"New repository"**
3. Название: `egog` (или любое другое)
4. Описание: (опционально) "Android app for EGE and OGE exam preparation"
5. Выберите **Public** (для бесплатного использования Actions)
6. **НЕ** добавляйте README, .gitignore или лицензию
7. Нажмите **"Create repository"**

### 2. Запомните URL репозитория

После создания GitHub покажет URL, например:
```
https://github.com/YOUR_USERNAME/egog.git
```

**Замените `YOUR_USERNAME` на ваш GitHub username!**

### 3. Настройте Git (если еще не настроен)

```bash
# Замените на ваши данные
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 4. Подключите к GitHub

**Вариант A: С токеном в URL (проще)**

```bash
# Замените YOUR_USERNAME на ваш GitHub username
git remote add origin https://YOUR_TOKEN_HERE@github.com/YOUR_USERNAME/egog.git

# Отправьте код
git branch -M main
git push -u origin main
```

**Вариант B: Без токена в URL (безопаснее)**

```bash
# Замените YOUR_USERNAME на ваш GitHub username
git remote add origin https://github.com/YOUR_USERNAME/egog.git

# При запросе пароля:
# Username: YOUR_USERNAME
# Password: YOUR_TOKEN_HERE
git branch -M main
git push -u origin main
```

### 5. Проверьте подключение

```bash
git remote -v
```

Должно показать ваш репозиторий.

## Следующие шаги

После успешного подключения:

1. ✅ Откройте репозиторий на GitHub
2. ✅ Перейдите в **Settings** → **Secrets and variables** → **Actions**
3. ✅ Добавьте секрет `SERVICE_ACCOUNT_KEY` (см. [GITHUB_ACTIONS_SETUP.md](GITHUB_ACTIONS_SETUP.md))
4. ✅ Поместите JSON файлы в `data/questions/`
5. ✅ Отправьте на GitHub: `git add data/questions/ && git commit -m "Add questions" && git push`

## Проблемы?

### Ошибка: "remote: Invalid username or password"

- Убедитесь, что используете токен, а не пароль
- Проверьте правильность username
- Убедитесь, что токен не истек

### Ошибка: "Repository not found"

- Убедитесь, что репозиторий создан
- Проверьте правильность URL (username и название)

### Ошибка: "Permission denied"

- Проверьте, что токен имеет права `repo` и `workflow`
- Убедитесь, что репозиторий существует

