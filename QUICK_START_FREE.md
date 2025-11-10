# Быстрый старт: Бесплатная загрузка данных (без Blaze)

## Вариант 1: Локальная загрузка (рекомендуется для начала) ✅

### Шаг 1: Установите зависимости

```bash
pip install firebase-admin
```

### Шаг 2: Загрузите данные

```bash
python scripts/upload_to_firestore.py \
  --json path/to/questions.json \
  --subject math_ege_profil \
  --credentials serviceAccountKey.json
```

**Примеры:**

```bash
# Для ЕГЭ по математике (профиль)
python scripts/upload_to_firestore.py \
  --json ege_questions.json \
  --subject math_ege_profil

# Для ОГЭ по математике
python scripts/upload_to_firestore.py \
  --json oge_questions.json \
  --subject math_oge

# Для ЕГЭ по английскому языку
python scripts/upload_to_firestore.py \
  --json eng_ege_questions.json \
  --subject eng_ege
```

**Коды предметов:**
- `math_ege_profil` - Математика (профиль) ЕГЭ
- `math_ege_base` - Математика (база) ЕГЭ
- `math_oge` - Математика ОГЭ
- `eng_ege` - Английский язык ЕГЭ
- `eng_oge` - Английский язык ОГЭ

### Готово! 🎉

Данные загружены в Firestore. Теперь приложение будет загружать вопросы из Firebase!

## Вариант 2: Автоматизация через GitHub Actions (бесплатно) 🆓

### Шаг 1: Добавьте секрет в GitHub

1. Откройте ваш репозиторий на GitHub
2. Перейдите в **Settings** → **Secrets and variables** → **Actions**
3. Нажмите **"New repository secret"**
4. Имя: `SERVICE_ACCOUNT_KEY`
5. Значение: скопируйте содержимое файла `serviceAccountKey.json`
6. Нажмите **"Add secret"**

### Шаг 2: Создайте папку для данных

```bash
mkdir -p data/questions
```

Поместите JSON файлы с вопросами в эту папку:
- `data/questions/ege_questions.json`
- `data/questions/oge_questions.json`

### Шаг 3: Запустите автоматическую загрузку

**Вариант A:** При коммите в Git (автоматически)

```bash
git add data/questions/
git commit -m "Add questions"
git push
```

GitHub Actions автоматически загрузит данные в Firestore!

**Вариант B:** Вручную через GitHub

1. Откройте репозиторий на GitHub
2. Перейдите в **Actions**
3. Выберите workflow **"Upload Questions to Firestore"**
4. Нажмите **"Run workflow"**
5. Выберите ветку и нажмите **"Run workflow"**

### Готово! 🎉

Данные автоматически загружены в Firestore!

## Преимущества каждого варианта

### Вариант 1 (Локальная загрузка)
- ✅ Простота - запускается одной командой
- ✅ Быстро - нет задержек на CI/CD
- ✅ Контроль - видите процесс загрузки
- ⚠️ Ручной запуск - нужно запускать вручную

### Вариант 2 (GitHub Actions)
- ✅ Автоматизация - загрузка при коммите
- ✅ Бесплатно - GitHub Actions бесплатен для публичных репозиториев
- ✅ История - видно когда и что загружалось
- ⚠️ Требует настройки - нужно добавить секрет в GitHub

## Рекомендация

**Для начала:** Используйте **Вариант 1** - это самый простой и быстрый способ.

**Для автоматизации:** Настройте **Вариант 2** - полностью бесплатно и автоматически.

## Дополнительная информация

См. [FREE_CLOUD_ALTERNATIVES.md](FREE_CLOUD_ALTERNATIVES.md) для других бесплатных альтернатив (Vercel, Railway, Render).

