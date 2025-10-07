package com.crimemanagement.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ASCIIArtGenerator {
    
    // ASCII characters from darkest to lightest
    private static final String ASCII_CHARS = "@%#*+=-:. ";
    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 40;
    
    public static void displayPhotoAsASCII(String imagePath) {
        displayPhotoAsASCII(imagePath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public static void displayPhotoAsASCII(String imagePath, int width, int height) {
        try {
            System.out.println("\n=== CRIMINAL PHOTO (ASCII ART) ===");
            System.out.println("Loading image: " + imagePath);
            
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.out.println("Image file not found: " + imagePath);
                displayDefaultASCIIArt();
                return;
            }
            
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                System.out.println("Unable to read image file: " + imagePath);
                displayDefaultASCIIArt();
                return;
            }
            
            String asciiArt = convertImageToASCII(image, width, height);
            System.out.println(asciiArt);
            System.out.println("=".repeat(width));
            
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            displayDefaultASCIIArt();
        } catch (Exception e) {
            System.err.println("Error generating ASCII art: " + e.getMessage());
            displayDefaultASCIIArt();
        }
    }
    
    private static String convertImageToASCII(BufferedImage image, int width, int height) {
        // Resize image to desired dimensions
        BufferedImage resizedImage = resizeImage(image, width, height);
        
        StringBuilder asciiArt = new StringBuilder();
        
        for (int y = 0; y < resizedImage.getHeight(); y++) {
            for (int x = 0; x < resizedImage.getWidth(); x++) {
                int rgb = resizedImage.getRGB(x, y);
                
                // Extract RGB components
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                
                // Calculate brightness (grayscale)
                int brightness = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                
                // Map brightness to ASCII character
                char asciiChar = getASCIIChar(brightness);
                asciiArt.append(asciiChar);
            }
            asciiArt.append('\n');
        }
        
        return asciiArt.toString();
    }
    
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImage.createGraphics();
        
        // Set rendering hints for better quality
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();
        
        return resizedImage;
    }
    
    private static char getASCIIChar(int brightness) {
        // Map brightness (0-255) to ASCII character index
        int index = (brightness * (ASCII_CHARS.length() - 1)) / 255;
        return ASCII_CHARS.charAt(index);
    }
    
    private static void displayDefaultASCIIArt() {
        System.out.println("\n=== DEFAULT CRIMINAL PHOTO (ASCII ART) ===");
        System.out.println("                    @@@@@@@@@@@@@@@@@@@@");
        System.out.println("                @@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("              @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("          @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("      @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("          @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("            @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("              @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("                @@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("                    @@@@@@@@@@@@@@@@@@@@");
        System.out.println("Photo not available - Default placeholder shown");
        System.out.println("================================================================================");
    }
    
    public static void displayASCIIBanner() {
        System.out.println("  ██████╗██████╗ ██╗███╗   ███╗███████╗    ██████╗ ███████╗ ██████╗ ██████╗ ██████╗ ██████╗ ");
        System.out.println(" ██╔════╝██╔══██╗██║████╗ ████║██╔════╝    ██╔══██╗██╔════╝██╔════╝██╔═══██╗██╔══██╗██╔══██╗");
        System.out.println(" ██║     ██████╔╝██║██╔████╔██║█████╗      ██████╔╝█████╗  ██║     ██║   ██║██████╔╝██║  ██║");
        System.out.println(" ██║     ██╔══██╗██║██║╚██╔╝██║██╔══╝      ██╔══██╗██╔══╝  ██║     ██║   ██║██╔══██╗██║  ██║");
        System.out.println(" ╚██████╗██║  ██║██║██║ ╚═╝ ██║███████╗    ██║  ██║███████╗╚██████╗╚██████╔╝██║  ██║██████╔╝");
        System.out.println("  ╚═════╝╚═╝  ╚═╝╚═╝╚═╝     ╚═╝╚══════╝    ╚═╝  ╚═╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚═════╝ ");
        System.out.println();
        System.out.println(" ███╗   ███╗ █████╗ ███╗   ██╗ █████╗  ██████╗ ███████╗███╗   ███╗███████╗███╗   ██╗████████╗");
        System.out.println(" ████╗ ████║██╔══██╗████╗  ██║██╔══██╗██╔════╝ ██╔════╝████╗ ████║██╔════╝████╗  ██║╚══██╔══╝");
        System.out.println(" ██╔████╔██║███████║██╔██╗ ██║███████║██║  ███╗█████╗  ██╔████╔██║█████╗  ██╔██╗ ██║   ██║   ");
        System.out.println(" ██║╚██╔╝██║██╔══██║██║╚██╗██║██╔══██║██║   ██║██╔══╝  ██║╚██╔╝██║██╔══╝  ██║╚██╗██║   ██║   ");
        System.out.println(" ██║ ╚═╝ ██║██║  ██║██║ ╚████║██║  ██║╚██████╔╝███████╗██║ ╚═╝ ██║███████╗██║ ╚████║   ██║   ");
        System.out.println(" ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝   ");
        System.out.println();
        System.out.println("                                    SYSTEM v1.0");
        System.out.println("================================================================================");
    }
    
    public static void testASCIIGeneration() {
        System.out.println("\n=== ASCII ART GENERATOR TEST ===");
        
        // Test with different sizes
        System.out.println("Testing ASCII art generation with different sizes:");
        
        // Small size
        System.out.println("\n1. Small size (40x20):");
        displayTestPattern(40, 20);
        
        // Medium size
        System.out.println("\n2. Medium size (60x30):");
        displayTestPattern(60, 30);
        
        // Large size
        System.out.println("\n3. Large size (80x40):");
        displayTestPattern(80, 40);
    }
    
    private static void displayTestPattern(int width, int height) {
        StringBuilder pattern = new StringBuilder();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Create a gradient pattern
                int brightness = (x * 255) / width;
                char asciiChar = getASCIIChar(brightness);
                pattern.append(asciiChar);
            }
            pattern.append('\n');
        }
        
        System.out.println(pattern.toString());
        System.out.println("=".repeat(width));
    }
    
    public static boolean isImageFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".jpg") || 
               lowerPath.endsWith(".jpeg") || 
               lowerPath.endsWith(".png") || 
               lowerPath.endsWith(".gif") || 
               lowerPath.endsWith(".bmp");
    }
    
    public static void displayPhotoWithOptions(String imagePath) {
        if (!isImageFile(imagePath)) {
            System.out.println("Invalid image file format. Supported formats: JPG, JPEG, PNG, GIF, BMP");
            displayDefaultASCIIArt();
            return;
        }
        
        System.out.println("\n=== ASCII ART DISPLAY OPTIONS ===");
        System.out.println("1. Small (40x20)");
        System.out.println("2. Medium (60x30)");
        System.out.println("3. Large (80x40)");
        System.out.println("4. Extra Large (100x50)");
        
        int choice = InputValidator.getValidInt("Select size (1-4): ", 1, 4);
        
        switch (choice) {
            case 1:
                displayPhotoAsASCII(imagePath, 40, 20);
                break;
            case 2:
                displayPhotoAsASCII(imagePath, 60, 30);
                break;
            case 3:
                displayPhotoAsASCII(imagePath, 80, 40);
                break;
            case 4:
                displayPhotoAsASCII(imagePath, 100, 50);
                break;
        }
    }
}
