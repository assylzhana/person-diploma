# Person Productivity System

Микросервисная система личной продуктивности и саморазвития.

## Архитектура

```
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway :8080                        │
│          (JWT validation + X-User-Id header injection)       │
└──────┬──────┬──────┬──────┬──────┬──────────────────────────┘
       │      │      │      │      │
    :8081  :8082  :8083  :8084  :8085  :8086
     auth  user  goal finance analytics notification
       │      │      │      │           │
       └──────┴──────┴──────┴───────────┘
                  PostgreSQL :5432
          (auth_db / user_db / goal_db /
           finance_db / notification_db)
```

**Межсервисная коммуникация:** синхронная через HTTP (Spring Cloud OpenFeign). Kafka/RabbitMQ **не используется**.

---

## Порты

| Сервис               | Порт | База данных      |
|----------------------|------|-----------------|
| api-gateway          | 8080 | —               |
| auth-service         | 8081 | auth_db          |
| user-service         | 8082 | user_db          |
| goal-service         | 8083 | goal_db          |
| finance-service      | 8084 | finance_db       |
| analytics-service    | 8085 | — (агрегация)    |
| notification-service | 8086 | notification_db  |
| PostgreSQL           | 5432 | —               |

---

## Технологии

| Технология                    | Версия      | Назначение                              |
|-------------------------------|-------------|----------------------------------------|
| Java                          | 21          | Основной язык                          |
| Spring Boot                   | 3.3.5       | Фреймворк                              |
| Spring Cloud                  | 2023.0.3    | Gateway, OpenFeign                     |
| Spring Cloud Gateway          | —           | API Gateway (reactive/WebFlux)         |
| Spring Security               | —           | BCrypt пароли                          |
| Spring Data JPA               | —           | ORM                                    |
| Spring Validation             | —           | Валидация DTO                          |
| Spring Cloud OpenFeign        | —           | HTTP-вызовы между сервисами            |
| PostgreSQL                    | 16          | Реляционная БД                         |
| Flyway                        | —           | Миграции БД                            |
| JJWT                          | 0.12.5      | JWT access/refresh токены              |
| Lombok                        | 1.18.34     | Сокращение boilerplate кода            |
| MapStruct                     | 1.5.5.Final | Маппинг DTO ↔ Entity                   |
| SpringDoc OpenAPI             | 2.5.0       | Swagger UI / OpenAPI 3                 |
| Docker Compose                | —           | Оркестрация контейнеров                |
| Maven                         | 3.9+        | Сборка (multi-module)                  |
| **Kafka / RabbitMQ**          | **НЕТ**     | **Не используется**                    |

> Уведомления отправляются синхронно: goal-service → notification-service через Feign HTTP-вызов.

---

## Запуск

### Требования

- Java 21
- Maven 3.9+
- Docker & Docker Compose

### Docker Compose (рекомендуется)

```bash
# 1. Сборка всех модулей
mvn clean package -DskipTests

# 2. Запуск всей инфраструктуры
docker-compose up --build

# 3. Проверка (должен вернуть 401 без токена)
curl -i http://localhost:8080/goals
```

Первый запуск занимает ~2-3 минуты. PostgreSQL поднимается первым, затем сервисы.

### Локальная разработка (без Docker Compose)

```bash
# 1. Запустить только PostgreSQL
docker run -d \
  --name person-postgres \
  -e POSTGRES_USER=person \
  -e POSTGRES_PASSWORD=person123 \
  -p 5432:5432 \
  postgres:16-alpine

# 2. Создать базы данных
docker exec -it person-postgres psql -U person -c "CREATE DATABASE auth_db;"
docker exec -it person-postgres psql -U person -c "CREATE DATABASE user_db;"
docker exec -it person-postgres psql -U person -c "CREATE DATABASE goal_db;"
docker exec -it person-postgres psql -U person -c "CREATE DATABASE finance_db;"
docker exec -it person-postgres psql -U person -c "CREATE DATABASE notification_db;"

# 3. Установить переменную окружения JWT_SECRET
export JWT_SECRET=your-super-secret-key-at-least-32-characters-long

# 4. Запустить каждый сервис в отдельном терминале
cd auth-service        && mvn spring-boot:run
cd user-service        && mvn spring-boot:run
cd goal-service        && mvn spring-boot:run
cd finance-service     && mvn spring-boot:run
cd analytics-service   && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway         && mvn spring-boot:run
```

---

## Аутентификация

Все запросы (кроме `/auth/register`, `/auth/login`, `/auth/refresh`) требуют заголовок:

```
Authorization: Bearer <access_token>
```

Gateway извлекает `userId` из JWT и добавляет заголовок `X-User-Id` к запросам в downstream-сервисы.

---

## REST API

### Быстрый старт — пример сценария

```bash
# 1. Регистрация
curl -s -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Asylzhan","lastName":"Kabibulla","email":"user@example.com","password":"secret123"}'

# 2. Логин — получить токены
RESPONSE=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret123"}')

TOKEN=$(echo $RESPONSE | jq -r '.accessToken')

# 3. Использовать токен
curl http://localhost:8080/users/me -H "Authorization: Bearer $TOKEN"
```

---

### Auth Service (`/auth`) — порт 8081

#### POST `/auth/register` — Регистрация
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Asylzhan",
    "lastName": "Kabibulla",
    "email": "user@example.com",
    "password": "secret123"
  }'
```
**Ответ 200:**
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci..."
}
```

#### POST `/auth/login` — Вход
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "secret123"
  }'
```
**Ответ 200:**
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci..."
}
```

#### POST `/auth/refresh` — Обновление токена
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGci..."
  }'
```
**Ответ 200:**
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci..."
}
```

#### POST `/auth/logout` — Выход
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** `logged out`

#### GET `/auth/validate` — Валидация токена
```bash
curl "http://localhost:8080/auth/validate?token=$TOKEN"
```
**Ответ 200:** `true` или `false`

---

### User Service (`/users`) — порт 8082

#### GET `/users/me` — Мой профиль
```bash
curl http://localhost:8080/users/me \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
{
  "id": 1,
  "firstName": "Asylzhan",
  "lastName": "Kabibulla",
  "email": "user@example.com",
  "bio": null,
  "avatarUrl": null,
  "friendCount": 0,
  "createdAt": "2026-04-25T10:00:00"
}
```

#### PUT `/users/me` — Обновить профиль
```bash
curl -X PUT http://localhost:8080/users/me \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Asylzhan",
    "lastName": "Kabibulla",
    "bio": "Backend developer",
    "avatarUrl": "https://example.com/avatar.jpg"
  }'
```
**Ответ 200:** обновлённый профиль (см. GET `/users/me`)

#### GET `/users` — Все публичные пользователи
```bash
curl http://localhost:8080/users \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
[
  { "id": 1, "firstName": "Asylzhan", "lastName": "Kabibulla", "bio": null, "avatarUrl": null }
]
```

#### POST `/users/friends/request/{id}` — Отправить заявку в друзья
```bash
curl -X POST http://localhost:8080/users/friends/request/2 \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** `friend request sent`

#### PUT `/users/friends/{id}/accept` — Принять заявку
```bash
curl -X PUT http://localhost:8080/users/friends/2/accept \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** `friend request accepted`

#### GET `/users/friends` — Список друзей
```bash
curl http://localhost:8080/users/friends \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
[
  { "id": 2, "firstName": "Ali", "lastName": "Bekov", "bio": null, "avatarUrl": null }
]
```

#### GET `/users/tests` — Психологические тесты
```bash
curl http://localhost:8080/users/tests \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
[
  {
    "id": 1,
    "title": "Stress & Productivity Assessment",
    "description": "Assess your current stress level and productivity",
    "questions": [
      {
        "id": 1,
        "text": "How often do you feel overwhelmed by your workload?",
        "options": [
          { "id": 1, "text": "Never", "stressScore": 10, "motivationScore": 80, "productivityScore": 85 },
          { "id": 2, "text": "Rarely", "stressScore": 25, "motivationScore": 65, "productivityScore": 70 }
        ]
      }
    ]
  }
]
```

#### POST `/users/tests/take` — Пройти тест
```bash
curl -X POST http://localhost:8080/users/tests/take \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "testId": 1,
    "answers": [
      { "questionId": 1, "selectedOptionId": 2 },
      { "questionId": 2, "selectedOptionId": 5 }
    ]
  }'
```
**Ответ 200:**
```json
{
  "id": 1,
  "testTitle": "Stress & Productivity Assessment",
  "stressScore": 45,
  "motivationScore": 60,
  "productivityScore": 65,
  "recommendations": ["Take regular breaks", "Prioritize tasks"],
  "takenAt": "2026-04-25T10:30:00"
}
```

#### GET `/users/tests/results` — Мои результаты
```bash
curl http://localhost:8080/users/tests/results \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** массив результатов (см. POST `/users/tests/take`)

---

### Goal Service (`/goals`) — порт 8083

Доступные значения enum:
- `category`: `HEALTH`, `EDUCATION`, `FINANCE`, `CAREER`, `PERSONAL`, `SOCIAL`, `OTHER`
- `periodType`: `DAILY`, `WEEKLY`, `MONTHLY`, `YEARLY`, `CUSTOM`
- `status`: `ACTIVE`, `COMPLETED`, `FAILED`

#### POST `/goals` — Создать цель
```bash
curl -X POST http://localhost:8080/goals \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot",
    "description": "Complete the microservices course",
    "category": "EDUCATION",
    "periodType": "MONTHLY",
    "deadline": "2026-05-31"
  }'
```
**Ответ 200:**
```json
{
  "id": 1,
  "title": "Learn Spring Boot",
  "description": "Complete the microservices course",
  "category": "EDUCATION",
  "periodType": "MONTHLY",
  "status": "ACTIVE",
  "deadline": "2026-05-31",
  "createdAt": "2026-04-25T10:00:00",
  "completedAt": null
}
```

#### PUT `/goals/{id}` — Обновить цель
```bash
curl -X PUT http://localhost:8080/goals/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot Advanced",
    "description": "Deep dive into reactive programming",
    "category": "EDUCATION",
    "periodType": "MONTHLY",
    "deadline": "2026-06-30"
  }'
```
**Ответ 200:** обновлённая цель

#### PATCH `/goals/{id}/complete` — Завершить цель
```bash
curl -X PATCH http://localhost:8080/goals/1/complete \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** цель со статусом `COMPLETED`

#### DELETE `/goals/{id}` — Удалить цель
```bash
curl -X DELETE http://localhost:8080/goals/1 \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 204:** No Content

#### GET `/goals` — Список целей (с фильтрами)
```bash
# Все цели
curl http://localhost:8080/goals \
  -H "Authorization: Bearer $TOKEN"

# С фильтром по статусу и категории
curl "http://localhost:8080/goals?status=ACTIVE&category=EDUCATION" \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** массив целей

#### GET `/goals/stats` — Статистика по целям
```bash
curl http://localhost:8080/goals/stats \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
{
  "total": 10,
  "active": 5,
  "completed": 4,
  "failed": 1,
  "completionRate": 40.0
}
```

---

### Finance Service (`/finance`) — порт 8084

#### POST `/finance/monthly` — Создать месячный план
```bash
curl -X POST http://localhost:8080/finance/monthly \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "month": 4,
    "year": 2026,
    "plannedIncome": 500000.00,
    "plannedExpenses": 350000.00
  }'
```
**Ответ 200:**
```json
{
  "id": 1,
  "month": 4,
  "year": 2026,
  "plannedIncome": 500000.00,
  "plannedExpenses": 350000.00,
  "actualIncome": 0.00,
  "actualExpenses": 0.00,
  "balance": 0.00,
  "createdAt": "2026-04-25T10:00:00"
}
```

#### GET `/finance/monthly/current` — Текущий месяц
```bash
curl http://localhost:8080/finance/monthly/current \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** месячный план (см. выше)

#### POST `/finance/monthly/{id}/income` — Добавить доход
```bash
curl -X POST http://localhost:8080/finance/monthly/1/income \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 300000.00,
    "description": "Salary",
    "date": "2026-04-25"
  }'
```
**Ответ 200:** обновлённый месячный план

#### POST `/finance/monthly/{id}/expense` — Добавить расход
```bash
curl -X POST http://localhost:8080/finance/monthly/1/expense \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50000.00,
    "description": "Groceries",
    "date": "2026-04-25"
  }'
```
**Ответ 200:** обновлённый месячный план

#### GET `/finance/stats` — Финансовая статистика
```bash
curl http://localhost:8080/finance/stats \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
{
  "totalIncome": 300000.00,
  "totalExpenses": 50000.00,
  "balance": 250000.00,
  "savingsRate": 83.3,
  "isOverBudget": false
}
```

---

### Analytics Service (`/analytics`) — порт 8085

> Сервис не имеет собственной БД. Агрегирует данные из goal-service, finance-service и user-service через Feign HTTP.

#### GET `/analytics/overview` — Полный обзор
```bash
curl http://localhost:8080/analytics/overview \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
{
  "userId": 1,
  "developmentLevel": "INTERMEDIATE",
  "goalStats": {
    "total": 10, "active": 5, "completed": 4, "failed": 1, "completionRate": 40.0
  },
  "financeStats": {
    "totalIncome": 300000.00, "totalExpenses": 50000.00, "balance": 250000.00,
    "savingsRate": 83.3, "isOverBudget": false
  },
  "testStats": {
    "testsCount": 2, "avgStressScore": 45.0, "avgMotivationScore": 60.0, "avgProductivityScore": 65.0
  },
  "recommendations": [
    "Keep maintaining your budget discipline!",
    "Great job completing your goals!"
  ]
}
```

> `developmentLevel`: `STARTER` → `BEGINNER` → `INTERMEDIATE` → `ADVANCED` → `ELITE`

#### GET `/analytics/goals` — Аналитика целей
```bash
curl http://localhost:8080/analytics/goals \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** блок `goalStats` из overview

#### GET `/analytics/finance` — Финансовая аналитика
```bash
curl http://localhost:8080/analytics/finance \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** блок `financeStats` из overview

#### GET `/analytics/productivity` — Аналитика продуктивности
```bash
curl http://localhost:8080/analytics/productivity \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** блок `testStats` из overview

#### GET `/analytics/recommendations` — AI-рекомендации
```bash
curl http://localhost:8080/analytics/recommendations \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
{
  "recommendations": [
    "You have too many failed goals. Try breaking them into smaller steps.",
    "Your stress level is high. Consider mindfulness or exercise."
  ]
}
```

> Рекомендации основаны на правилах (rule-based): кол-во провалённых целей, превышение бюджета, уровни стресса/продуктивности. Интерфейс `AiRecommendationClient` позволяет заменить на реальный AI API.

---

### Notification Service (`/notifications`) — порт 8086

Уведомления создаются автоматически при:
- приближении дедлайна цели (за 1 день, ежедневно в 09:00)
- просрочке цели (goal → статус FAILED)

#### GET `/notifications` — Все уведомления
```bash
curl http://localhost:8080/notifications \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
[
  {
    "id": 1,
    "type": "GOAL_DEADLINE",
    "title": "Goal Deadline Tomorrow",
    "message": "Your goal 'Learn Spring Boot' is due tomorrow!",
    "isRead": false,
    "createdAt": "2026-04-25T09:00:00"
  }
]
```

Типы уведомлений: `GOAL_DEADLINE`, `GOAL_EXPIRED`, `GOAL_COMPLETED`, `SYSTEM`

#### GET `/notifications/unread` — Непрочитанные уведомления
```bash
curl http://localhost:8080/notifications/unread \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** массив непрочитанных уведомлений

#### GET `/notifications/unread/count` — Количество непрочитанных
```bash
curl http://localhost:8080/notifications/unread/count \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:**
```json
{ "count": 3 }
```

#### PATCH `/notifications/{id}/read` — Отметить прочитанным
```bash
curl -X PATCH http://localhost:8080/notifications/1/read \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** обновлённое уведомление

#### PATCH `/notifications/read-all` — Прочитать все
```bash
curl -X PATCH http://localhost:8080/notifications/read-all \
  -H "Authorization: Bearer $TOKEN"
```
**Ответ 200:** `all notifications marked as read`

---

## Swagger UI

| Сервис               | URL                                  |
|----------------------|--------------------------------------|
| Auth Service         | http://localhost:8081/swagger-ui.html |
| User Service         | http://localhost:8082/swagger-ui.html |
| Goal Service         | http://localhost:8083/swagger-ui.html |
| Finance Service      | http://localhost:8084/swagger-ui.html |
| Analytics Service    | http://localhost:8085/swagger-ui.html |
| Notification Service | http://localhost:8086/swagger-ui.html |

---

## Переменные окружения

| Переменная                  | Default                                     | Описание                        |
|-----------------------------|---------------------------------------------|---------------------------------|
| `JWT_SECRET`                | *обязательно*                               | Секрет для JWT (min 32 символа) |
| `JWT_ACCESS_EXPIRATION`     | `900000` (15 мин)                           | Access token TTL (мс)           |
| `JWT_REFRESH_EXPIRATION`    | `604800000` (7 дней)                        | Refresh token TTL (мс)          |
| `SPRING_DATASOURCE_URL`     | `jdbc:postgresql://localhost:5432/<service_db>` | URL PostgreSQL               |
| `SPRING_DATASOURCE_USERNAME`| `person`                                    | Пользователь PostgreSQL         |
| `SPRING_DATASOURCE_PASSWORD`| `person123`                                 | Пароль PostgreSQL               |
| `USER_SERVICE_URL`          | `http://localhost:8082`                     | URL user-service (для auth)     |
| `GOAL_SERVICE_URL`          | `http://localhost:8083`                     | URL goal-service (для analytics)|
| `FINANCE_SERVICE_URL`       | `http://localhost:8084`                     | URL finance-service             |
| `USER_SERVICE_INTERNAL_URL` | `http://localhost:8082`                     | URL user-service (для analytics)|
| `NOTIFICATION_SERVICE_URL`  | `http://localhost:8086`                     | URL notification-service        |

В Docker Compose все URL настроены автоматически через имена контейнеров.

---

## Планировщик задач

Goal Service запускает `GoalScheduler` ежедневно в **09:00**:
- Находит цели с дедлайном **завтра** → отправляет уведомление `GOAL_DEADLINE`
- Находит **просроченные** активные цели → меняет статус на `FAILED`, отправляет уведомление `GOAL_EXPIRED`
