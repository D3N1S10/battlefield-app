package com.battlefield.servlet;

import com.battlefield.model.StaticObject;
import com.battlefield.service.StaticObjectService;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * POST   /api/objects       — разместить статический объект
 * GET    /api/objects/{id}   — информация об объекте
 * DELETE /api/objects/{id}   — удалить объект
 */
@WebServlet(urlPatterns = "/api/objects/*")
public class StaticObjectServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            JsonObject body = readJsonBody(rq);
            long bfId = body.get("battlefieldId").getAsLong();
            String name = body.get("name").getAsString();
            String objectType = body.get("objectType").getAsString();
            int x = body.get("x").getAsInt();
            int y = body.get("y").getAsInt();
            StaticObject obj = getService(StaticObjectService.class).place(bfId, name, objectType, x, y);
            sendJson(rs, 201, obj);
        });
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long id = extractId(rq);
            if (id == null) throw new IllegalArgumentException("Укажите ID объекта");
            StaticObject obj = getService(StaticObjectService.class).getById(id)
                    .orElseThrow(() -> new NoSuchElementException("Объект не найден: " + id));
            sendJson(rs, 200, obj);
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long id = extractId(rq);
            if (id == null) throw new IllegalArgumentException("Укажите ID объекта");
            getService(StaticObjectService.class).delete(id);
            sendJson(rs, 200, Map.of("deleted", true, "id", id));
        });
    }
}
