package com.battlefield.servlet;

import com.battlefield.service.CombatService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /api/combat-log/{battlefieldId} — журнал боевых действий
 */
@WebServlet(urlPatterns = "/api/combat-log/*")
public class CombatServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp, (rq, rs) -> {
            Long bfId = extractId(rq);
            if (bfId == null) throw new IllegalArgumentException("Укажите ID поля");
            sendJson(rs, 200, getService(CombatService.class).getCombatLog(bfId));
        });
    }
}
