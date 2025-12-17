package com.example.vkvision.service;

import com.example.vkvision.model.VisionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;


@Service
public class VisionService {

    @Value("${vk.oauth.token}")
    private String oauthToken;

    private static final String API_URL = "https://smarty.mail.ru/api/v1/objects/detect";

    private final RestTemplate restTemplate;

    public VisionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object detectObjects(byte[] imageBytes, String fileName) {
        // 1. Формируем URI с параметрами
        String url = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("oauth_token", oauthToken)
                .queryParam("oauth_provider", "mcs")
                .toUriString();

        // 2. Формируем multipart/form-data тело
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // JSON-часть
        String metaJson = """
            {
              "mode": ["multiobject"],
              "images": [{"name": "file"}]
            }
            """;

        // Бинарная часть (изображение)
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("meta", metaJson);
        body.add("file", imageResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 3. Отправляем запрос
        ResponseEntity<Object> response;
        try {
            response = restTemplate.postForEntity(url, requestEntity, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при вызове VK Vision API", e);
        }

        // 4. Проверяем статус
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("HTTP error: " + response.getStatusCode());
        }

        // 5. Возвращаем результат
        return response.getBody();
    }
}