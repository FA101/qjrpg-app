enum StatusEvento { PLANEJADO, PUBLICADO, ENCERRADO, CANCELADO }

/// Entidade de dominio - nao sabe nada sobre JSON ou HTTP.
/// A camada data (EventoDto) e que traduz para/de rede.
class Evento {
  final String? id;
  final String nome;
  final String local;
  final String? linkMapa;
  final StatusEvento status;

  const Evento({
    this.id,
    required this.nome,
    required this.local,
    this.linkMapa,
    required this.status,
  });
}
