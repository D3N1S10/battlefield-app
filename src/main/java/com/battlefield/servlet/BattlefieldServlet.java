package com.battlefield.servlet;

import com.battlefield.model.Battlefield;
import com.battlefield.service.BattlefieldService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * POST   /api/battlefields           — создать поле боя
 * GET    /api/battlefields            — список всех полей
 * GET    /api/battlefields/{id}       — получить поле по ID
 * DELETE /api/battlefields/{id}       — удалить поле
 */
@WebServlet(urlPatterns = "/api/battlefields/*")
public class BattlefieldServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            JsonObject body = readJsonBody(rq);
            String name = body.get("name").getAsString();
            int width = body.get("width").getAsInt();
            int height = body.get("height").getAsInt();
            Battlefield bf = getService(BattlefieldService.class).create(name, width, height);
            sendJson(rs, 201, bf);
        });
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long id = extractId(rq);
            BattlefieldService svc = getService(BattlefieldService.class);
            if (id == null) {
                sendJson(rs, 200, svc.getAll());
            } else {
                Battlefield bf = svc.getById(id)
                        .orElseThrow(() -> new NoSuchElementException("Поле не найдено: " + id));
                sendJson(rs, 200, bf);
            }
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long id = extractId(rq);
            if (id == null) throw new IllegalArgumentException("Укажите ID поля");
            getService(BattlefieldService.class).delete(id);
            sendJson(rs, 200, Map.of("deleted", true, "id", id));
        });
    }
}
