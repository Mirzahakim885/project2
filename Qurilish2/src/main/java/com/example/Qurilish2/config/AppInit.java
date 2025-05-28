package com.example.Qurilish2.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.io.*;
@Component
@AllArgsConstructor
public class AppInit implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File file=new File("FILES");
        file.mkdir();
    }
}
