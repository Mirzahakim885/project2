package com.example.Qurilish2.controller;

import com.example.Qurilish2.entity.ImageEntity;
import com.example.Qurilish2.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("api/image")
@CrossOrigin(origins = "*")
public class ImageController {
    private final String FILE_ROOT="FILES";
    @Autowired
    ImageService service;

    @GetMapping("/all")
    public ResponseEntity<List<ImageEntity>> list(){
        return new ResponseEntity<>(service.list(), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<ImageEntity> find(@PathVariable("id") Long id){
        return new ResponseEntity<>(service.findimage(id),HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ImageEntity> add(@ModelAttribute ImageEntity image,@RequestParam("imageInputs") MultipartFile file) throws IOException {
        ImageEntity image2 = new ImageEntity();
        image2.setImageTitle(image.getImageTitle());
        image2.setImageDesc(image.getImageDesc());
        image2.setImageUrl("/" + FILE_ROOT + "/" + file.getOriginalFilename());

        image = service.save(image2);
        saveFile(image,file);
        return new ResponseEntity<>(image,HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<ImageEntity> update(@ModelAttribute ImageEntity image,
                                              @RequestParam("imageInputs") MultipartFile file) throws IOException {
        ImageEntity existing = service.findimage(image.getImageId());
        if (existing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        existing.setImageTitle(image.getImageTitle());
        existing.setImageDesc(image.getImageDesc());
        existing.setImageUrl("/" + FILE_ROOT + "/" + file.getOriginalFilename());

        image = service.update(existing);
        saveFile(image, file);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id){
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/upload/{id}")
    public ResponseEntity<Resource> findImage(@PathVariable("id") Long id) {
        try {
            ImageEntity image = service.findimage(id);
            if (image == null || image.getImageUrl() == null) {
                return ResponseEntity.notFound().build();
            }

            String ext = "";
            if (image.getImageUrl().contains(".")) {
                ext = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("."));
            }

            Path filePath = new File(FILE_ROOT + "/images/" + id + ext).toPath();

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private void saveFile(ImageEntity image,@RequestParam("imageInputs") MultipartFile file) throws IOException {
        new File(FILE_ROOT + "/images").mkdir();
        String fileName = file.getOriginalFilename();
        String ext="";
        if (fileName != null && fileName.contains(".")){
            ext = fileName.substring(fileName.lastIndexOf("."));
        }
        File sf = new File(FILE_ROOT + "/images/" + image.getImageId() + ext);
        if (sf.createNewFile()) {
            FileOutputStream fos = new FileOutputStream(sf);
            BufferedOutputStream bw = new BufferedOutputStream(fos);
            bw.write(file.getBytes());
            bw.close();
            fos.close();
        }
    }

}
