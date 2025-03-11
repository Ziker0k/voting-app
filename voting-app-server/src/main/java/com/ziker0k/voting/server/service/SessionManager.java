package com.ziker0k.voting.server.service;

import com.ziker0k.voting.server.model.UserSession;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();

    private static final Map<ChannelHandlerContext, UserSession> sessions = new ConcurrentHashMap<>();
    private static final Map<String, UserSession> activeUsers = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param username - имя пользователя.
     * @param session  - сессия пользователя.
     * @return true, если регистрация успешна, false - если пользователь уже зарегистрирован.
     */
    public boolean registerUser(String username, UserSession session) {
        return activeUsers.putIfAbsent(username, session) == null;  // Пользователь уже существует.
    }

    /**
     * Проверка, является ли пользователь авторизованным.
     *
     * @param username - имя пользователя.
     * @return true, если пользователь авторизован.
     */
    public boolean isUserLoggedIn(String username) {
        return activeUsers.containsKey(username);
    }

    /**
     * Получение сессии для данного ChannelHandlerContext.
     *
     * @param ctx - контекст канала.
     * @return сессия пользователя.
     */
    public UserSession getSession(ChannelHandlerContext ctx) {
        return sessions.computeIfAbsent(ctx, UserSession::new);
    }

    /**
     * Уничтожение сессии пользователя.
     *
     * @param session - сессия, которую необходимо удалить.
     */
    public void destroySession(UserSession session) {
        // Удаление пользователя из activeUsers и сессии
        String username = session.getUsername();
        if (username != null) {
            activeUsers.remove(username); // Удаляем пользователя из списка активных
        }
        sessions.remove(session.getChannelHandlerContext()); // Удаляем сессию по контексту
    }

    /**
     * Возвращает все активные сессии.
     *
     * @return активные сессии.
     */
    public Map<ChannelHandlerContext, UserSession> getSessions() {
        return sessions;
    }

    public void clear() {
        sessions.clear();
        activeUsers.clear();
    }
}