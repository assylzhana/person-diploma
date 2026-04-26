# Person API — Техническая документация

Полная спецификация всех REST API: обязательные/опциональные поля, типы данных, валидация, коды ответов.

**Базовый URL:** `http://localhost:8080`  
**Аутентификация:** `Authorization: Bearer <accessToken>` (все эндпоинты кроме `/auth/register`, `/auth/login`, `/auth/refresh`)

---

## Соглашения

| Обозначение | Смысл |
|-------------|-------|
| **жирный** | обязательное поле |
| *курсив* | опциональное поле |
| `[enum]` | одно из перечисленных значений |

---

## Auth Service

### POST `/auth/register`

Регистрация нового пользователя. Авторизация **не требуется**.

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **firstName** | string | да | не пустой |
| **lastName** | string | да | не пустой |
| **email** | string | да | не пустой, валидный email |
| **password** | string | да | не пустой, min 6 символов |

```json
{
  "firstName": "Asylzhan",
  "lastName": "Kabibulla",
  "email": "user@example.com",
  "password": "secret123"
}
```

**Response `201 Created`**

| Поле | Тип | Описание |
|------|-----|---------|
| userId | number | ID созданного пользователя |
| email | string | Email пользователя |
| accessToken | string | JWT access token (15 мин) |
| refreshToken | string | JWT refresh token (7 дней) |
| tokenType | string | Всегда `"Bearer"` |

```json
{
  "userId": 1,
  "email": "user@example.com",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

**Ошибки**

| Код | Причина |
|-----|---------|
| 400 | Невалидное тело запроса |
| 409 | Email уже зарегистрирован |

---

### POST `/auth/login`

Вход в систему. Авторизация **не требуется**.

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **email** | string | да | не пустой, валидный email |
| **password** | string | да | не пустой |

```json
{
  "email": "user@example.com",
  "password": "secret123"
}
```

**Response `200 OK`** — идентичен `/auth/register`

**Ошибки**

| Код | Причина |
|-----|---------|
| 400 | Невалидное тело |
| 401 | Неверный email или пароль |

---

### POST `/auth/refresh`

Обновление токенов по refresh token. Авторизация **не требуется**.  
Старый refresh token аннулируется, выдаётся новая пара.

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **refreshToken** | string | да | не пустой |

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `200 OK`** — идентичен `/auth/register`

**Ошибки**

| Код | Причина |
|-----|---------|
| 401 | Refresh token недействителен или истёк |

---

### POST `/auth/logout`

Выход — аннулирует refresh token.

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **refreshToken** | string | да | не пустой |

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `204 No Content`** — тело пустое

---

### GET `/auth/validate`

Проверить валидность токена.

**Query Parameters**

| Параметр | Тип | Обязательное | Описание |
|----------|-----|:---:|---------|
| **token** | string | да | JWT токен |

```
GET /auth/validate?token=eyJhbGciOiJIUzI1NiJ9...
```

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| valid | boolean | `true` если токен действителен |
| userId | number | null если токен невалиден |
| email | string | null если токен невалиден |

```json
{
  "valid": true,
  "userId": 1,
  "email": "user@example.com"
}
```

---

## User Service

### GET `/users/me`

Получить профиль текущего пользователя.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет  
**Query Params:** нет

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| id | number | ID профиля |
| userId | number | ID пользователя (из auth-service) |
| firstName | string | Имя |
| lastName | string | Фамилия |
| email | string | Email |
| bio | string \| null | Биография |
| avatarUrl | string \| null | URL аватара |
| privacyType | string | `PUBLIC` / `PRIVATE` |
| createdAt | datetime | Дата создания (ISO 8601) |

```json
{
  "id": 1,
  "userId": 1,
  "firstName": "Asylzhan",
  "lastName": "Kabibulla",
  "email": "user@example.com",
  "bio": null,
  "avatarUrl": null,
  "privacyType": "PUBLIC",
  "createdAt": "2026-04-25T10:00:00"
}
```

---

### PUT `/users/me`

Обновить профиль текущего пользователя.

**Headers:** `Authorization: Bearer <token>`

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **firstName** | string | да | не пустой |
| **lastName** | string | да | не пустой |
| *bio* | string | нет | — |
| *avatarUrl* | string | нет | — |
| *privacyType* | string | нет | `PUBLIC` / `PRIVATE` |

```json
{
  "firstName": "Asylzhan",
  "lastName": "Kabibulla",
  "bio": "Backend developer",
  "avatarUrl": "https://example.com/avatar.jpg",
  "privacyType": "PUBLIC"
}
```

**Response `200 OK`** — обновлённый профиль (идентично GET `/users/me`)

---

### GET `/users`

Список всех публичных профилей.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет  
**Query Params:** нет

**Response `200 OK`** — массив профилей

```json
[
  {
    "id": 1,
    "userId": 1,
    "firstName": "Asylzhan",
    "lastName": "Kabibulla",
    "email": "user@example.com",
    "bio": null,
    "avatarUrl": null,
    "privacyType": "PUBLIC",
    "createdAt": "2026-04-25T10:00:00"
  }
]
```

---

### GET `/users/{userId}`

Получить профиль конкретного пользователя.

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **userId** | number | ID пользователя |

**Response `200 OK`** — профиль пользователя (идентично GET `/users/me`)

**Ошибки**

| Код | Причина |
|-----|---------|
| 404 | Пользователь не найден |

---

### POST `/users/friends/request/{addresseeId}`

Отправить заявку в друзья.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **addresseeId** | number | ID пользователя, которому отправляется заявка |

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| id | number | ID записи дружбы |
| requesterId | number | ID инициатора |
| addresseeId | number | ID адресата |
| status | string | `PENDING` |
| friend | object | Профиль адресата |
| createdAt | datetime | Дата создания |

```json
{
  "id": 1,
  "requesterId": 1,
  "addresseeId": 2,
  "status": "PENDING",
  "friend": {
    "id": 2,
    "firstName": "Ali",
    "lastName": "Bekov"
  },
  "createdAt": "2026-04-25T10:00:00"
}
```

**Ошибки**

| Код | Причина |
|-----|---------|
| 404 | Адресат не найден |
| 409 | Заявка уже существует |

---

### PUT `/users/friends/{friendshipId}/accept`

Принять заявку в друзья.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **friendshipId** | number | ID записи дружбы (из заявки) |

**Response `200 OK`** — запись дружбы со статусом `ACCEPTED`

**Ошибки**

| Код | Причина |
|-----|---------|
| 403 | Нельзя принять чужую заявку |
| 404 | Заявка не найдена |

---

### DELETE `/users/friends/{friendId}`

Удалить из друзей.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **friendId** | number | ID пользователя (не дружбы) |

**Response `204 No Content`**

---

### GET `/users/friends`

Список друзей текущего пользователя.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`** — массив записей дружбы (статус `ACCEPTED`)

---

### GET `/users/friends/pending`

Входящие заявки в друзья (ожидают принятия).

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`** — массив записей дружбы со статусом `PENDING`

---

### GET `/users/tests`

Список всех психологических тестов.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`**

```json
[
  {
    "id": 1,
    "title": "Stress & Productivity Assessment",
    "description": "Assess your current stress level and productivity",
    "questions": [
      {
        "id": 1,
        "questionText": "How often do you feel overwhelmed by your workload?",
        "orderIndex": 1,
        "options": [
          { "id": 1, "optionText": "Never" },
          { "id": 2, "optionText": "Rarely" },
          { "id": 3, "optionText": "Sometimes" },
          { "id": 4, "optionText": "Often" }
        ]
      }
    ]
  }
]
```

> Баллы (`stressScore`, `motivationScore`, `productivityScore`) за каждый вариант ответа **не возвращаются** клиенту — они используются внутри для подсчёта результата.

---

### GET `/users/tests/{testId}`

Получить конкретный тест с вопросами.

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **testId** | number | ID теста |

**Response `200 OK`** — один тест (структура как в `/users/tests`)

**Ошибки**

| Код | Причина |
|-----|---------|
| 404 | Тест не найден |

---

### POST `/users/tests/take`

Пройти психологический тест.

**Headers:** `Authorization: Bearer <token>`

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **testId** | number | да | — |
| **answers** | object | да | не пустой; ключ = questionId, значение = selectedOptionId |

```json
{
  "testId": 1,
  "answers": {
    "1": 2,
    "2": 5,
    "3": 9,
    "4": 13
  }
}
```

> Нужно ответить на **все** вопросы теста. Ключи — ID вопросов, значения — ID выбранного варианта ответа.

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| id | number | ID результата |
| testId | number | ID теста |
| testTitle | string | Название теста |
| stressLevel | number | Уровень стресса (0–100) |
| motivationLevel | number | Уровень мотивации (0–100) |
| productivityLevel | number | Уровень продуктивности (0–100) |
| recommendations | array | Список рекомендаций (строки) |
| createdAt | datetime | Дата прохождения |

```json
{
  "id": 1,
  "testId": 1,
  "testTitle": "Stress & Productivity Assessment",
  "stressLevel": 45,
  "motivationLevel": 60,
  "productivityLevel": 65,
  "recommendations": [
    "Take regular breaks during work",
    "Practice mindfulness"
  ],
  "createdAt": "2026-04-25T10:30:00"
}
```

---

### GET `/users/tests/results`

История прохождения тестов текущего пользователя.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`** — массив результатов (структура как в POST `/users/tests/take`)

---

### GET `/users/tests/stats`

Средние показатели по всем пройденным тестам.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| avgStressLevel | number | Средний уровень стресса |
| avgMotivationLevel | number | Средняя мотивация |
| avgProductivityLevel | number | Средняя продуктивность |
| totalTestsTaken | number | Кол-во пройденных тестов |
| overallStatus | string | Текстовое описание состояния |

```json
{
  "avgStressLevel": 45.5,
  "avgMotivationLevel": 62.0,
  "avgProductivityLevel": 58.5,
  "totalTestsTaken": 4,
  "overallStatus": "Moderate stress, good motivation"
}
```

---

## Goal Service

**Enum значения:**

`category`: `HEALTH` / `EDUCATION` / `FINANCE` / `CAREER` / `PERSONAL` / `SOCIAL` / `OTHER`  
`periodType`: `DAILY` / `WEEKLY` / `MONTHLY` / `YEARLY` / `CUSTOM`  
`status`: `ACTIVE` / `COMPLETED` / `FAILED`

---

### POST `/goals`

Создать новую цель.

**Headers:** `Authorization: Bearer <token>`

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **title** | string | да | не пустой |
| **category** | string | да | валидный enum GoalCategory |
| **periodType** | string | да | валидный enum GoalPeriodType |
| **deadline** | string | да | дата в формате `YYYY-MM-DD`, должна быть в будущем |
| *description* | string | нет | — |

```json
{
  "title": "Learn Spring Boot",
  "description": "Complete the microservices course",
  "category": "EDUCATION",
  "periodType": "MONTHLY",
  "deadline": "2026-05-31"
}
```

**Response `201 Created`**

| Поле | Тип | Описание |
|------|-----|---------|
| id | number | ID цели |
| userId | number | ID владельца |
| title | string | Название |
| description | string \| null | Описание |
| category | string | Категория |
| status | string | Начальный статус — `ACTIVE` |
| periodType | string | Тип периода |
| progressPercentage | number | Прогресс в % (0–100), изначально 0 |
| deadline | string | Дедлайн (YYYY-MM-DD) |
| createdAt | datetime | Дата создания |
| updatedAt | datetime | Дата обновления |

```json
{
  "id": 1,
  "userId": 1,
  "title": "Learn Spring Boot",
  "description": "Complete the microservices course",
  "category": "EDUCATION",
  "status": "ACTIVE",
  "periodType": "MONTHLY",
  "progressPercentage": 0,
  "deadline": "2026-05-31",
  "createdAt": "2026-04-25T10:00:00",
  "updatedAt": "2026-04-25T10:00:00"
}
```

**Ошибки**

| Код | Причина |
|-----|---------|
| 400 | Дедлайн в прошлом или невалидные поля |

---

### PUT `/goals/{goalId}`

Обновить цель.

**Headers:** `Authorization: Bearer <token>`

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **goalId** | number | ID цели |

**Request Body** `application/json`

Все поля опциональны — передайте только то, что нужно изменить.

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| *title* | string | нет | — |
| *description* | string | нет | — |
| *category* | string | нет | валидный enum GoalCategory |
| *deadline* | string | нет | `YYYY-MM-DD` |
| *progressPercentage* | number | нет | 0–100 |

```json
{
  "title": "Learn Spring Boot Advanced",
  "progressPercentage": 50,
  "deadline": "2026-06-30"
}
```

**Response `200 OK`** — обновлённая цель (структура как в POST `/goals`)

**Ошибки**

| Код | Причина |
|-----|---------|
| 403 | Цель принадлежит другому пользователю |
| 404 | Цель не найдена |

---

### PATCH `/goals/{goalId}/complete`

Завершить цель (статус → `COMPLETED`).

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **goalId** | number | ID цели |

**Response `200 OK`** — цель со статусом `COMPLETED` и `progressPercentage: 100`

**Ошибки**

| Код | Причина |
|-----|---------|
| 400 | Цель уже завершена или провалена |
| 403 | Чужая цель |
| 404 | Цель не найдена |

---

### DELETE `/goals/{goalId}`

Удалить цель.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **goalId** | number | ID цели |

**Response `204 No Content`**

**Ошибки**

| Код | Причина |
|-----|---------|
| 403 | Чужая цель |
| 404 | Цель не найдена |

---

### GET `/goals`

Список целей текущего пользователя с фильтрацией.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Query Params**

| Параметр | Тип | Обязательное | Описание |
|----------|-----|:---:|---------|
| *category* | string | нет | Фильтр по категории (`HEALTH`, `EDUCATION`, ...) |
| *status* | string | нет | Фильтр по статусу (`ACTIVE`, `COMPLETED`, `FAILED`) |

```
GET /goals?status=ACTIVE&category=EDUCATION
```

**Response `200 OK`** — массив целей (структура как в POST `/goals`)

---

### GET `/goals/{goalId}`

Получить конкретную цель.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`** — цель (структура как в POST `/goals`)

**Ошибки**

| Код | Причина |
|-----|---------|
| 403 | Чужая цель |
| 404 | Цель не найдена |

---

### GET `/goals/stats`

Статистика по целям текущего пользователя.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| totalGoals | number | Всего целей |
| activeGoals | number | Активных |
| completedGoals | number | Завершённых |
| failedGoals | number | Провалённых |
| completionRate | number | % выполнения (0.0–100.0) |
| avgProgressPercentage | number | Средний прогресс по активным |

```json
{
  "totalGoals": 10,
  "activeGoals": 5,
  "completedGoals": 4,
  "failedGoals": 1,
  "completionRate": 40.0,
  "avgProgressPercentage": 35.5
}
```

---

## Finance Service

**Enum значения:**

`IncomeType`: `SALARY` / `FREELANCE` / `INVESTMENT` / `GIFT` / `OTHER`  
`ExpenseCategory`: `FOOD` / `TRANSPORT` / `HOUSING` / `HEALTHCARE` / `EDUCATION` / `ENTERTAINMENT` / `CLOTHING` / `SAVINGS` / `OTHER`

---

### POST `/finance/monthly`

Создать месячный финансовый план.

**Headers:** `Authorization: Bearer <token>`

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **year** | number | да | целое число, min 2020 |
| **month** | number | да | 1–12 |
| **baseIncome** | number | да | положительное число |

```json
{
  "year": 2026,
  "month": 4,
  "baseIncome": 500000.00
}
```

**Response `201 Created`**

| Поле | Тип | Описание |
|------|-----|---------|
| id | number | ID плана |
| year | number | Год |
| month | number | Месяц |
| baseIncome | number | Базовый доход |
| totalIncome | number | Фактический доход (сумма всех добавленных) |
| totalExpenses | number | Фактические расходы |
| balance | number | `totalIncome - totalExpenses` |
| spentPercentage | number | % потраченного от дохода |
| expensesByCategory | object | Расходы сгруппированные по категориям |
| incomes | array | Список доходов |
| expenses | array | Список расходов |

```json
{
  "id": 1,
  "year": 2026,
  "month": 4,
  "baseIncome": 500000.00,
  "totalIncome": 0.00,
  "totalExpenses": 0.00,
  "balance": 0.00,
  "spentPercentage": 0.0,
  "expensesByCategory": {},
  "incomes": [],
  "expenses": []
}
```

**Ошибки**

| Код | Причина |
|-----|---------|
| 409 | План на этот месяц уже существует |

---

### GET `/finance/monthly`

Все месячные планы текущего пользователя.

**Headers:** `Authorization: Bearer <token>`  
**Response `200 OK`** — массив планов

---

### GET `/finance/monthly/current`

Месячный план на текущий месяц.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`** — план текущего месяца

**Ошибки**

| Код | Причина |
|-----|---------|
| 404 | План на текущий месяц не создан |

---

### GET `/finance/monthly/{financeId}`

Получить конкретный месячный план.

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **financeId** | number | ID плана |

**Response `200 OK`** — план

**Ошибки**

| Код | Причина |
|-----|---------|
| 403 | Чужой план |
| 404 | План не найден |

---

### POST `/finance/monthly/{financeId}/income`

Добавить запись дохода в месячный план.

**Headers:** `Authorization: Bearer <token>`

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **financeId** | number | ID месячного плана |

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **amount** | number | да | положительное число |
| **type** | string | да | валидный IncomeType enum |
| **date** | string | да | `YYYY-MM-DD` |
| *description* | string | нет | — |

```json
{
  "amount": 300000.00,
  "type": "SALARY",
  "description": "Monthly salary",
  "date": "2026-04-25"
}
```

**Response `201 Created`**

| Поле | Тип | Описание |
|------|-----|---------|
| id | number | ID записи дохода |
| amount | number | Сумма |
| type | string | Тип дохода |
| description | string \| null | Описание |
| date | string | Дата |
| createdAt | datetime | Дата создания записи |

```json
{
  "id": 1,
  "amount": 300000.00,
  "type": "SALARY",
  "description": "Monthly salary",
  "date": "2026-04-25",
  "createdAt": "2026-04-25T10:00:00"
}
```

---

### POST `/finance/monthly/{financeId}/expense`

Добавить запись расхода в месячный план.

**Headers:** `Authorization: Bearer <token>`

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **financeId** | number | ID месячного плана |

**Request Body** `application/json`

| Поле | Тип | Обязательное | Валидация |
|------|-----|:---:|-----------|
| **amount** | number | да | положительное число |
| **category** | string | да | валидный ExpenseCategory enum |
| **date** | string | да | `YYYY-MM-DD` |
| *description* | string | нет | — |

```json
{
  "amount": 15000.00,
  "category": "FOOD",
  "description": "Groceries",
  "date": "2026-04-25"
}
```

**Response `201 Created`**

| Поле | Тип | Описание |
|------|-----|---------|
| id | number | ID записи расхода |
| amount | number | Сумма |
| category | string | Категория расхода |
| description | string \| null | Описание |
| date | string | Дата |
| createdAt | datetime | Дата создания |

```json
{
  "id": 1,
  "amount": 15000.00,
  "category": "FOOD",
  "description": "Groceries",
  "date": "2026-04-25",
  "createdAt": "2026-04-25T10:00:00"
}
```

---

### GET `/finance/stats`

Общая финансовая статистика пользователя (по всем месяцам).

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| totalIncome | number | Суммарный доход |
| totalExpenses | number | Суммарные расходы |
| balance | number | Остаток |
| spentPercentage | number | % потраченного |
| overBudget | boolean | `true` если расходы > доходов |

```json
{
  "totalIncome": 300000.00,
  "totalExpenses": 15000.00,
  "balance": 285000.00,
  "spentPercentage": 5.0,
  "overBudget": false
}
```

---

## Analytics Service

> Сервис не имеет собственной БД. Получает данные из goal-service, finance-service и user-service через Feign HTTP. При недоступности источника возвращает нулевые значения без ошибки.

### GET `/analytics/overview`

Полная аналитика: цели + финансы + продуктивность + рекомендации.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет  
**Query Params:** нет

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| goals | object | Аналитика целей |
| finance | object | Финансовая аналитика |
| productivity | object | Аналитика продуктивности |
| overallDevelopmentScore | number | Итоговый балл развития (0–100) |
| developmentLevel | string | `STARTER` / `BEGINNER` / `INTERMEDIATE` / `ADVANCED` / `ELITE` |
| recommendations | object | Рекомендации |

```json
{
  "goals": {
    "totalGoals": 10,
    "activeGoals": 5,
    "completedGoals": 4,
    "failedGoals": 1,
    "completionRate": 40.0,
    "avgProgressPercentage": 35.5,
    "statusChart": {
      "labels": ["Active", "Completed", "Failed"],
      "values": [5, 4, 1],
      "chartType": "pie"
    }
  },
  "finance": {
    "totalIncome": 300000.00,
    "totalExpenses": 15000.00,
    "balance": 285000.00,
    "spentPercentage": 5.0,
    "overBudget": false,
    "budgetChart": {
      "labels": ["Income", "Expenses"],
      "values": [300000.00, 15000.00],
      "chartType": "bar"
    }
  },
  "productivity": {
    "avgStressLevel": 45.0,
    "avgMotivationLevel": 60.0,
    "avgProductivityLevel": 65.0,
    "totalTestsTaken": 3,
    "overallStatus": "Good",
    "developmentScore": 62.5,
    "productivityChart": {
      "labels": ["Stress", "Motivation", "Productivity"],
      "values": [45.0, 60.0, 65.0],
      "chartType": "radar"
    }
  },
  "overallDevelopmentScore": 58.3,
  "developmentLevel": "INTERMEDIATE",
  "recommendations": {
    "recommendations": ["Keep maintaining your budget discipline!"],
    "source": "rule-based"
  }
}
```

**Уровни развития:**

| Уровень | Балл |
|---------|------|
| STARTER | 0–20 |
| BEGINNER | 20–40 |
| INTERMEDIATE | 40–60 |
| ADVANCED | 60–80 |
| ELITE | 80–100 |

---

### GET `/analytics/goals`

Только аналитика по целям.

**Response `200 OK`** — блок `goals` из overview

---

### GET `/analytics/finance`

Только финансовая аналитика.

**Response `200 OK`** — блок `finance` из overview

---

### GET `/analytics/productivity`

Только аналитика продуктивности (на основе психологических тестов).

**Response `200 OK`** — блок `productivity` из overview

---

### GET `/analytics/recommendations`

Персональные рекомендации.

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| recommendations | array | Список рекомендаций (строки) |
| source | string | `"rule-based"` |

```json
{
  "recommendations": [
    "You have too many failed goals. Try breaking them into smaller steps.",
    "Your stress level is high. Consider mindfulness or regular exercise.",
    "You are over budget this month. Review your expenses."
  ],
  "source": "rule-based"
}
```

**Правила генерации рекомендаций:**

| Условие | Рекомендация |
|---------|-------------|
| failedGoals > 3 | Разбить цели на меньшие шаги |
| overBudget = true | Пересмотреть расходы |
| avgStressLevel > 70 | Mindfulness, физическая активность |
| avgProductivityLevel < 40 | Организация времени, Pomodoro |
| activeGoals = 0 | Поставить новые цели |

---

## Notification Service

**Типы уведомлений:** `GOAL_DEADLINE` / `GOAL_EXPIRED` / `GOAL_COMPLETED` / `SYSTEM`  
**Статусы уведомлений:** `UNREAD` / `READ`

---

### GET `/notifications`

Все уведомления текущего пользователя (от новых к старым).

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `200 OK`**

| Поле | Тип | Описание |
|------|-----|---------|
| id | number | ID уведомления |
| userId | number | ID получателя |
| title | string | Заголовок |
| message | string | Текст уведомления |
| type | string | Тип уведомления |
| status | string | `UNREAD` / `READ` |
| referenceId | number \| null | ID связанного объекта (например, goalId) |
| createdAt | datetime | Дата создания |

```json
[
  {
    "id": 1,
    "userId": 1,
    "title": "Goal Deadline Tomorrow",
    "message": "Your goal 'Learn Spring Boot' is due tomorrow!",
    "type": "GOAL_DEADLINE",
    "status": "UNREAD",
    "referenceId": 5,
    "createdAt": "2026-04-25T09:00:00"
  }
]
```

---

### GET `/notifications/unread`

Только непрочитанные уведомления.

**Headers:** `Authorization: Bearer <token>`  
**Response `200 OK`** — массив уведомлений со статусом `UNREAD`

---

### GET `/notifications/unread/count`

Количество непрочитанных уведомлений.

**Headers:** `Authorization: Bearer <token>`

**Response `200 OK`**

```json
{
  "count": 3
}
```

---

### PATCH `/notifications/{notificationId}/read`

Отметить уведомление как прочитанное.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Path Params**

| Параметр | Тип | Описание |
|----------|-----|---------|
| **notificationId** | number | ID уведомления |

**Response `200 OK`** — уведомление со статусом `READ`

**Ошибки**

| Код | Причина |
|-----|---------|
| 403 | Чужое уведомление |
| 404 | Уведомление не найдено |

---

### PATCH `/notifications/read-all`

Отметить все уведомления как прочитанные.

**Headers:** `Authorization: Bearer <token>`  
**Request Body:** нет

**Response `204 No Content`**

---

## Внутренние эндпоинты (inter-service)

Эти эндпоинты вызываются только между сервисами через Feign. Через API Gateway они недоступны извне.

| Метод | URL | Вызывающий | Описание |
|-------|-----|-----------|---------|
| POST | `/users/internal/profile` | auth-service | Создать профиль при регистрации |
| GET | `/users/tests/internal/{userId}/stats` | analytics-service | Статистика тестов пользователя |
| GET | `/goals/internal/{userId}/stats` | analytics-service | Статистика целей пользователя |
| GET | `/finance/internal/{userId}/stats` | analytics-service | Финансовая статистика пользователя |
| POST | `/notifications/internal/send` | goal-service | Создать уведомление (дедлайн / просрочка) |

---

## HTTP коды ответов

| Код | Значение |
|-----|---------|
| 200 | Успех |
| 201 | Объект создан |
| 204 | Успех, тело пустое |
| 400 | Невалидное тело запроса / параметры |
| 401 | Токен отсутствует, истёк или невалиден |
| 403 | Доступ запрещён (чужой ресурс) |
| 404 | Ресурс не найден |
| 409 | Конфликт (дубликат) |
| 500 | Внутренняя ошибка сервера |
