# Finance-tracker


RESTful API для учета финансов

## 🛠 Технологический стек
* **ЯП**: Java 21
* **Фреймворк**: Spring Boot 4.1.0
* **Аутентификация**: Spring Security, JWT (Stateless аутентификация), BCrypt
* **База данных**: PostgreSQL (основная в Docker), H2 (In-Memory для тестирования)
* **Миграции**: Liquibase
* **Тестирование**: JUnit 5, Mockito: юнит тесты, интеграционные тесты


## ⚙️ Локальный запуск
1. Клонировать репозиторий: `git clone <https://github.com/IDAnton/finance-tracker>`
2. Развернуть PostgreSQL в Docker: `docker-compose up -d`
3. Запустить приложение через Maven: `./mvnw spring-boot:run`
