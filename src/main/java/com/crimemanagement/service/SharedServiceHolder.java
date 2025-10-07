package com.crimemanagement.service;

public class SharedServiceHolder {
    private static WebSocketService webSocketService;
    private static CrimeService crimeService;

    public static void setWebSocketService(WebSocketService service) {
        webSocketService = service;
    }

    public static WebSocketService getWebSocketService() {
        return webSocketService;
    }

    public static void setCrimeService(CrimeService service) {
        crimeService = service;
    }

    public static CrimeService getCrimeService() {
        return crimeService;
    }
}