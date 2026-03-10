package com.battlefield.servlet;

import com.battlefield.di.ServiceContainer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/**
 * Базовый сервлет: JSON-утилиты, обработка ошибок, доступ к DI-контейнеру.
 */
public abstract class BaseServlet extends HttpServlet {

    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    protected <T> T getService(Class<T> type) {
        return ServiceContainer.getInstance().resolve(type);
    }

    protected JsonObject readJsonBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return GSON.fromJson(sb.toString(), JsonObject.class);
    }

    protected void sendJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.print(GSON.toJson(data));
        }
    }

    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        JsonObject error = new JsonObject();
        error.addProperty("error", message);
        sendJson(resp, status, error);
    }

    protected Long extractId(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null || path.equals("/")) return null;
        String[] parts = path.split("/");
        if (parts.length >= 2) {
            try { return Long.parseLong(parts[1]); }
            catch (NumberFormatException e) { return null; }
        }
        return null;
    }

    protected String extractAction(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) return null;
        String[] parts = path.split("/");
        if (parts.length >= 3) return parts[2];
        return null;
    }

    /** Шаблонный метод обработки запроса с единой обработкой ошибок. */
    protected void handleRequest(HttpServletRequest req, HttpServletResponse resp,
                                 RequestHandler handler) throws IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        try {
            handler.handle(req, resp);
        } catch (NoSuchElementException e) {
            sendError(resp, 404, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendError(resp, 400, e.getMessage());
        } catch (IllegalStateException e) {
            sendError(resp, 409, e.getMessage());
        } catch (Exception e) {
            sendError(resp, 500, "Внутренняя ошибка: " + e.getMessage());
        }
    }

    @FunctionalInterface
    protected interface RequestHandler {
        void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception;
    }
}
