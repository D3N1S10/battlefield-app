# Battlefield Simulator — Java Servlet Application

**Автор:** Далецкий Денис, 3 курс, группа 14.2  
**Репозиторий:** [github.com/D3N1S10/battlefield-app](https://github.com/D3N1S10/battlefield-app)

Моделирование поля боя на сетке клеток. Боевые юниты различных типов занимают одну или несколько клеток, двигаются с разной скоростью, стреляют в различных направлениях. При попадании юнит повреждается или уничтожается.

---

## API Endpoints (14 шт.)

### Поля боя
- `POST   /api/battlefields` — Создать поле боя
- `GET    /api/battlefields` — Список всех полей
- `GET    /api/battlefields/{id}` — Получить поле по ID
- `DELETE /api/battlefields/{id}` — Удалить поле
- `GET    /api/battlefields-state/{id}` — Полное состояние поля

### Юниты
- `POST   /api/units` — Разместить юнит
- `GET    /api/units/{id}` — Информация о юните
- `POST   /api/units/{id}/move` — Переместить юнит
- `POST   /api/units/{id}/shoot` — Стрельба по координатам
- `DELETE /api/units/{id}` — Удалить юнит

### Объекты и лог
- `POST   /api/objects` — Разместить объект
- `GET    /api/objects/{id}` — Информация об объекте
- `DELETE /api/objects/{id}` — Удалить объект
- `GET    /api/combat-log/{battlefieldId}` — Журнал боевых действий

---

## Примеры запросов (curl)

# Создать поле 20×20
curl -X POST http://localhost:8080/battlefield/api/battlefields \
  -H "Content-Type: application/json" \
  -d '{"name":"Kursk","width":20,"height":20}'

# Разместить танк на (5,5)
curl -X POST http://localhost:8080/battlefield/api/units \
  -H "Content-Type: application/json" \
  -d '{"battlefieldId":1,"name":"Tiger I","unitType":"TANK","x":5,"y":5}'

# Разместить пехоту на (10,5)
curl -X POST http://localhost:8080/battlefield/api/units \
  -H "Content-Type: application/json" \
  -d '{"battlefieldId":1,"name":"Rifleman","unitType":"INFANTRY","x":10,"y":5}'

# Переместить танк на EAST
curl -X POST http://localhost:8080/battlefield/api/units/1/move \
  -H "Content-Type: application/json" \
  -d '{"direction":"EAST"}'

# Танк стреляет по (10,5)
curl -X POST http://localhost:8080/battlefield/api/units/1/shoot \
  -H "Content-Type: application/json" \
  -d '{"targetX":10,"targetY":5}'

# Полное состояние поля
curl http://localhost:8080/battlefield/api/battlefields-state/1

# Журнал боя
curl http://localhost:8080/battlefield/api/combat-log/1

# Архитектура и ООП: Моделирование игровых объектов

## Иерархия классов

Ниже представлена иерархия классов для моделирования игровых объектов. Абстрактные классы выделены **жирным** начертанием, а интерфейсы, которые реализуют классы, указаны после ключевого слова `implements`.

*   `GameObject` (абстрактный)
    *   **`BattleUnit`** (абстрактный, implements `Damageable`, `Shootable`)
        *   `Tank` (implements `Movable`) — Размер: 2×2, скорость: 3, HP: 150
        *   `Infantry` (implements `Movable`) — Размер: 1×1, скорость: 2, HP: 50
        *   `Artillery` (implements `Movable`) — Размер: 2×1, скорость: 1, дальность: 8, направление: только NORTH
        *   `Sniper` (implements `Movable`) — Размер: 1×1, скорость: 2, дальность: 10, HP: 30
        *   `APC` (implements `Movable`) — Размер: 2×1, скорость: 4, HP: 100
        *   `Turret` (**НЕ** implements `Movable`!) — Размер: 1×1, скорость: 0, HP: 120
    *   `StaticObject` (implements `Damageable`)

## Интерфейсы

Интерфейсы определяют контракты поведения для объектов.

*   **`Damageable`** — определяет возможность получения урона.
    *   `getHealth()`
    *   `getMaxHealth()`
    *   `takeDamage(int)`
    *   `isDestroyed()`
*   **`Movable`** — определяет способность к перемещению. `Turret` этот интерфейс **не реализует**.
    *   `getSpeed()`
    *   `move(Direction)`
*   **`Shootable`** — определяет способность к стрельбе.
    *   `getAttackRange()`
    *   `getDamage()`
    *   `getFireDirection()`
    *   `canShootAt(Position)`

## Применяемые паттерны проектирования

*   **Factory Method** — `UnitFactory.create(UnitType, ...)` для создания различных типов юнитов.
*   **Template Method** — `BaseServlet.handleRequest()` для единообразной обработки запросов и ошибок в сервлетах.
*   **Repository** — Абстрагирует слой доступа к данным (JDBC), скрывая детали работы с базой данных.
*   **IoC / DI** — `ServiceContainer` + внедрение зависимостей через конструктор (см. пример ниже).

## Пример Dependency Injection (DI) в контексте веб-приложения

Фрагмент кода (`AppContextListener.java`) демонстрирует сборку зависимостей с использованием внедрения через конструктор и регистрацию сервисов в контейнере (IoC).

```java
// Создание и настройка зависимостей (компоновка графа объектов)
DatabaseManager dbManager = new DatabaseManager();

// Внедрение зависимостей через конструктор
BattlefieldRepository bfRepo = new BattlefieldRepositoryImpl(dbManager);     // DI
UnitRepository unitRepo = new UnitRepositoryImpl(dbManager);                  // DI

// Сервис, зависящий от репозиториев
CombatService combatService = new CombatServiceImpl(bfRepo, unitRepo, ...);  // DI

// Регистрация готовых сервисов в контейнере для последующего использования (IoC)
container.register(CombatService.class, combatService);
```

### Технологии
Java 17, Jakarta Servlet 6.0, H2 Database (in-memory), HikariCP, Gson, SLF4J + Logback, Maven, Apache Tomcat 10.1+