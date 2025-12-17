package com.example.lab2.controller;

import com.example.lab2.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    // Главная страница
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Страница управления бакетами
    @GetMapping("/buckets")
    public String buckets(Model model) {
        List<String> buckets = s3Service.listBuckets();
        model.addAttribute("buckets", buckets);
        return "buckets";
    }

    // Создание бакета
    @PostMapping("/buckets/create")
    public String createBucket(@RequestParam String bucketName, RedirectAttributes redirectAttributes) {
        try {
            s3Service.createBucket(bucketName);
            redirectAttributes.addFlashAttribute("message", "Бакет '" + bucketName + "' успешно создан.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/buckets";
    }

    // Удаление бакета
    @PostMapping("/buckets/delete/{bucketName}")
    public String deleteBucket(@PathVariable String bucketName, RedirectAttributes redirectAttributes) {
        try {
            s3Service.deleteBucket(bucketName);
            redirectAttributes.addFlashAttribute("message", "Бакет '" + bucketName + "' успешно удален.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
        }
        return "redirect:/buckets";
    }

    // Страница управления файлами в бакете
    @GetMapping("/files/{bucketName}")
    public String files(@PathVariable String bucketName, Model model) {
        List<String> files = s3Service.listObjects(bucketName);
        model.addAttribute("bucketName", bucketName);
        model.addAttribute("files", files);
        return "files";
    }

    // Загрузка файла
    @PostMapping("/files/{bucketName}/upload")
    public String uploadFile(@PathVariable String bucketName,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Файл не выбран.");
            return "redirect:/files/" + bucketName;
        }

        try {
            s3Service.uploadFile(bucketName, file.getOriginalFilename(), file);
            redirectAttributes.addFlashAttribute("message", "Файл '" + file.getOriginalFilename() + "' загружен.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки: " + e.getMessage());
        }
        return "redirect:/files/" + bucketName;
    }

    // Удаление файла
    @PostMapping("/files/{bucketName}/delete/{fileName}")
    public String deleteFile(@PathVariable String bucketName,
                             @PathVariable String fileName,
                             RedirectAttributes redirectAttributes) {
        try {
            s3Service.deleteFile(bucketName, fileName);
            redirectAttributes.addFlashAttribute("message", "Файл '" + fileName + "' удален.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления: " + e.getMessage());
        }
        return "redirect:/files/" + bucketName;
    }
}