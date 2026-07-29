package com.qjrpg.api.moderacao;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;

/**
 * Filtro simples de termos proibidos, usado ao definir apelido (RF de
 * moderacao). A lista fica em resources/moderacao/palavras-proibidas.txt,
 * fora do codigo-fonte, para poder ser curada/atualizada sem recompilar.
 * Normaliza acentos e caixa antes de comparar (ainda assim e um filtro
 * basico - a fila de denuncia cobre o que o filtro nao pega).
 */
@Service
public class FiltroDePalavrasService {

    private final List<String> palavrasProibidas;

    public FiltroDePalavrasService() {
        this.palavrasProibidas = carregarLista();
    }

    public boolean contemPalavraProibida(String texto) {
        if (texto == null || palavrasProibidas.isEmpty()) return false;
        String normalizado = normalizar(texto);
        return palavrasProibidas.stream().anyMatch(normalizado::contains);
    }

    private List<String> carregarLista() {
        try (InputStream input = getClass().getResourceAsStream("/moderacao/palavras-proibidas.txt")) {
            if (input == null) return List.of();
            try (BufferedReader leitor = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                return leitor.lines()
                        .map(String::trim)
                        .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                        .map(this::normalizar)
                        .toList();
            }
        } catch (IOException e) {
            return List.of();
        }
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase();
    }
}
