
package com.qurilish;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@RestController
public class UploadController {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws Exception {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadUrl = supabaseUrl + "/storage/v1/object/public/images/" + filename;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(supabaseUrl + "/storage/v1/object/images/" + filename))
            .header("Authorization", "Bearer " + supabaseKey)
            .header("Content-Type", file.getContentType())
            .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return ResponseEntity.ok(uploadUrl);
        } else {
            return ResponseEntity.status(response.statusCode()).body(response.body());
        }
    }
}
