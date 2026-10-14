package com.qjrpg.api.tag;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Cria as tags fixas (Game Master, Vendas, Apoiador, Moderador) na subida da API, se ainda nao existirem. */
@Component
public class TagsDoSistemaInicializador implements ApplicationRunner {

    private final TagService tagService;

    public TagsDoSistemaInicializador(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public void run(ApplicationArguments args) {
        tagService.garantirTagsDoSistema();
    }
}
