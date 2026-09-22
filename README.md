# Java Tickets Docker Compose Demo

Учебный проект из трех компонентов для демонстрации Docker на занятии:

- `postgres` — база PostgreSQL с постоянным Docker volume;
- `backend` — Java 17 и Spring Boot, REST API, JPA и автоматическое создание таблицы;
- `frontend` — статический личный кабинет, который раздает nginx и проксирует `/api` в backend.

На компьютере нужны только Docker Desktop и браузер. Java, Maven и nginx устанавливать не требуется: они находятся внутри образов.

## Быстрый запуск в Windows

Откройте PowerShell в каталоге проекта и выполните:

```powershell
docker compose build
docker compose up -d
docker compose ps
```

Либо запустите `start-demo.cmd` двойным щелчком. Скрипт выполнит те же команды и откроет браузер. `stop-demo.cmd` остановит стенд, сохранив данные базы.

Если установлен старый Docker Compose, замените `docker compose` на `docker-compose`.

После запуска откройте:

- личный кабинет: <http://localhost:8080>
- Swagger UI backend: <http://localhost:8081/swagger-ui.html>
- healthcheck backend: <http://localhost:8081/actuator/health>

При первом запуске backend автоматически создаст таблицу `segments` и два демонстрационных билета:

- билет `5552139265672`, два сегмента;
- билет `5552139265673`, один сегмент;
- документ пассажира для обоих билетов: `3108111434`.

Проверка всего стенда из PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1
```

## Что показывать студентам

### 1. Сборка образов

```powershell
docker compose build
docker image ls
```

Во время сборки backend Maven скачивает зависимости и собирает JAR в промежуточном контейнере. В финальный образ попадают только Java Runtime, `wget` для healthcheck и готовый JAR. Frontend строится отдельно на базе nginx.

Для наглядной повторной сборки без кэша:

```powershell
docker compose build --no-cache
```

### 2. Запуск связанных сервисов

```powershell
docker compose up
```

Без `-d` логи всех трех контейнеров видны в одном окне. Остановка — `Ctrl+C`.

Фоновый режим и просмотр логов:

```powershell
docker compose up -d
docker compose logs -f
```

Compose сначала ждет готовности PostgreSQL, потом запускает backend и лишь после его healthcheck запускает frontend.

### 3. Разница между `up` и `run`

`docker compose up` поднимает описанную систему сервисов. `docker compose run` создает одноразовый контейнер из конфигурации выбранного сервиса и обычно применяется для утилит, миграций и административных команд.

Посмотреть версию Java внутри одноразового контейнера backend:

```powershell
docker compose run --rm --no-deps backend java -version
```

Посмотреть версию nginx:

```powershell
docker compose run --rm --no-deps frontend nginx -v
```

После `docker compose up -d` проверить backend из одноразового контейнера в сети Compose:

```powershell
docker compose run --rm --no-deps frontend wget -qO- http://backend:8080/api/info
```

Флаг `--rm` удаляет временный контейнер после завершения команды. `--no-deps` не запускает зависимости повторно.

### 4. Сеть контейнеров

В браузере frontend доступен по `localhost:8080`, backend — по `localhost:8081`. Но nginx обращается к backend по имени сервиса `http://backend:8080`, а backend к базе — `postgres:5432`. Внутри сети Compose `localhost` всегда означает текущий контейнер.

Порты можно увидеть командой:

```powershell
docker compose ps
```

### 5. Переменные окружения

Скопируйте `.env.example` в `.env` и измените, например, внешний порт frontend:

```powershell
Copy-Item .env.example .env
```

```text
FRONTEND_PORT=8090
```

После `docker compose up -d` кабинет будет доступен на <http://localhost:8090>. Строка подключения backend собирается из переменных окружения и использует внутреннее имя сервиса `postgres`.

### 6. Volume и жизненный цикл данных

Обычная остановка сохраняет данные:

```powershell
docker compose down
docker compose up -d
```

Полный сброс базы вместе с volume:

```powershell
docker compose down -v
docker compose up -d
```

После сброса демонстрационные записи создадутся заново.

## API

| Метод | Адрес | Назначение |
|---|---|---|
| `POST` | `/api/process/sale` | Продать билет и создать по записи на каждый сегмент |
| `POST` | `/api/process/refund` | Пометить возвращенными все сегменты билета |
| `GET` | `/api/segments?ticketNumber=...` | Найти сегменты по номеру билета |
| `GET` | `/api/segments?documentNumber=...` | Найти все билеты по документу |
| `GET` | `/api/info` | Проверить связь frontend с backend |

Примеры тела запроса лежат в `examples/sale.json` и `examples/refund.json`. В PowerShell 7 их можно отправить так:

```powershell
Invoke-RestMethod -Method Post -ContentType "application/json" `
  -Uri "http://localhost:8080/api/process/sale" `
  -Body (Get-Content .\examples\sale.json -Raw)
```

```powershell
Invoke-RestMethod -Method Post -ContentType "application/json" `
  -Uri "http://localhost:8080/api/process/refund" `
  -Body (Get-Content .\examples\refund.json -Raw)
```

Backend проверяет формат JSON, ограничивает тело операций двумя килобайтами и возвращает:

- `200` — операция выполнена;
- `400` — данные не прошли проверку;
- `409` — повторная продажа, отсутствующий билет или повторный возврат;
- `413` — JSON больше 2 КБ.

Продажа выполняется в транзакции. Ограничение уникальности пары `(ticket_number, serial_number)` не позволяет параллельным запросам создать дубликаты. Возврат использует блокировку строк `PESSIMISTIC_WRITE`, поэтому два одновременных возврата не могут успешно обработать один билет.

## Полезные команды

```powershell
# Состояние контейнеров
docker compose ps

# Логи только backend
docker compose logs -f backend

# Выполнить SQL в PostgreSQL
docker compose exec postgres psql -U tickets_user -d tickets -c "select ticket_number, serial_number, refunded from segments order by ticket_number, serial_number;"

# Пересобрать только backend и заменить его контейнер
docker compose up -d --build backend

# Остановить стенд без удаления данных
docker compose down
```

## Структура проекта

```text
java-docker-tickets/
├── docker-compose.yml
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── src/
├── examples/
└── scripts/
```

## Если проект не запускается

1. Убедитесь, что Docker Desktop запущен и использует Linux containers.
2. Проверьте занятые порты: `docker compose ps` и `netstat -ano | findstr ":8080 :8081 :5432"`.
3. Если порт занят, измените соответствующее значение в `.env`.
4. Посмотрите причину: `docker compose logs backend postgres frontend`.
5. Для полностью чистого старта выполните `docker compose down -v`, затем `docker compose build --no-cache` и `docker compose up`.

Для остановки используйте `docker compose down`, а не удаляйте контейнеры вручную: так Compose корректно работает со всей группой сервисов.
