package com.battlefield.servlet;

import com.battlefield.model.BattleUnit;
import com.battlefield.model.ShotResult;
import com.battlefield.service.CombatService;
import com.battlefield.service.UnitService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * POST   /api/units              — разместить юнит
 * GET    /api/units/{id}          — информация о юните
 * POST   /api/units/{id}/move     — переместить
 * POST   /api/units/{id}/shoot    — стрелять по координатам
 * DELETE /api/units/{id}          — удалить
 */
@WebServlet(urlPatterns = "/api/units/*")
public class UnitServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long id = extractId(rq);
            String action = extractAction(rq);

            if (id == null) {
                // POST /api/units — создать юнит
                JsonObject body = readJsonBody(rq);
                long bfId = body.get("battlefieldId").getAsLong();
                String name = body.get("name").getAsString();
                String unitType = body.get("unitType").getAsString();
                int x = body.get("x").getAsInt();
                int y = body.get("y").getAsInt();
                BattleUnit unit = getService(UnitService.class).place(bfId, name, unitType, x, y);
                sendJson(rs, 201, unit);

            } else if ("move".equals(action)) {
                JsonObject body = readJsonBody(rq);
                String direction = body.get("direction").getAsString();
                Map<String, Object> result = getService(UnitService.class).move(id, direction);
                int status = Boolean.TRUE.equals(result.get("success")) ? 200 : 400;
                sendJson(rs, status, result);

            } else if ("shoot".equals(action)) {
                JsonObject body = readJsonBody(rq);
                int tx = body.get("targetX").getAsInt();
                int ty = body.get("targetY").getAsInt();
                ShotResult result = getService(CombatService.class).shoot(id, tx, ty);
                sendJson(rs, 200, result);

            } else {
                throw new IllegalArgumentException("Неизвестное действие: " + action);
            }
        });
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long id = extractId(rq);
            if (id == null) throw new IllegalArgumentException("Укажите ID юнита");
            BattleUnit unit = getService(UnitService.class).getById(id)
                    .orElseThrow(() -> new NoSuchElementException("Юнит не найден: " + id));
            sendJson(rs, 200, unit);
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long id = extractId(rq);
            if (id == null) throw new IllegalArgumentException("Укажите ID юнита");
            getService(UnitService.class).delete(id);
            sendJson(rs, 200, Map.of("deleted", true, "id", id));
        });
    }
}
