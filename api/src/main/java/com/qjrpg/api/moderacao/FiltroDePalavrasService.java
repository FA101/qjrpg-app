package com.qjrpg.api.moderacao;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtro por PALAVRA INTEIRA (nao por trecho) - evita falsos positivos do
 * tipo "efeito Scunthorpe", onde um termo proibido curto aparece como pedaco
 * de uma palavra inocente (ex: "ass" dentro de "Cassio" ou "classico").
 */
@Service
public class FiltroDePalavrasService {

    private final Set<String> palavrasProibidas;

    public FiltroDePalavrasService() {
        this.palavrasProibidas = carregarLista();
    }

    public boolean contemPalavraProibida(String texto) {
        if (texto == null || palavrasProibidas.isEmpty()) return false;
        String normalizado = normalizar(texto);
        for (String token : normalizado.split("[^a-z0-9]+")) {
            if (!token.isBlank() && palavrasProibidas.contains(token)) return true;
        }
        return false;
    }

    private Set<String> carregarLista() {
        try (InputStream input = getClass().getResourceAsStream("/moderacao/palavras-proibidas.txt")) {
            if (input == null) return Set.of();
            try (BufferedReader leitor = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                return leitor.lines()
                        .map(String::trim)
                        .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                        .map(this::normalizar)
                        .collect(Collectors.toSet());
            }
        } catch (IOException e) {
            return Set.of();
        }
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase();
    }
}
