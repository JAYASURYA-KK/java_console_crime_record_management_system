package com.crimemanagement.controller;

import com.crimemanagement.model.Crime;
import com.crimemanagement.service.CrimeService;
import com.crimemanagement.service.SharedServiceHolder;
import com.crimemanagement.service.SearchService;
import com.crimemanagement.service.WebSocketService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class WebController {
    
    private final CrimeService crimeService;
    private final SearchService searchService;
    
    public WebController(@org.springframework.beans.factory.annotation.Qualifier("webSocketNotifier") WebSocketService webSocketService) {
        CrimeService shared = SharedServiceHolder.getCrimeService();
        if (shared != null) {
            this.crimeService = shared;
            this.crimeService.setWebSocketService(webSocketService);
        } else {
            this.crimeService = new CrimeService(webSocketService);
            SharedServiceHolder.setCrimeService(this.crimeService);
        }
        this.searchService = new SearchService();
    }
    
    @GetMapping("/photo/{id}")
    public ResponseEntity<FileSystemResource> getPhoto(@PathVariable String id) {
        Crime crime = crimeService.getCrimeById(id);
        if (crime != null && crime.getPhotoPath() != null) {
            File file = new File(crime.getPhotoPath());
            if (file.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(new FileSystemResource(file));
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Serve default image when a record has no photo
    @GetMapping(value = "/defaultcriminal.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<FileSystemResource> getDefaultPhoto() {
        try {
            // Try to serve from working directory first (project root)
            File file = new File("defaultcriminal.jpg");
            if (file.exists()) {
                return ResponseEntity.ok(new FileSystemResource(file));
            }
            // Fallback to classpath if available under resources/static
            ClassPathResource classpathResource = new ClassPathResource("static/defaultcriminal.jpg");
            if (classpathResource.exists()) {
                return ResponseEntity.ok(new FileSystemResource(classpathResource.getFile()));
            }
        } catch (Exception ignored) {
        }
        return ResponseEntity.notFound().build();
    }

    // Serve favicon used by the web page and notifications
    @GetMapping(value = "/favico.ico")
    public ResponseEntity<FileSystemResource> getFavicon() {
        try {
            File file = new File("favico.ico");
            if (file.exists()) {
                return ResponseEntity.ok(new FileSystemResource(file));
            }
            ClassPathResource classpathResource = new ClassPathResource("static/favico.ico");
            if (classpathResource.exists()) {
                return ResponseEntity.ok(new FileSystemResource(classpathResource.getFile()));
            }
        } catch (Exception ignored) {
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/crime-records")
    public String crimeRecords(
            @RequestParam(value = "search", required = false) String searchTerm,
            @RequestParam(value = "searchType", required = false, defaultValue = "name") String searchType,
            Model model) {
        
        List<Crime> crimes;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Perform search based on type
            switch (searchType.toLowerCase()) {
                case "id":
                    Crime crime = crimeService.getCrimeById(searchTerm);
                    crimes = crime != null ? List.of(crime) : List.of();
                    break;
                case "city":
                    crimes = searchService.searchByCity(searchTerm);
                    break;
                case "crimetype":
                    crimes = searchService.searchByCrimeType(searchTerm);
                    break;
                case "details":
                    crimes = searchService.searchByDetails(searchTerm);
                    break;
                case "name":
                default:
                    crimes = searchService.searchByName(searchTerm);
                    break;
            }
            model.addAttribute("searchTerm", searchTerm);
            model.addAttribute("searchType", searchType);
        } else {
            // Get all crimes in FIFO order (latest first using Stack behavior)
            crimes = crimeService.getAllCrimesInFIFOOrder();
        }
        
        // Sort newest first (createdAt descending)
        crimes = new ArrayList<>(crimes);
        crimes.sort(Comparator.comparing(Crime::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        // Group by date: Today, Yesterday, Earlier
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<Crime> todayCrimes = new ArrayList<>();
        List<Crime> yesterdayCrimes = new ArrayList<>();
        List<Crime> earlierCrimes = new ArrayList<>();
        for (Crime c : crimes) {
            if (c.getCreatedAt() == null) {
                earlierCrimes.add(c);
                continue;
            }
            LocalDate d = c.getCreatedAt().toLocalDate();
            if (d.isEqual(today)) todayCrimes.add(c);
            else if (d.isEqual(yesterday)) yesterdayCrimes.add(c);
            else earlierCrimes.add(c);
        }

        model.addAttribute("crimes", crimes);
        model.addAttribute("totalRecords", crimes.size());
        model.addAttribute("todayCrimes", todayCrimes);
        model.addAttribute("yesterdayCrimes", yesterdayCrimes);
        model.addAttribute("earlierCrimes", earlierCrimes);
        
        return "crime-records";
    }
}
