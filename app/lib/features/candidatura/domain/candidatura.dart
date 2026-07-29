enum StatusCandidatura { PENDENTE, ACEITA, RECUSADA }

class Candidatura {
  final String? id;
  final String mesaId;
  final String usuarioId;
  final String? usuarioNome;
  final StatusCandidatura status;
  final bool bloqueada;

  const Candidatura({
    this.id, required this.mesaId, required this.usuarioId, this.usuarioNome,
    this.status = StatusCandidatura.PENDENTE, this.bloqueada = false,
  });
}
