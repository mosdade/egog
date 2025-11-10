# Быстрый старт: Firebase Cloud Functions

## Шаги для настройки Cloud Functions (10-15 минут)

### 1. Установите Firebase CLI

```bash
npm install -g firebase-tools
```

### 2. Войдите в Firebase

```bash
firebase login
```

### 3. Инициализируйте проект

```bash
cd C:\Users\User\AndroidStudioProjects\EGOG
firebase init functions
```

Выберите:
- ✅ Использовать существующий проект: `egog-771fc`
- ✅ Codebase name: `egog`
- ✅ Sub-directory: `egog`
- ✅ Язык: JavaScript
- ✅ ESLint: Да (опционально)

### 4. Установите зависимости

```bash
cd egog
npm install
cd ..
```

### 5. Установите секретный ключ (Firebase Secrets)

Сгенерируйте случайный ключ:

```bash
# Windows PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))

# Или используйте онлайн генератор: https://www.random.org/strings/
```

Создайте секрет в Firebase:

```bash
# Создайте секрет с именем SECRET_KEY
echo "YOUR_GENERATED_KEY_HERE" | firebase functions:secrets:set SECRET_KEY

# Предоставьте доступ к секрету для функций
firebase functions:secrets:access SECRET_KEY --grant-universal
```

**⚠️ ВАЖНО:** Сохраните этот ключ! Он понадобится для вызова функций.

**📖 Подробная инструкция:** См. [SETUP_SECRET_KEY.md](SETUP_SECRET_KEY.md)

### 6. Разверните функции

```bash
firebase deploy --only functions:egog
```

После деплоя вы получите URL функции, например:
```
https://us-central1-egog-771fc.cloudfunctions.net/uploadQuestions
```

### 7. Используйте новый скрипт для загрузки

```bash
# Установите зависимости
pip install requests

# Загрузите данные
python scripts/upload_via_cloud_function.py \
  --json path/to/questions.json \
  --subject math_ege_profil \
  --function-url "https://YOUR_REGION-egog-771fc.cloudfunctions.net/uploadQuestions" \
  --secret-key "YOUR_SECRET_KEY"
```

Или установите переменные окружения:

```bash
# Windows PowerShell
$env:CLOUD_FUNCTION_URL="https://YOUR_REGION-egog-771fc.cloudfunctions.net/uploadQuestions"
$env:CLOUD_FUNCTION_SECRET="YOUR_SECRET_KEY"

# Затем запустите
python scripts/upload_via_cloud_function.py --json path/to/questions.json --subject math_ege_profil
```

## Преимущества Cloud Functions

- ✅ **Безопасность** - секретные ключи не хранятся в приложении
- ✅ **Автоматизация** - можно настроить автоматическое обновление
- ✅ **Масштабируемость** - Firebase автоматически масштабирует
- ✅ **Бесплатно** - 2 миллиона вызовов в месяц бесплатно

## Готово! 🎉

Теперь вы можете загружать данные безопасно через Cloud Functions!

## Дополнительная информация

См. [CLOUD_FUNCTIONS_GUIDE.md](CLOUD_FUNCTIONS_GUIDE.md) для подробной документации.

