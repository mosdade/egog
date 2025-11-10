# Скрипт для подключения к GitHub
# Замените YOUR_TOKEN_HERE на ваш токен

Write-Host "=== Подключение к GitHub ===" -ForegroundColor Green
Write-Host ""

# Запросите GitHub username
$GITHUB_USERNAME = Read-Host "Введите ваш GitHub username"

if ([string]::IsNullOrWhiteSpace($GITHUB_USERNAME)) {
    Write-Host "❌ Ошибка: Username не может быть пустым!" -ForegroundColor Red
    exit 1
}

$REPO_NAME = "egog"
$TOKEN = "YOUR_TOKEN_HERE"  # Замените на ваш токен

Write-Host ""
Write-Host "Подключение к GitHub..." -ForegroundColor Cyan
Write-Host "Username: $GITHUB_USERNAME" -ForegroundColor Cyan
Write-Host "Repository: $REPO_NAME" -ForegroundColor Cyan
Write-Host ""

# Проверка существования remote
$existingRemote = git remote get-url origin 2>$null
if ($existingRemote) {
    Write-Host "⚠️ Remote 'origin' уже существует: $existingRemote" -ForegroundColor Yellow
    $replace = Read-Host "Заменить? (y/n)"
    if ($replace -eq "y" -or $replace -eq "Y") {
        git remote remove origin
    } else {
        Write-Host "Отмена подключения" -ForegroundColor Yellow
        exit 0
    }
}

# Подключение к GitHub
$remoteUrl = "https://${TOKEN}@github.com/${GITHUB_USERNAME}/${REPO_NAME}.git"
git remote add origin $remoteUrl

# Переименование ветки в main
Write-Host "Переименование ветки в main..." -ForegroundColor Cyan
git branch -M main

# Отправка кода
Write-Host ""
Write-Host "Отправка кода на GitHub..." -ForegroundColor Cyan
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Готово! Репозиторий подключен к GitHub!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Репозиторий: https://github.com/${GITHUB_USERNAME}/${REPO_NAME}" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Следующие шаги:" -ForegroundColor Yellow
    Write-Host "1. Откройте https://github.com/${GITHUB_USERNAME}/${REPO_NAME}" -ForegroundColor Cyan
    Write-Host "2. Перейдите в Settings → Secrets and variables → Actions" -ForegroundColor Cyan
    Write-Host "3. Добавьте секрет SERVICE_ACCOUNT_KEY" -ForegroundColor Cyan
    Write-Host "   (скопируйте содержимое файла serviceAccountKey.json)" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "❌ Ошибка при отправке кода!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Возможные причины:" -ForegroundColor Yellow
    Write-Host "1. Репозиторий не создан на GitHub" -ForegroundColor Yellow
    Write-Host "2. Неправильный username" -ForegroundColor Yellow
    Write-Host "3. Токен недействителен или истек" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Создайте репозиторий на GitHub:" -ForegroundColor Cyan
    Write-Host "1. Откройте https://github.com/new" -ForegroundColor Cyan
    Write-Host "2. Название: $REPO_NAME" -ForegroundColor Cyan
    Write-Host "3. Выберите Public" -ForegroundColor Cyan
    Write-Host "4. НЕ добавляйте README, .gitignore или лицензию" -ForegroundColor Cyan
    Write-Host "5. Нажмите 'Create repository'" -ForegroundColor Cyan
}

