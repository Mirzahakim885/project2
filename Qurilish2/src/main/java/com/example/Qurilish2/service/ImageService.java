package com.example.Qurilish2.service;

import com.example.Qurilish2.entity.ImageEntity;
import com.example.Qurilish2.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    @Autowired
    ImageRepository repository;

    public List<ImageEntity> list(){
        return repository.findAll();
    }
    public ImageEntity findimage(Long id){
        return repository.findById(id).orElse(null);
    }
    public ImageEntity save(ImageEntity image){
        return repository.save(image);
    }

    public ImageEntity update(ImageEntity image){
        return repository.save(image);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
}
