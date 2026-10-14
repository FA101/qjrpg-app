package com.qjrpg.api.tag;

import com.qjrpg.api.tag.dto.TagRequest;
import com.qjrpg.api.shared.exception.RecursoNaoEncontradoException;
import com.qjrpg.api.shared.exception.RegraDeNegocioException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository repository;
    private final UsuarioTagRepository usuarioTagRepository;

    public TagServiceImpl(TagRepository repository, UsuarioTagRepository usuarioTagRepository) {
        this.repository = repository;
        this.usuarioTagRepository = usuarioTagRepository;
    }

    @Override
    public Tag criar(TagRequest request) {
        validarCorExclusiva(request.corHex(), null);
        validarNomeLivre(request.nome(), null);
        Tag tag = new Tag(request.nome(), request.corHex(), request.tipo(), request.regraAplicacao());
        return repository.save(tag);
    }

    @Override
    public List<Tag> listarTodos() {
        return repository.findAll();
    }

    @Override
    public Tag buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tag nao encontrada: " + id));
    }

    @Override
    public Tag atualizar(UUID id, TagRequest request) {
        Tag tag = buscarPorId(id);
        if (ehTagDoSistema(tag) && !tag.getNome().equals(request.nome())) {
            throw new RegraDeNegocioException("Tags fixas do sistema nao podem ser renomeadas");
        }
        validarCorExclusiva(request.corHex(), id);
        validarNomeLivre(request.nome(), id);
        TipoTag tipo = ehTagDoSistema(tag) ? TipoTag.FIXA : request.tipo();
        tag.atualizar(request.nome(), request.corHex(), tipo, request.regraAplicacao());
        return repository.save(tag);
    }

    @Override
    public void excluir(UUID id) {
        Tag tag = buscarPorId(id);
        if (ehTagDoSistema(tag)) {
            throw new RegraDeNegocioException("Tags fixas do sistema nao podem ser excluidas");
        }
        usuarioTagRepository.deleteAll(usuarioTagRepository.findByTagId(id));
        repository.delete(tag);
    }

    @Override
    public void garantirTagsDoSistema() {
        for (TagSistema ts : TagSistema.values()) {
            if (repository.findByNome(ts.getNome()).isPresent()) continue;
            repository.save(new Tag(ts.getNome(), corLivre(ts.getCorPadrao()), TipoTag.FIXA, "Atribuida pelo sistema"));
        }
    }

    @Override
    public void atribuirTagDoSistema(UUID usuarioId, TagSistema tagSistema) {
        Tag tag = tagDoSistema(tagSistema);
        if (!usuarioTagRepository.existsByUsuarioIdAndTagId(usuarioId, tag.getId())) {
            usuarioTagRepository.save(new UsuarioTag(usuarioId, tag.getId(), true, null));
        }
    }

    @Override
    public void removerTagDoSistema(UUID usuarioId, TagSistema tagSistema) {
        Tag tag = tagDoSistema(tagSistema);
        usuarioTagRepository.findByUsuarioIdAndTagId(usuarioId, tag.getId()).ifPresent(usuarioTagRepository::delete);
    }

    @Override
    public void atribuirManual(UUID usuarioId, UUID tagId, UUID atribuidaPor) {
        Tag tag = buscarPorId(tagId);
        if (tag.getNome().equals(TagSistema.MODERADOR.getNome())) {
            throw new RegraDeNegocioException("A tag Moderador so e atribuida por promocao de papel");
        }
        if (usuarioTagRepository.existsByUsuarioIdAndTagId(usuarioId, tagId)) {
            throw new RegraDeNegocioException("O usuario ja possui a tag " + tag.getNome());
        }
        usuarioTagRepository.save(new UsuarioTag(usuarioId, tagId, false, atribuidaPor));
    }

    @Override
    public void removerManual(UUID usuarioId, UUID tagId) {
        Tag tag = buscarPorId(tagId);
        if (tag.getNome().equals(TagSistema.MODERADOR.getNome())) {
            throw new RegraDeNegocioException("A tag Moderador so e removida por democao de papel");
        }
        UsuarioTag atribuicao = usuarioTagRepository.findByUsuarioIdAndTagId(usuarioId, tagId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("O usuario nao possui a tag " + tag.getNome()));
        usuarioTagRepository.delete(atribuicao);
    }

    @Override
    public List<Tag> tagsDoUsuario(UUID usuarioId) {
        List<UUID> ids = usuarioTagRepository.findByUsuarioId(usuarioId).stream().map(UsuarioTag::getTagId).toList();
        return ids.isEmpty() ? List.of() : repository.findAllById(ids);
    }

    private Tag tagDoSistema(TagSistema tagSistema) {
        return repository.findByNome(tagSistema.getNome()).orElseGet(() -> {
            garantirTagsDoSistema();
            return repository.findByNome(tagSistema.getNome())
                    .orElseThrow(() -> new IllegalStateException("Tag do sistema ausente: " + tagSistema.getNome()));
        });
    }

    private static boolean ehTagDoSistema(Tag tag) {
        return Arrays.stream(TagSistema.values()).anyMatch(ts -> ts.getNome().equals(tag.getNome()));
    }

    /** Se a cor padrao ja estiver em uso por uma tag customizada, escolhe outra livre (RF28). */
    private String corLivre(String preferida) {
        if (repository.findByCorHex(preferida).isEmpty()) return preferida;
        for (int i = 1; i < 4096; i++) {
            String candidata = String.format("#%06X", (Integer.parseInt(preferida.substring(1), 16) + i * 0x0F0F0F) & 0xFFFFFF);
            if (repository.findByCorHex(candidata).isEmpty()) return candidata;
        }
        throw new IllegalStateException("Nao foi possivel encontrar cor livre para tag do sistema");
    }

    // RF28: cada tag tem cor exclusiva
    private void validarCorExclusiva(String corHex, UUID idAtual) {
        repository.findByCorHex(corHex).ifPresent(existente -> {
            boolean mesmoRegistro = idAtual != null && idAtual.equals(existente.getId());
            if (!mesmoRegistro) {
                throw new RegraDeNegocioException("Ja existe uma tag com a cor " + corHex);
            }
        });
    }

    private void validarNomeLivre(String nome, UUID idAtual) {
        repository.findByNome(nome).ifPresent(existente -> {
            boolean mesmoRegistro = idAtual != null && idAtual.equals(existente.getId());
            if (!mesmoRegistro) {
                throw new RegraDeNegocioException("Ja existe uma tag com o nome " + nome);
            }
        });
    }
}
