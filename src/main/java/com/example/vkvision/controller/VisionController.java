package com.example.vkvision.controller;

import com.example.vkvision.model.VisionResponse;
import com.example.vkvision.service.VisionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Slf4j
@Controller
public class VisionController {

    @Autowired
    private VisionService visionService;

    @GetMapping("/")
    public String showUploadForm() {
        return "upload";
    }


    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("image") MultipartFile file, Model model) {

        model.addAttribute("success");
        if (file.isEmpty()) {
            model.addAttribute("error", "Пожалуйста, выберите файл для загрузки.");
            return "upload";
        }

        try {
            byte[] bytes = file.getBytes();
            Object response = visionService.detectObjects(bytes, file.getOriginalFilename());

            // Преобразуем изображение в base64 для отображения
            String imageBase64 = Base64.getEncoder().encodeToString(bytes);

            model.addAttribute("response", response);
            model.addAttribute("fileName", file.getOriginalFilename());
            model.addAttribute("success", true);
            model.addAttribute("imageBase64", imageBase64);


        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обработке изображения: " + e.getMessage());
            e.printStackTrace();
        }

        return "upload";
    }
}
