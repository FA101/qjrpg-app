package com.qjrpg.api.relatorio;

import com.qjrpg.api.evento.Evento;
import com.qjrpg.api.evento.EventoRepository;
import com.qjrpg.api.mesa.Mesa;
import com.qjrpg.api.mesa.MesaRepository;
import com.qjrpg.api.usuario.Usuario;
import com.qjrpg.api.usuario.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * RF41-42. Simplificacao consciente: filtra em memoria (Java streams), sem
 * JPQL customizado - adequado para a escala do evento (baixa frequencia,
 * poucas dezenas de mesas por mes). Se o volume crescer muito, isso pode
 * precisar virar consulta no banco.
 * "Frequencia de participacao" (contagem de eventos distintos por usuario)
 * fica fora desta v1 - retornamos os dados brutos filtrados, que ja cobrem
 * a maior parte dos filtros pedidos no PRD.
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final MesaRepository mesaRepository;
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public RelatorioController(MesaRepository mesaRepository, EventoRepository eventoRepository,
                                UsuarioRepository usuarioRepository) {
        this.mesaRepository = mesaRepository;
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERADOR')")
    @GetMapping("/mesas")
    public RelatorioMesasResponse mesas(
            @RequestParam(required = false) UUID eventoId,
            @RequestParam(required = false) UUID gameMasterId,
            @RequestParam(required = false) String tipoJogo,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String faixaEtaria,
            @RequestParam(required = false) String sexo) {

        List<RelatorioMesaItem> itens = mesaRepository.findAll().stream()
                .filter(m -> eventoId == null || m.getEventoId().equals(eventoId))
                .filter(m -> gameMasterId == null || m.getGameMasterId().equals(gameMasterId))
                .filter(m -> tipoJogo == null || tipoJogo.equalsIgnoreCase(m.getTipoJogo()))
                .filter(m -> faixaEtaria == null || faixaEtaria.equalsIgnoreCase(m.getFaixaEtaria()))
                .map(this::enriquecer)
                .filter(item -> mes == null || (item.eventoData() != null && item.eventoData().getMonthValue() == mes))
                .filter(item -> ano == null || (item.eventoData() != null && item.eventoData().getYear() == ano))
                .filter(item -> sexo == null || sexo.equalsIgnoreCase(item.sexoGameMaster()))
                .collect(Collectors.toList());

        Map<String, Long> porTipo = itens.stream()
                .collect(Collectors.groupingBy(RelatorioMesaItem::tipoJogo, Collectors.counting()));

        return new RelatorioMesasResponse(itens, itens.size(), porTipo);
    }

    private RelatorioMesaItem enriquecer(Mesa m) {
        Evento evento = eventoRepository.findById(m.getEventoId()).orElse(null);
        Usuario gameMaster = usuarioRepository.findById(m.getGameMasterId()).orElse(null);
        return new RelatorioMesaItem(
                m.getId(), m.getEventoId(), evento != null ? evento.getNome() : "?",
                evento != null ? evento.getData() : null,
                m.getGameMasterId(), gameMaster != null ? gameMaster.nomeExibicao() : "?",
                gameMaster != null ? gameMaster.getSexo() : null,
                m.getTipoJogo(), m.getSistemaJogo(), m.getHoraInicio(), m.getHoraFim(),
                m.getVagasTotais(), m.getFaixaEtaria(), m.getStatus());
    }
}
