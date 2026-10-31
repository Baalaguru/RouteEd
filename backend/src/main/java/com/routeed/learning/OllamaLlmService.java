package com.routeed.learning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routeed.course.CourseModule;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OllamaLlmService {
    private final RestClient client;
    private final ObjectMapper objectMapper;
    private final String model;

    public OllamaLlmService(RestClient.Builder builder, ObjectMapper objectMapper,
                            @Value("${routeed.ollama.base-url}") String baseUrl,
                            @Value("${routeed.ollama.model}") String model) {
        this.client = builder.baseUrl(baseUrl).build(); this.objectMapper = objectMapper; this.model = model;
    }

    public PersonalizedRoute generate(List<CourseModule> modules, Map<String, BigDecimal> skillScores) {
        try {
            var moduleData = modules.stream().map(module -> Map.of("id", module.getId().toString(), "title", module.getTitle(), "skillTag", module.getSkillTag() == null ? "GENERAL" : module.getSkillTag(), "defaultOrderIndex", module.getDefaultOrder())).toList();
            String prompt = "You personalize learning paths. Return ONLY valid JSON with moduleOrder (all module UUIDs exactly once), skippedModuleIds (only UUIDs from modules), and aiRationale (UUID to concise reason). Skip only skills at >=80 mastery. Modules: " + objectMapper.writeValueAsString(moduleData) + ". Mastery percentages: " + objectMapper.writeValueAsString(skillScores);
            Map<String, Object> request = Map.of("model", model, "prompt", prompt, "stream", false, "format", "json", "options", Map.of("temperature", 0.1));
            JsonNode response = client.post().uri("/api/generate").contentType(MediaType.APPLICATION_JSON).body(request).retrieve().body(JsonNode.class);
            if (response == null || !response.hasNonNull("response")) throw new IllegalArgumentException("Ollama returned no response");
            JsonNode route = objectMapper.readTree(response.get("response").asText());
            return new PersonalizedRoute(readUuids(route.path("moduleOrder")), readUuids(route.path("skippedModuleIds")), objectMapper.convertValue(route.path("aiRationale"), objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)));
        } catch (Exception exception) { throw new OllamaUnavailableException(exception); }
    }
    public String weeklyEncouragement(String learnerName) {
        try {
            Map<String, Object> request = Map.of("model", model, "prompt", "Write exactly two encouraging sentences for " + learnerName + " about continuing an online course. Do not use markdown.", "stream", false, "options", Map.of("temperature", 0.4));
            JsonNode response = client.post().uri("/api/generate").contentType(MediaType.APPLICATION_JSON).body(request).retrieve().body(JsonNode.class);
            return response == null ? "Keep your momentum this week by completing one focused module." : response.path("response").asText("Keep your momentum this week by completing one focused module.").trim();
        } catch (Exception exception) { return "Keep your momentum this week by completing one focused module and taking the next step in your route."; }
    }
    private List<UUID> readUuids(JsonNode values) { if (!values.isArray()) throw new IllegalArgumentException("Invalid array"); return java.util.stream.StreamSupport.stream(values.spliterator(), false).map(value -> UUID.fromString(value.asText())).toList(); }
    public record PersonalizedRoute(List<UUID> moduleOrder, List<UUID> skippedModuleIds, Map<String, String> aiRationale) { }
    public static class OllamaUnavailableException extends RuntimeException { OllamaUnavailableException(Throwable cause) { super(cause); } }
}
