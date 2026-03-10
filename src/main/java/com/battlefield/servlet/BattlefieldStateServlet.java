package com.battlefield.servlet;

import com.battlefield.service.BattlefieldService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /api/battlefields-state/{id} — полное состояние поля (юниты + объекты)
 */
@WebServlet(urlPatterns = "/api/battlefields-state/*")
public class BattlefieldStateServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long id = extractId(rq);
            if (id == null) throw new IllegalArgumentException("Укажите ID поля");
            sendJson(rs, 200, getService(BattlefieldService.class).getState(id));
        });
    }
}
