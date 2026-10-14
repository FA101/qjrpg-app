package com.qjrpg.api.usuario.dto;
import com.qjrpg.api.usuario.PapelUsuario;
import com.qjrpg.api.usuario.Usuario;
import java.time.LocalDate;
import java.util.UUID;

public record UsuarioResponse(UUID id, String nomeExibicao, String apelido, String email, PapelUsuario papel,
                               boolean mostrarNomeReal, boolean ativo, boolean suspenso,
                               boolean notificacoesPushAtivas, LocalDate dataNascimento, String sexo,
                               String responsavelEmail, boolean vinculoResponsavelConfirmado, boolean totpAtivo,
                               boolean aguardandoAutorizacaoResponsavel, boolean menorDeIdade) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.nomeExibicao(), u.getApelido(), u.getEmail(), u.getPapel(),
                u.isMostrarNomeReal(), u.isAtivo(), u.estaSuspenso(), u.isNotificacoesPushAtivas(),
                u.getDataNascimento(), u.getSexo(), u.getResponsavelEmail(), u.isVinculoResponsavelConfirmado(),
                u.isTotpAtivo(), u.aguardandoAutorizacaoResponsavel(), u.isMenorDeIdade());
    }
}
