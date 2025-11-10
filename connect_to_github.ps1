# Скрипт для подключения к GitHub
# ВАЖНО: Замените YOUR_USERNAME на ваш GitHub username!

Write-Host "=== Подключение к GitHub ===" -ForegroundColor Green
Write-Host ""

# Замените YOUR_USERNAME на ваш GitHub username!
$GITHUB_USERNAME = "YOUR_USERNAME"
$REPO_NAME = "egog"
$TOKEN = "YOUR_TOKEN_HERE"  # Замените на ваш токен

Write-Host "⚠️ ВАЖНО: Замените YOUR_USERNAME в скрипте на ваш GitHub username!" -ForegroundColor Yellow
Write-Host ""

if ($GITHUB_USERNAME -eq "YOUR_USERNAME") {
    Write-Host "❌ Ошибка: Замените YOUR_USERNAME на ваш GitHub username!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Как найти ваш username:" -ForegroundColor Cyan
    Write-Host "1. Откройте https://github.com" -ForegroundColor Cyan
    Write-Host "2. Посмотрите в правый верхний угол" -ForegroundColor Cyan
    Write-Host "3. Или откройте любой ваш репозиторий - username в URL" -ForegroundColor Cyan
    exit 1
}

# Настройка Git (если еще не настроен)
Write-Host "Настройка Git..." -ForegroundColor Cyan
$userName = Read-Host "Введите ваше имя (для Git)"
$userEmail = Read-Host "Введите ваш email (для Git)"

git config user.name $userName
git config user.email $userEmail

# Создание первого коммита
Write-Host ""
Write-Host "Создание первого коммита..." -ForegroundColor Cyan
git commit -m "Initial commit"

# Подключение к GitHub
Write-Host ""
Write-Host "Подключение к GitHub..." -ForegroundColor Cyan
$remoteUrl = "https://${TOKEN}@github.com/${GITHUB_USERNAME}/${REPO_NAME}.git"
git remote add origin $remoteUrl

# Отправка кода
Write-Host ""
Write-Host "Отправка кода на GitHub..." -ForegroundColor Cyan
git branch -M main
git push -u origin main

Write-Host ""
Write-Host "✅ Готово! Репозиторий подключен к GitHub!" -ForegroundColor Green
Write-Host ""
Write-Host "Следующие шаги:" -ForegroundColor Cyan
Write-Host "1. Откройте https://github.com/${GITHUB_USERNAME}/${REPO_NAME}" -ForegroundColor Cyan
Write-Host "2. Перейдите в Settings → Secrets and variables → Actions" -ForegroundColor Cyan
Write-Host "3. Добавьте секрет SERVICE_ACCOUNT_KEY" -ForegroundColor Cyan

