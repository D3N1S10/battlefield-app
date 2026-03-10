Battlefield Simulator — Java Servlet Application
Автор: Далецкий Денис, 3 курс, группа 14.2
Репозиторий: github.com/D3N1S10/battlefield-app

Моделирование поля боя на сетке клеток. Боевые юниты различных типов занимают одну или несколько клеток, двигаются с разной скоростью, стреляют в различных направлениях. При попадании юнит повреждается или уничтожается.

API Endpoints (13 шт.)
Поля боя
POST /api/battlefields — Создать поле боя

GET /api/battlefields — Список всех полей

GET /api/battlefields/{id} — Получить поле по ID

DELETE /api/battlefields/{id} — Удалить поле

GET /api/battlefields-state/{id} — Полное состояние поля

Юниты
POST /api/units — Разместить юнит

GET /api/units/{id} — Информация о юните

POST /api/units/{id}/move — Переместить юнит

POST /api/units/{id}/shoot — Стрельба по координатам

DELETE /api/units/{id} — Удалить юнит

Объекты и лог
POST /api/objects — Разместить объект

GET /api/objects/{id} — Информация об объекте

DELETE /api/objects/{id} — Удалить объект

GET /api/combat-log/{battlefieldId} — Журнал боевых действий

Примеры запросов (curl)
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
Архитектура и ООП
Иерархия классов
GameObject (abstract)
   ├── BattleUnit (abstract) implements Damageable, Shootable
   │       ├── Tank implements Movable          — 2×2, speed=3, HP=150
   │       ├── Infantry implements Movable      — 1×1, speed=2, HP=50
   │       ├── Artillery implements Movable     — 2×1, speed=1, range=8, NORTH only
   │       ├── Sniper implements Movable        — 1×1, speed=2, range=10, HP=30
   │       ├── APC implements Movable           — 2×1, speed=4, HP=100
   │       └── Turret (БЕЗ Movable!)           — 1×1, speed=0, HP=120
   └── StaticObject implements Damageable
Интерфейсы
Damageable — getHealth(), getMaxHealth(), takeDamage(int), isDestroyed()

Movable — getSpeed(), move(Direction) (Turret не реализует)

Shootable — getAttackRange(), getDamage(), getFireDirection(), canShootAt(Position)

Паттерны
Factory Method — UnitFactory.create(UnitType, ...)

Template Method — BaseServlet.handleRequest() для единой обработки ошибок

Repository — абстрагирует JDBC

IoC / DI — ServiceContainer + constructor injection

Dependency Injection
// AppContextListener.java — точка сборки (constructor injection)
DatabaseManager dbManager = new DatabaseManager();
BattlefieldRepository bfRepo = new BattlefieldRepositoryImpl(dbManager);     // DI
UnitRepository unitRepo = new UnitRepositoryImpl(dbManager);                  // DI
CombatService combatService = new CombatServiceImpl(bfRepo, unitRepo, ...);  // DI
container.register(CombatService.class, combatService);                      // IoC
Технологии
Java 17, Jakarta Servlet 6.0, H2 Database (in-memory), HikariCP, Gson, SLF4J + Logback, Maven, Apache Tomcat 10.1+