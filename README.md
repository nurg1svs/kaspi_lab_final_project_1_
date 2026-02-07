# 📁 File Uploader Service

Асинхронный сервис для загрузки файлов с поддержкой **идемпотентности** и хранением в **MinIO**.

---

## 📋 Описание

Сервис предоставляет **REST API** для:

- Асинхронной загрузки файлов в объектное хранилище **MinIO**
- Отслеживания статуса загрузки файлов
- Защиты от дубликатов с помощью **Idempotency Key**
- Получения информации о загруженных файлах

---

## 🛠 Технологии

- **Java 21**
- **Spring Boot 3.2.2**
- **PostgreSQL** — хранение метаданных файлов
- **MinIO** — объектное хранилище
- **Gradle** — система сборки
- **Swagger / OpenAPI** — документация API

---

## 📦 Требования (Prerequisites)

Перед запуском убедитесь, что установлены:

### Java 21+
```bash
   java -version
```

## PostgreSQL 14+
```
psql --version
```

## Docker (для MinIO)
```
docker --version
```

## Gradle или используйте встроенный gradlew


## Установка и запуск
🔹 Шаг 1: Клонирование репозитория
```
git clone <repository-url>
cd kaspi_lab_final_project_1/fileUploaderService
```

🔹 Шаг 2: Настройка PostgreSQL

Создайте базу данных:
```
psql -U postgres
```
В psql выполните:
```
CREATE DATABASE "fileStorage";
CREATE USER nurgisaserikkali WITH PASSWORD '123456';
GRANT ALL PRIVILEGES ON DATABASE "fileStorage" TO nurgisaserikkali;
```

Выход:
```
\q
```

🔁 Альтернатива

Если используете другие креды — измените application.yml:
```
spring:
datasource:
url: jdbc:postgresql://localhost:5432/fileStorage
username: YOUR_USERNAME
password: YOUR_PASSWORD
```

🔹 Шаг 3: Запуск MinIO через Docker
Через Docker Compose
```
docker-compose up -d
```
Или вручную
```
docker run -d \
--name file_uploader_minio \
-p 9000:9000 \
-p 9001:9001 \
-e MINIO_ROOT_USER=minioadmin \
-e MINIO_ROOT_PASSWORD=minioadmin \
minio/minio:latest \
server /data --console-address ":9001"
```

Проверка:
```
docker ps | grep minio
```

## 📌 MinIO Console

URL: http://localhost:9001
```
Login: minioadmin
Password: minioadmin
```
📝 Bucket uploaded-files создаётся автоматически при первом запуске приложения

🔹 Шаг 4: Проверка конфигурации
```
src/main/resources/application.yml:
```
```
spring:
datasource:
url: jdbc:postgresql://localhost:5432/fileStorage
username: nurgisaserikkali
password: 123456

server:
port: 8030

minio:
url: http://localhost:9000
access-key: minioadmin
secret-key: minioadmin
bucket: uploaded-files

async:
executor:
core-pool-size: 5
max-pool-size: 10
queue-capacity: 100
```

🔹 Шаг 5: Сборка проекта
```
# Linux / Mac
./gradlew clean build

# Windows
gradlew.bat clean build
```
🔹 Шаг 6: Запуск приложения
Вариант 1: Gradle
```
./gradlew bootRun
```
Вариант 2: Java
```
java -jar build/libs/fileUploaderService-0.0.1-SNAPSHOT.jar
```
Вариант 3: IntelliJ IDEA
```
Откройте проект

Найдите FileUploaderServiceApplication.java

Run
```
🔹 Шаг 7: Проверка запуска
```
curl http://localhost:8030/actuator/health


Ответ:

{"status":"UP"}
```

📚 Документация API (Swagger)
```
Swagger UI: http://localhost:8030/swagger

OpenAPI: http://localhost:8030/v3/api-docs
```
## 🧪 Тестирование API
1️⃣ Загрузка файла
```
curl -X POST http://localhost:8030/files/upload \
-H "X-Client-Id: client_123" \
-H "Idempotency-Key: upload_001" \
-F "file=@/path/to/your/file.pdf"


Ответ:

{
"fileId": 1,
"status": "UPLOADING"
}
```

2️⃣ Проверка статуса файла
```
curl http://localhost:8030/files/1

response: 

{
"fileId": 1,
"fileName": "file.pdf",
"clientId": "client_123",
"status": "STORED",
"storagePath": "uuid_file.pdf",
"createdAt": "2026-02-07T23:17:00"
}
```
3️⃣ Получение всех файлов
```
curl http://localhost:8030/files
```
4️⃣ Тест идемпотентности
```
curl -X POST http://localhost:8030/files/upload \
-H "X-Client-Id: client_123" \
-H "Idempotency-Key: upload_001" \
-F "file=@/path/to/your/file.pdf"

```
➡️ Вернётся тот же fileId, дубликат не создаётся.


Логи
```
INFO AsyncFileProcessor - Async processing started, fileId=1
INFO AsyncFileProcessor - DB updated to STORED, fileId=1
```
🗄 Проверка БД
```
psql -U nurgisaserikkali -d fileStorage

SELECT * FROM files;
SELECT * FROM files WHERE status = 'STORED';
```
📁 Структура проекта
```
fileUploaderService/
├── src/
│   ├── main/
│   │   ├── java/kz/lab/fileuploaderservice/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── processor/
│   │   │   ├── db/
│   │   │   ├── dto/
│   │   │   └── mapper/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── build.gradle
├── compose.yml
└── README.md
```
📝 Статусы файлов

```
UPLOADING — файл принят

STORED — успешно сохранён

FAILED — ошибка загрузки
```