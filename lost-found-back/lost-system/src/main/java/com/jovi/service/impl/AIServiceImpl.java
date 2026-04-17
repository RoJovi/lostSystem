package com.jovi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jovi.pojo.AICompleteRequest;
import com.jovi.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.model}")
    private String model;

    @Value("${ai.api.max-tokens:200}")
    private int maxTokens;

    @Value("${ai.api.temperature:0.7}")
    private double temperature;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String completeDescription(AICompleteRequest request) {
        String prompt = buildPrompt(request);
        return callAI(prompt);
    }

    @Override
    public String analyzeStatistics(String prompt) {
        return callAI(prompt);
    }

    private String buildPrompt(AICompleteRequest request) {
        String typeText = request.getType() == 0 ? "丢失" : "拾到";
        String hasDesc = (request.getDescription() != null && !request.getDescription().isEmpty())
                ? "用户已填写描述：" + request.getDescription() + "，请在此基础上补充完善。"
                : "";

        return String.format(
                "你是失物招领平台的AI助手。用户%s物品「%s」。%s请生成一段50-100字的详细描述，直接输出描述内容。",
                typeText, request.getTitle(), hasDesc
        );
    }

    private String callAI(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String requestBody = String.format(
                    "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}],\"max_tokens\":%d,\"temperature\":%f}",
                    model, escapeJson(prompt), maxTokens, temperature
            );

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return extractContent(response.getBody());
            }
            return fallbackPrompt(prompt);
        } catch (Exception e) {
            log.error("AI API异常: {}", e.getMessage());
            return fallbackPrompt(prompt);
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String extractContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText().trim();
        } catch (Exception e) {
            return fallbackPrompt(null);
        }
    }

    private String fallbackPrompt(String prompt) {
        return "请提供更详细的物品描述，如颜色、品牌、特征等，以便更快找到失主。";
    }
}