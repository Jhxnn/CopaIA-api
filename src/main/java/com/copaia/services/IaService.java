package com.copaia.services;

import com.copaia.dtos.gemini.*;
import com.copaia.models.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class IaService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.model}")
    private String model;

    @Value("${gemini.api.base-url}")
    private String baseUrl;

    private static final String SYSTEM_PROMPT = """
            Você é um especialista em análise e previsão de jogos da Copa do Mundo FIFA 2026.
            Seu papel é analisar partidas entre seleções nacionais e fornecer previsões detalhadas e fundamentadas.

            Ao analisar um jogo, considere:
            - Ranking FIFA atual dos times
            - Desempenho recente e forma das seleções
            - Histórico de confrontos diretos (head-to-head)
            - Estilo de jogo e características de cada seleção
            - Qualidade do elenco e principais jogadores

            Responda sempre em português brasileiro de forma envolvente e informativa.
            Apresente uma previsão clara indicando qual seleção tem mais chances de vencer e por quê.
            Quando houver informações de seleções via API externa, use-as como contexto adicional.
            """;

    public IaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateResponse(String userMessage, List<Message> history, String teamsContext) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Chave da API Gemini não configurada. Configure a variável de ambiente GEMINI_API_KEY.";
        }

        List<GeminiContentDto> contents = buildContents(history, userMessage);

        String systemText = SYSTEM_PROMPT;
        if (teamsContext != null && !teamsContext.isBlank()) {
            systemText += "\n\nContexto da API externa:\n" + teamsContext;
        }

        GeminiSystemInstructionDto systemInstruction = new GeminiSystemInstructionDto(
                List.of(new GeminiPartDto(systemText))
        );

        GeminiGenerationConfigDto generationConfig = new GeminiGenerationConfigDto(0.8, 2048);

        GeminiRequestDto request = new GeminiRequestDto(contents, systemInstruction, generationConfig);

        String url = baseUrl + "/" + model + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GeminiRequestDto> entity = new HttpEntity<>(request, headers);

        try {
            GeminiResponseDto response = restTemplate.postForObject(url, entity, GeminiResponseDto.class);

            if (response != null
                    && response.candidates() != null
                    && !response.candidates().isEmpty()
                    && response.candidates().get(0).content() != null
                    && !response.candidates().get(0).content().parts().isEmpty()) {
                return response.candidates().get(0).content().parts().get(0).text();
            }
            return "Não foi possível obter uma resposta da IA no momento.";
        } catch (Exception e) {
            return "Erro ao comunicar com a IA: " + e.getMessage();
        }
    }

    private List<GeminiContentDto> buildContents(List<Message> history, String currentUserMessage) {
        List<GeminiContentDto> contents = new ArrayList<>();

        for (Message msg : history) {
            String role = msg.isFromIa() ? "model" : "user";
            contents.add(new GeminiContentDto(role, List.of(new GeminiPartDto(msg.getContent()))));
        }

        contents.add(new GeminiContentDto("user", List.of(new GeminiPartDto(currentUserMessage))));

        return contents;
    }
}
