package com.crimemanagement.service;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.crimemanagement.CrimeManagementWebApplication;

import java.awt.Desktop;
import java.net.URI;

public class WebLauncherService {
    private static ConfigurableApplicationContext webContext;
    private static boolean isWebServerRunning = false;
    private static int serverPort = 8080;
    private static volatile boolean browserOpened = false;
    
    public static void launchWebInterface() {
        try {
            if (!isWebServerRunning) {
                System.out.println("\n=== LAUNCHING WEB INTERFACE ===");
                System.out.println("Starting Spring Boot web server...");
                
                // Find an available port
                serverPort = findAvailablePort();
                
                // Set system property for server.port
                System.setProperty("server.port", String.valueOf(serverPort));
                
                // Start Spring Boot application in a separate thread
                Thread webThread = new Thread(() -> {
                    try {
                        webContext = SpringApplication.run(CrimeManagementWebApplication.class);
                        
                        // Get the WebSocket service from the web context and share it
                        WebSocketService webSocketService = webContext.getBean(WebSocketService.class);
                        SharedServiceHolder.setWebSocketService(webSocketService);
                        
                        // Update existing crime service with the web socket service
                        CrimeService existingCrimeService = SharedServiceHolder.getCrimeService();
                        if (existingCrimeService != null) {
                            existingCrimeService.setWebSocketService(webSocketService);
                        }
                        
                        isWebServerRunning = true;
                        if (!browserOpened) {
                            browserOpened = true;
                            System.out.println("Web server started successfully on port " + serverPort + "!");
                            System.out.println("Opening browser...");
                            openBrowser("http://localhost:" + serverPort + "/crime-records");
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to start web server on port " + serverPort + ": " + e.getMessage());
                        serverPort = findAvailablePort();
                        System.setProperty("server.port", String.valueOf(serverPort));
                        
                        // Retry once with new port
                        webContext = SpringApplication.run(CrimeManagementWebApplication.class);
                        WebSocketService webSocketService = webContext.getBean(WebSocketService.class);
                        SharedServiceHolder.setWebSocketService(webSocketService);
                        
                        CrimeService existingCrimeService = SharedServiceHolder.getCrimeService();
                        if (existingCrimeService != null) {
                            existingCrimeService.setWebSocketService(webSocketService);
                        }
                        
                        isWebServerRunning = true;
                        if (!browserOpened) {
                            browserOpened = true;
                            System.out.println("Web server started successfully on port " + serverPort + "!");
                            System.out.println("Opening browser...");
                            openBrowser("http://localhost:" + serverPort + "/crime-records");
                        }
                    }
                });
                webThread.setDaemon(true);
                webThread.start();
                
                // Browser will be opened by the web thread when the server is ready
                
            } else {
                System.out.println("Web interface is already running on port " + serverPort + ".");
                System.out.println("Opening browser...");
                openBrowser("http://localhost:" + serverPort + "/crime-records");
            }
            
        } catch (Exception e) {
            System.err.println("Error launching web interface: " + e.getMessage());
            System.out.println("You can manually open: http://localhost:8080/crime-records");
        }
    }
    
    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                    System.out.println("Browser opened with URL: " + url);
                    return;
                }
            }
            
            // Fallback for different operating systems
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", url).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else if (os.contains("nix") || os.contains("nux")) {
                new ProcessBuilder("xdg-open", url).start();
            }
            
        } catch (Exception e) {
            System.out.println("Could not automatically open browser.");
            System.out.println("Please manually open: " + url);
        }
    }
    
    public static void stopWebInterface() {
        if (isWebServerRunning && webContext != null) {
            System.out.println("Stopping web server...");
            webContext.close();
            isWebServerRunning = false;
            System.out.println("Web server stopped.");
        }
    }
    
    public static boolean isRunning() {
        return isWebServerRunning;
    }
    
    private static int findAvailablePort() {
        try (java.net.ServerSocket socket = new java.net.ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (Exception e) {
            return 8090; // Fallback port
        }
    }
}
