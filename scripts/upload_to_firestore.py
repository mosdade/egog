#!/usr/bin/env python3
"""
Скрипт для загрузки вопросов из JSON файлов в Firebase Firestore.

Использование:
    python scripts/upload_to_firestore.py --json path/to/questions.json --subject math_ege_profil

Требования:
    pip install firebase-admin
"""

import json
import argparse
import firebase_admin
from firebase_admin import credentials, firestore
from typing import Dict, List, Any
import os

# Коды предметов
SUBJECT_CODES = {
    "math_ege_profil": "Математика (профиль) ЕГЭ",
    "math_ege_base": "Математика (база) ЕГЭ",
    "math_oge": "Математика ОГЭ",
    "eng_ege": "Английский язык ЕГЭ",
    "eng_oge": "Английский язык ОГЭ"
}

def determine_subject_code(question: Dict[str, Any], default_subject: str = None) -> str:
    """
    Определяет код предмета на основе данных вопроса.
    
    Args:
        question: Словарь с данными вопроса
        default_subject: Код предмета по умолчанию
    
    Returns:
        Код предмета
    """
    # Если указан subjectCode, используем его
    if "subjectCode" in question and question["subjectCode"]:
        return question["subjectCode"]
    
    # Если указан examType, определяем по нему
    exam_type = question.get("examType", "").lower()
    
    # Пытаемся определить по id или другим полям
    question_id = question.get("id", "").upper()
    
    # Определение по examType и другим признакам
    if exam_type == "ege":
        # По умолчанию для ЕГЭ - математика профиль
        return default_subject or "math_ege_profil"
    elif exam_type == "oge":
        # По умолчанию для ОГЭ - математика
        return default_subject or "math_oge"
    
    # Если не удалось определить, используем значение по умолчанию
    return default_subject or "math_ege_profil"

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
        # Ищем ключи, которые могут содержать вопросы
        for key in ['questions', 'data', 'items']:
            if key in data and isinstance(data[key], list):
                return data[key]
    
    raise ValueError(f"Не удалось найти список вопросов в файле {json_path}")

def prepare_question_for_firestore(question: Dict[str, Any], subject_code: str) -> Dict[str, Any]:
    """
    Подготавливает вопрос для загрузки в Firestore.
    
    Args:
        question: Словарь с данными вопроса
        subject_code: Код предмета
    
    Returns:
        Подготовленный словарь для Firestore
    """
    # Определяем subjectCode
    final_subject_code = determine_subject_code(question, subject_code)
    
    # Подготавливаем данные
    firestore_question = {
        "id": question.get("id", ""),
        "guid": question.get("guid"),
        "hint": question.get("hint", ""),
        "codifier": question.get("codifier", []) or [],
        "question": question.get("question", ""),
        "problem": question.get("problem", ""),
        "img": question.get("img", []) or [],
        "imgUrls": question.get("imgUrls", []) or [],
        "audioUrls": question.get("audioUrls", []) or [],
        "numberInGroup": question.get("numberInGroup", ""),
        "answerType": question.get("answerType", ""),
        "answer": question.get("answer", ""),
        "subjectCode": final_subject_code  # Обязательное поле!
    }
    
    # Удаляем None значения
    return {k: v for k, v in firestore_question.items() if v is not None}

def upload_questions_to_firestore(
    questions: List[Dict[str, Any]],
    subject_code: str,
    batch_size: int = 500
):
    """
    Загружает вопросы в Firestore.
    
    Args:
        questions: Список вопросов
        subject_code: Код предмета по умолчанию
        batch_size: Размер батча для загрузки
    """
    db = firestore.client()
    
    print(f"Начинаю загрузку {len(questions)} вопросов в Firestore...")
    
    # Загружаем батчами
    for i in range(0, len(questions), batch_size):
        batch = db.batch()
        batch_questions = questions[i:i + batch_size]
        
        for question in batch_questions:
            try:
                prepared_question = prepare_question_for_firestore(question, subject_code)
                
                # Используем id вопроса как document ID (если есть)
                doc_id = prepared_question.get("id")
                if not doc_id:
                    # Если нет id, генерируем уникальный ID
                    import uuid
                    doc_id = str(uuid.uuid4())
                
                # Создаем ссылку на документ
                doc_ref = db.collection("questions").document(doc_id)
                batch.set(doc_ref, prepared_question, merge=True)
                
            except Exception as e:
                print(f"Ошибка при подготовке вопроса {question.get('id', 'unknown')}: {e}")
                continue
        
        # Отправляем батч
        try:
            batch.commit()
            print(f"Загружено {min(i + batch_size, len(questions))} из {len(questions)} вопросов")
        except Exception as e:
            print(f"Ошибка при загрузке батча {i}-{i + batch_size}: {e}")
    
    print(f"✅ Загрузка завершена! Всего загружено {len(questions)} вопросов")

def main():
    parser = argparse.ArgumentParser(
        description="Загрузка вопросов из JSON в Firebase Firestore"
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
        "--credentials",
        default="serviceAccountKey.json",
        help="Путь к файлу с учетными данными Firebase (serviceAccountKey.json)"
    )
    parser.add_argument(
        "--batch-size",
        type=int,
        default=500,
        help="Размер батча для загрузки (по умолчанию 500)"
    )
    
    args = parser.parse_args()
    
    # Проверяем наличие файла с учетными данными
    if not os.path.exists(args.credentials):
        print(f"❌ Ошибка: Файл {args.credentials} не найден!")
        print("\nДля получения serviceAccountKey.json:")
        print("1. Откройте Firebase Console: https://console.firebase.google.com/")
        print("2. Выберите проект egog-771fc")
        print("3. Перейдите в Project Settings > Service Accounts")
        print("4. Нажмите 'Generate new private key'")
        print("5. Сохраните файл как serviceAccountKey.json")
        return
    
    # Инициализация Firebase
    try:
        cred = credentials.Certificate(args.credentials)
        firebase_admin.initialize_app(cred)
        print("✅ Firebase инициализирован")
    except Exception as e:
        print(f"❌ Ошибка инициализации Firebase: {e}")
        return
    
    # Загрузка вопросов из JSON
    try:
        questions = load_questions_from_json(args.json)
        print(f"✅ Загружено {len(questions)} вопросов из {args.json}")
    except Exception as e:
        print(f"❌ Ошибка загрузки JSON: {e}")
        return
    
    # Загрузка в Firestore
    try:
        upload_questions_to_firestore(questions, args.subject, args.batch_size)
    except Exception as e:
        print(f"❌ Ошибка загрузки в Firestore: {e}")
        return

if __name__ == "__main__":
    main()

