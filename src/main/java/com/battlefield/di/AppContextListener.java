package com.battlefield.di;

import com.battlefield.repository.*;
import com.battlefield.repository.impl.*;
import com.battlefield.service.*;
import com.battlefield.service.impl.*;
import com.battlefield.util.DatabaseManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Инициализация приложения при старте сервера.
 * Создаёт все зависимости и связывает их через constructor injection.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);
    private DatabaseManager dbManager;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("=== Battlefield Simulator: запуск ===");

        ServiceContainer container = ServiceContainer.getInstance();

        // 1. Инфраструктура
        dbManager = new DatabaseManager();

        // 2. Repository слой — constructor injection от DatabaseManager
        BattlefieldRepository bfRepo = new BattlefieldRepositoryImpl(dbManager);
        UnitRepository unitRepo = new UnitRepositoryImpl(dbManager);
        StaticObjectRepository objRepo = new StaticObjectRepositoryImpl(dbManager);
        CombatLogRepository logRepo = new CombatLogRepositoryImpl(dbManager);

        // 3. Service слой — constructor injection от Repository
        BattlefieldService bfService = new BattlefieldServiceImpl(bfRepo, unitRepo, objRepo);
        UnitService unitService = new UnitServiceImpl(bfRepo, unitRepo, objRepo);
        StaticObjectService objService = new StaticObjectServiceImpl(bfRepo, unitRepo, objRepo);
        CombatService combatService = new CombatServiceImpl(bfRepo, unitRepo, objRepo, logRepo);

        // 4. Регистрация в DI-контейнере
        container.register(BattlefieldService.class, bfService);
        container.register(UnitService.class, unitService);
        container.register(StaticObjectService.class, objService);
        container.register(CombatService.class, combatService);

        log.info("=== Battlefield Simulator: готов к работе ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("=== Battlefield Simulator: остановка ===");
        if (dbManager != null) dbManager.shutdown();
        ServiceContainer.getInstance().clear();
    }
}
