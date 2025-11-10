#!/usr/bin/env python3
"""
Скрипт для загрузки вопросов через Firebase Cloud Functions.

Этот скрипт использует Cloud Functions вместо прямого доступа к Firestore,
что обеспечивает безопасность и не требует serviceAccountKey.json в приложении.

Использование:
    python scripts/upload_via_cloud_function.py --json path/to/questions.json --subject math_ege_profil

Требования:
    pip install requests
"""

import json
import argparse
import requests
import os
from typing import Dict, List, Any

# Коды предметов
SUBJECT_CODES = {
    "math_ege_profil": "Математика (профиль) ЕГЭ",
    "math_ege_base": "Математика (база) ЕГЭ",
    "math_oge": "Математика ОГЭ",
    "eng_ege": "Английский язык ЕГЭ",
    "eng_oge": "Английский язык ОГЭ"
}

def load_questions_from_json(json_path: str) -> List[Dict[str, Any]]:
    """
    Загружает вопросы из JSON файла.
    
    Args:
        json_path: Путь к JSON файлу
    
    Returns:
        Список вопросов
    """
    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    # Если это список, возвращаем как есть
    if isinstance(data, list):
        return data
    
    # Если это словарь, пытаемся найти список вопросов
    if isinstance(data, dict):
        for key in ['questions', 'data', 'items']:
            if key in data and isinstance(data[key], list):
                return data[key]
    
    raise ValueError(f"Не удалось найти список вопросов в файле {json_path}")

def upload_via_cloud_function(
    questions: List[Dict[str, Any]],
    subject_code: str,
    function_url: str,
    secret_key: str
) -> Dict[str, Any]:
    """
    Загружает вопросы через Cloud Function.
    
    Args:
        questions: Список вопросов
        subject_code: Код предмета
        function_url: URL Cloud Function
        secret_key: Секретный ключ для авторизации
    
    Returns:
        Результат загрузки
    """
    print(f"Отправка {len(questions)} вопросов в Cloud Function...")
    
    try:
        response = requests.post(
            function_url,
            headers={
                "Authorization": f"Bearer {secret_key}",
                "Content-Type": "application/json"
            },
            json={
                "subjectCode": subject_code,
                "questions": questions
            },
            timeout=300  # 5 минут таймаут
        )
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ {result.get('message', 'Загрузка завершена')}")
            print(f"   Загружено: {result.get('uploaded', 0)}")
            print(f"   Ошибок: {result.get('errors', 0)}")
            return result
        else:
            error = response.json() if response.content else {"error": "Unknown error"}
            print(f"❌ Ошибка {response.status_code}: {error.get('error', 'Unknown error')}")
            return error
            
    except requests.exceptions.Timeout:
        print("❌ Превышено время ожидания (таймаут 5 минут)")
        return {"error": "Timeout"}
    except requests.exceptions.RequestException as e:
        print(f"❌ Ошибка запроса: {e}")
        return {"error": str(e)}

def main():
    parser = argparse.ArgumentParser(
        description="Загрузка вопросов через Firebase Cloud Functions"
    )
    parser.add_argument(
        "--json",
        required=True,
        help="Путь к JSON файлу с вопросами"
    )
    parser.add_argument(
        "--subject",
        required=True,
        choices=list(SUBJECT_CODES.keys()),
        help="Код предмета"
    )
    parser.add_argument(
        "--function-url",
        default=os.getenv("CLOUD_FUNCTION_URL"),
        help="URL Cloud Function (или установите CLOUD_FUNCTION_URL)"
    )
    parser.add_argument(
        "--secret-key",
        default=os.getenv("CLOUD_FUNCTION_SECRET"),
        help="Секретный ключ (или установите CLOUD_FUNCTION_SECRET)"
    )
    
    args = parser.parse_args()
    
    # Проверка обязательных параметров
    if not args.function_url:
        print("❌ Ошибка: Не указан URL Cloud Function!")
        print("   Используйте --function-url или установите переменную окружения CLOUD_FUNCTION_URL")
        print("\n   Пример:")
        print("   export CLOUD_FUNCTION_URL='https://YOUR_REGION-egog-771fc.cloudfunctions.net/uploadQuestions'")
        return
    
    if not args.secret_key:
        print("❌ Ошибка: Не указан секретный ключ!")
        print("   Используйте --secret-key или установите переменную окружения CLOUD_FUNCTION_SECRET")
        print("\n   Пример:")
        print("   export CLOUD_FUNCTION_SECRET='your_secret_key_here'")
        return
    
    # Загрузка вопросов из JSON
    try:
        questions = load_questions_from_json(args.json)
        print(f"✅ Загружено {len(questions)} вопросов из {args.json}")
    except Exception as e:
        print(f"❌ Ошибка загрузки JSON: {e}")
        return
    
    # Загрузка через Cloud Function
    result = upload_via_cloud_function(
        questions,
        args.subject,
        args.function_url,
        args.secret_key
    )
    
    if result.get("success"):
        print(f"\n🎉 Успешно загружено {result.get('uploaded', 0)} вопросов для {SUBJECT_CODES[args.subject]}")
    else:
        print(f"\n❌ Загрузка не удалась: {result.get('error', 'Unknown error')}")

if __name__ == "__main__":
    main()

