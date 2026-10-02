# Документация на проекта

## Система за управление на задачи със срокове и приоритети

### 1. Обща информация

Проектът представлява уеб-базирана система за управление на задачи. Приложението позволява на потребителите да създават, редактират, изтриват и проследяват задачи със срокове, приоритети, статуси и коментари.

Системата е разработена като учебен проект по производствена практика.

### 2. Използвани технологии

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL
- HTML5
- CSS3
- JavaScript
- Maven

### 3. Основни функционалности

- Регистрация на потребители
- Вход и изход от системата
- Създаване на задачи
- Преглед на задачите на текущия потребител
- Редактиране на задачи
- Изтриване на задачи
- Промяна на статус на задача
- Задаване на приоритет
- Задаване на краен срок
- Търсене на задачи по заглавие
- Филтриране по статус
- Филтриране по приоритет
- Сортиране по срок
- Сортиране по приоритет
- Добавяне и изтриване на коментари
- Съхраняване на данните в MySQL база
- Всеки потребител вижда само своите задачи

### 4. Архитектура на проекта

Проектът използва трислойна архитектура.

**Presentation Layer**

Потребителският интерфейс е реализиран чрез HTML, CSS и JavaScript. Основните файлове са:

```text
src/main/resources/static/index.html
src/main/resources/static/login.html
src/main/resources/static/register.html
```

**Business Logic Layer**

Бизнес логиката е реализирана в service класове:

```text
TaskService
CommentService
CustomUserDetailsService
```

**Data Access Layer**

Достъпът до базата данни се реализира чрез Spring Data JPA repositories:

```text
TaskRepository
CommentRepository
UserRepository
```

### 5. Структура на проекта

```text
src/main/java/org/example/taskmanager
├── config
│   └── SecurityConfig.java
├── controller
│   ├── AuthController.java
│   ├── TaskController.java
│   └── CommentController.java
├── model
│   ├── AppUser.java
│   ├── UserRole.java
│   ├── Task.java
│   ├── TaskPriority.java
│   ├── TaskStatus.java
│   └── Comment.java
├── repository
│   ├── UserRepository.java
│   ├── TaskRepository.java
│   └── CommentRepository.java
├── service
│   ├── CustomUserDetailsService.java
│   ├── TaskService.java
│   └── CommentService.java
└── TaskmanagerApplication.java
```

### 6. База данни

Проектът използва MySQL база данни.

Име на базата:

```sql
taskmanager_db
```

SQL команда за създаване:

```sql
CREATE DATABASE taskmanager_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Таблиците се създават автоматично от Spring Boot чрез JPA.

Основни таблици:

```text
users
tasks
comments
```

**users**

Съдържа данни за потребителите:

- id
- username
- password
- role

Паролите се съхраняват криптирани чрез BCrypt.

**tasks**

Съдържа задачите:

- id
- title
- description
- priority
- status
- deadline
- user_id

Всяка задача е свързана с конкретен потребител.

**comments**

Съдържа коментарите:

- id
- task_id
- text
- created_at

### 7. Конфигурация

Файл:

```text
src/main/resources/application.properties
```

Примерна конфигурация:

```properties
spring.application.name=taskmanager

spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

В `YOUR_PASSWORD` се поставя паролата за MySQL потребителя.

### 8. Инсталация и стартиране

1. Инсталира се Java 17.
2. Инсталира се MySQL Server.
3. Създава се база данни `taskmanager_db`.
4. В `application.properties` се настройват MySQL потребителят и паролата.
5. Стартира се главният клас:

```text
TaskmanagerApplication.java
```

След стартиране приложението е достъпно на адрес:

```text
http://localhost:8080
```

### 9. Ръководство за потребителя

**Регистрация**

Потребителят отваря:

```text
http://localhost:8080/register.html
```

Въвежда потребителско име и парола. След успешна регистрация се пренасочва към страницата за вход.

**Вход**

Потребителят отваря:

```text
http://localhost:8080/login.html
```

След успешен вход се отваря главната страница на системата.

**Създаване на задача**

Във формата "Create Task" потребителят въвежда заглавие, описание, приоритет и краен срок. След натискане на "Add Task" задачата се добавя в списъка.

**Редактиране на задача**

Чрез бутона "Edit" данните на задачата се зареждат във формата. След промяна потребителят натиска "Save Changes".

**Изтриване на задача**

Чрез бутона "Delete" задачата се премахва от системата.

**Промяна на статус**

Всяка задача има статус:

```text
TODO
IN_PROGRESS
DONE
```

Потребителят може да променя статуса чрез бутоните към всяка задача.

**Коментари**

Под всяка задача има поле за добавяне на коментар. Коментарите могат да се добавят и изтриват.

**Филтриране и търсене**

Потребителят може да филтрира задачите по статус и приоритет, както и да търси задачи по заглавие.

**Сортиране**

Задачите могат да се сортират по краен срок и по приоритет.

**Изход**

Чрез бутона "Logout" потребителят излиза от системата.

### 10. REST API документация

**Регистрация**

```text
POST /register
```

Параметри:

```text
username
password
```

**Вход**

```text
POST /login
```

**Изход**

```text
POST /logout
```

**Получаване на всички задачи**

```text
GET /api/tasks
```

Връща задачите на текущия потребител.

**Получаване на задача по ID**

```text
GET /api/tasks/{id}
```

**Създаване на задача**

```text
POST /api/tasks
```

Примерно тяло:

```json
{
  "title": "Learn Spring Boot",
  "description": "Create backend for task manager",
  "priority": "HIGH",
  "status": "TODO",
  "deadline": "2026-07-10"
}
```

**Редактиране на задача**

```text
PUT /api/tasks/{id}
```

**Изтриване на задача**

```text
DELETE /api/tasks/{id}
```

**Промяна на статус**

```text
PATCH /api/tasks/{id}/status?status=DONE
```

**Филтриране по статус**

```text
GET /api/tasks/filter/status?status=TODO
```

**Филтриране по приоритет**

```text
GET /api/tasks/filter/priority?priority=HIGH
```

**Търсене по заглавие**

```text
GET /api/tasks/search?keyword=spring
```

**Сортиране по срок**

```text
GET /api/tasks/sort/deadline
```

**Сортиране по приоритет**

```text
GET /api/tasks/sort/priority
```

**Получаване на коментари към задача**

```text
GET /api/tasks/{taskId}/comments
```

**Добавяне на коментар**

```text
POST /api/tasks/{taskId}/comments
```

Примерно тяло:

```json
{
  "text": "This task is important."
}
```

**Изтриване на коментар**

```text
DELETE /api/comments/{commentId}
```

### 11. Сигурност

Системата използва Spring Security.

Реализирани са:

- регистрация
- вход
- изход
- криптиране на пароли с BCrypt
- лични задачи за всеки потребител

Всеки потребител има достъп само до своите задачи и коментари.

### 12. Сравнение със съществуващи приложения

Проектът е вдъхновен от системи като Trello, Asana и Todoist.

Общи функционалности:

- създаване и управление на задачи
- приоритети
- срокове
- статуси
- коментари
- търсене и филтриране

Разработваният проект е учебна система и е по-опростен от професионалните приложения, но покрива основните функции, необходими за управление на лични задачи.

### 13. Тестване

Системата е проверена чрез следните сценарии:

1. Регистрация на нов потребител.
2. Вход с регистриран потребител.
3. Създаване на задача.
4. Редактиране на задача.
5. Изтриване на задача.
6. Промяна на статус.
7. Добавяне и изтриване на коментар.
8. Филтриране по статус.
9. Филтриране по приоритет.
10. Търсене по заглавие.
11. Сортиране по срок.
12. Сортиране по приоритет.
13. Проверка дали задачите се запазват след рестартиране.
14. Проверка дали различните потребители виждат само своите задачи.

### 14. Възможности за бъдещо развитие

В бъдеще системата може да бъде разширена с:

- администраторски панел
- редактиране на потребителски профил
- известия за наближаващи срокове
- качване на файлове към задача
- екипна работа
- споделяне на задачи между потребители
- календарен изглед
- по-подробни статистики

### 15. Заключение

Разработената система предоставя основните функционалности за управление на задачи със срокове и приоритети. Приложението използва Spring Boot, MySQL и уеб интерфейс, като осигурява регистрация, вход, лични задачи за всеки потребител, коментари, филтриране, търсене и сортиране.

Проектът изпълнява поставените изисквания и може да бъде използван като основа за бъдещо разширяване.
