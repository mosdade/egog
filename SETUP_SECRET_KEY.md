# Настройка секретного ключа для Cloud Functions

## Важно: Новый способ хранения секретов

В Firebase Functions v2 секреты хранятся через **Firebase Secrets**, а не через `functions.config()`.

## Шаги настройки

### 1. Установите секретный ключ

```bash
# Генерация случайного ключа (Windows PowerShell)
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))

# Или используйте онлайн генератор: https://www.random.org/strings/
```

### 2. Создайте секрет в Firebase

```bash
# Создайте секрет с именем SECRET_KEY
echo "YOUR_GENERATED_KEY_HERE" | firebase functions:secrets:set SECRET_KEY
```

Или через Firebase Console:
1. Откройте [Firebase Console](https://console.firebase.google.com/)
2. Выберите проект `egog-771fc`
3. Перейдите в **Functions** → **Secrets**
4. Нажмите **"Add secret"**
5. Введите имя: `SECRET_KEY`
6. Введите значение: ваш сгенерированный ключ
7. Нажмите **"Add"**

### 3. Предоставьте доступ к секрету

После создания секрета нужно предоставить доступ к нему для функций:

```bash
# Предоставить доступ к секрету для всех функций
firebase functions:secrets:access SECRET_KEY --grant-universal
```

### 4. Проверьте секрет

```bash
# Просмотр списка секретов
firebase functions:secrets:list
```

## Использование в коде

В коде функции секрет доступен через `process.env.SECRET_KEY`:

```javascript
const secretKey = process.env.SECRET_KEY;
```

## Обновление существующих функций

Если у вас уже развернуты функции, после добавления секрета нужно их переразвернуть:

```bash
firebase deploy --only functions:egog
```

## Безопасность

- ✅ Секреты хранятся в зашифрованном виде
- ✅ Не коммитьте секреты в Git
- ✅ Используйте сложные случайные ключи
- ✅ Регулярно обновляйте секреты

## Проблемы и решения

### Ошибка: "Secret not found"

- Убедитесь, что секрет создан: `firebase functions:secrets:list`
- Проверьте, что секрет предоставлен функциям: `firebase functions:secrets:access SECRET_KEY --grant-universal`
- Переразверните функции после добавления секрета

### Ошибка: "Unauthorized"

- Проверьте, что секрет установлен правильно
- Убедитесь, что ключ передается в заголовке `Authorization: Bearer YOUR_KEY`
- Проверьте, что секрет предоставлен функциям

