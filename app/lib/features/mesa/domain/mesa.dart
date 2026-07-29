enum StatusMesa { PENDENTE, APROVADA, RECUSADA, AJUSTE_SOLICITADO }

class Mesa {
  final String? id;
  final String eventoId;
  final String gameMasterId;
  final String? gameMasterNome;
  final int? numero;
  final String tipoJogo;
  final String? sistemaJogo;
  final String? tituloAventura;
  final String? sinopse;
  final String? palavrasChave;
  final String? observacoes;
  final String? faixaEtaria;
  final String horaInicio;
  final String horaFim;
  final int vagasTotais;
  final int vagasReservadas;
  final int? vagasDisponiveis;
  final StatusMesa status;

  const Mesa({
    this.id, required this.eventoId, required this.gameMasterId, this.gameMasterNome, this.numero,
    required this.tipoJogo, this.sistemaJogo, this.tituloAventura, this.sinopse, this.palavrasChave,
    this.observacoes, this.faixaEtaria, required this.horaInicio, required this.horaFim,
    required this.vagasTotais, this.vagasReservadas = 0, this.vagasDisponiveis,
    this.status = StatusMesa.PENDENTE,
  });
}
