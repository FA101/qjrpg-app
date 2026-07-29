enum StatusEvento { PLANEJADO, PUBLICADO, ENCERRADO, CANCELADO }

class Evento {
  final String? id;
  final String nome;
  final String local;
  final String? linkMapa;
  final StatusEvento status;
  final String? horaInicioJanela; // "HH:mm:ss"
  final String? horaFimJanela;

  const Evento({
    this.id, required this.nome, required this.local, this.linkMapa,
    required this.status, this.horaInicioJanela, this.horaFimJanela,
  });
}
