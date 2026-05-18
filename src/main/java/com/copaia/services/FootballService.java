package com.copaia.services;

import com.copaia.dtos.football.FootballTeamsResponseDto;
import com.copaia.dtos.football.TeamDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FootballService {

    private final RestTemplate restTemplate;

    @Value("${football.api.key:}")
    private String apiKey;

    @Value("${football.api.url}")
    private String apiUrl;

    private List<TeamDto> cachedTeams = null;

    public FootballService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<TeamDto> getWorldCupTeams() {
        if (cachedTeams != null) {
            return cachedTeams;
        }
        if (apiKey == null || apiKey.isBlank()) {
            return Collections.emptyList();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<FootballTeamsResponseDto> response = restTemplate.exchange(
                    apiUrl + "/competitions/WC/teams",
                    HttpMethod.GET,
                    entity,
                    FootballTeamsResponseDto.class
            );

            if (response.getBody() != null && response.getBody().teams() != null) {
                cachedTeams = response.getBody().teams();
                return cachedTeams;
            }
        } catch (Exception e) {
            // API indisponível - continua sem dados externos
        }
        return Collections.emptyList();
    }

    public Optional<TeamDto> findTeamByName(String name) {
        String nameLower = name.toLowerCase();
        return getWorldCupTeams().stream()
                .filter(t -> t.name().toLowerCase().contains(nameLower)
                        || t.shortName().toLowerCase().contains(nameLower)
                        || t.tla().toLowerCase().contains(nameLower))
                .findFirst();
    }

    public String buildTeamsContext() {
        List<TeamDto> teams = getWorldCupTeams();
        if (teams.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("Seleções participantes da Copa do Mundo:\n");
        for (TeamDto team : teams) {
            sb.append("- ").append(team.name())
              .append(" (").append(team.tla()).append(")\n");
        }
        return sb.toString();
    }
}
