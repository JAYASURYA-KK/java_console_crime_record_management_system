package com.crimemanagement.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service("webSocketNotifier")  // Give it a specific name to avoid conflicts
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyCrimeUpdate() {
        if (messagingTemplate != null) {
            try {
                // Send a detailed update message with timestamp and action type
                String updateMessage = String.format(
                    "{\"type\":\"update\",\"timestamp\":%d,\"action\":\"refresh\",\"message\":\"Crime records updated\"}",
                    System.currentTimeMillis()
                );
                messagingTemplate.convertAndSend("/topic/crimes", updateMessage);
                System.out.println("WebSocket notification sent successfully at: " + new java.util.Date());
                
                // Send another notification after a short delay to ensure clients receive it
                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        messagingTemplate.convertAndSend("/topic/crimes", updateMessage);
                    } catch (Exception e) {
                        // Ignore interruption
                    }
                }).start();
            } catch (Exception e) {
                System.err.println("Error sending WebSocket notification: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Warning: WebSocket messaging template is null");
            // Try to get a new instance from the shared holder
            WebSocketService sharedService = SharedServiceHolder.getWebSocketService();
            if (sharedService != null && sharedService != this) {
                sharedService.notifyCrimeUpdate();
            }
        }
    }
}