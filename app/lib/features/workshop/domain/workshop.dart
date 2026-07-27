enum StatusWorkshop { PENDENTE, APROVADO, RECUSADO }

class Workshop {
  final String? id;
  final String usuarioId;
  final String eventoId;
  final String tema;
  final String? descricao;
  final String? horarioDesejado;
  final StatusWorkshop status;

  const Workshop({
    this.id, required this.usuarioId, required this.eventoId, required this.tema,
    this.descricao, this.horarioDesejado, this.status = StatusWorkshop.PENDENTE,
  });
}
