# Product Catalog Application

Приложение позволяет просматривать, добавлять, редактировать и удалять каталоги и продукты, с поддержкой аутентификации пользователей, аудита действий.
Бэкенд использует PostgreSQL как базу данных, Liquibase для миграций, и реализовано на Java, используя фреймворк Spring Boot.

## Функциональность

- **Пользователи**: Регистрация, логин/логаут. Роли: User (просмотр) и Admin (полное управление).
- **Каталоги**: Создание, удаление, просмотр.
- **Товары**: Добавление, обновление, удаление, фильтрация (по имени, цене, бренду, категории).
- **Аудит**: Логирование действий в БД.
- **Метрики**: Отображение времени выполнения операций.

## Требования

- **Java**: 17+.
- **Maven**: 3.8+ для сборки и тестирования.
- **Docker**: Для запуска PostgreSQL в контейнере.
- **PostgreSQL**: Версия 16+.
- **Дополнительно**: Для тестов требуется Docker (Testcontainers использует контейнеры для изоляции).

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/LittleBlBb/Y_LAB_HOMEWORK.git
cd Y_LAB_HOMEWORK
```

### 2. Настройка конфигурации

Файл `src/main/resources/application.yml` содержит настройки БД. По умолчанию:

```
liquibase:
  changeLog: db/changelog/changelog.xml
  serviceSchema: liquibase

db:
  host: localhost
  username: catalog
  password: password
  name: postgres
  port: 15532
  schema: app

spring:
  liquibase:
    enabled: false

```

### 3. Запуск базы данных в Docker

Проект использует PostgreSQL в Docker. Файл `docker-compose.yml` настроен для запуска контейнера.

- Запустите Docker Compose:

```bash
docker-compose up -d
```

Это запустит PostgreSQL на порту 15532 с пользователем `catalog`, паролем `password` и БД `postgres`.

- Проверьте статус:

```bash
docker ps
```

Должен отображаться контейнер `postgres`.

- Остановка:

```bash
docker-compose down
```

### 4. Применение миграций Liquibase

Миграции создают схему `app` для моделей и `liquibase` для служебных таблиц, также создаются таблицы, последовательности и вставляют начальные данные (админ-аккаунт, каталоги, товары).

- Соберите проект Maven (если не собрали):

```bash
mvn clean install
```

### 5. Запуск приложения

- Запустите класс `Main.java` (Запустится сервер на `localhost:8080`, а также пройдут миграции).

#### Использование WEB
Если запускаете в apache-tomcat:
Страница Swagger будет доступна по адресу:
```
http://localhost:8080/swagger-ui/index.html
```

Также можно будет обратиться к следующим эндпоинтам:
- `http://localhost:8080/api/products` - отображение всех товаров
- `http://localhost:8080/api/catalogs` - отображение всех каталогов
- `http://localhost:8080/api/logs` - отображение всех логов
- `http://localhost:8080/api/users` - отображение всех пользователей
Также можно протестировать все GET, POST, PUT, DELETE в Swagger.

### 6. Запуск тестов

Тесты используют JUnit, Mockito и Testcontainers (автоматически запускают PostgreSQL в контейнере для изоляции).

- Запустите все тесты:

```bash
mvn test
```

- Для конкретного теста (через IDE): Запустите классы в `src/test/java` (например, `AuditRepositoryTest`).
- Требования: Docker должен быть запущен, так как Testcontainers создает контейнеры на лету.

## Структура проекта
- `src/main/java/ProductCatalog/audit`: Модуль аудита
- `src/main/java/ProductCatalog/performance`: Модуль метрик производительности
- `src/main/java/ProductCatalog/`: Модели, Репозитории, Сервисы, Контроллеры, валидаторы, UI, DB и прочее.
- `src/main/resources/`: `application.yml` (конфиг), Liquibase changelogs.
- `src/test/java/`: Unit-тесты для репозиториев.
- `docker-compose.yml`: Для PostgreSQL.