enum StatusMesa { PENDENTE, APROVADA, RECUSADA, AJUSTE_SOLICITADO }

class Mesa {
  final String? id;
  final String eventoId;
  final String gameMasterId;
  final String tipoJogo;
  final String horaInicio; // "HH:mm:ss", simplificacao para evitar parsing de LocalTime aqui
  final String horaFim;
  final int vagas;
  final StatusMesa status;

  const Mesa({
    this.id, required this.eventoId, required this.gameMasterId, required this.tipoJogo,
    required this.horaInicio, required this.horaFim, required this.vagas,
    this.status = StatusMesa.PENDENTE,
  });
}
