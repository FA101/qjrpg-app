import '../domain/evento.dart';

/// Traduz entre o JSON da API e a entidade de dominio.
/// Isolar isso aqui evita que o formato da API vaze para o resto do app (DRY).
class EventoDto {
  static Evento fromJson(Map<String, dynamic> json) {
    return Evento(
      id: json['id'] as String?,
      nome: json['nome'] as String,
      local: json['local'] as String,
      linkMapa: json['linkMapa'] as String?,
      status: StatusEvento.values.byName(json['status'] as String),
    );
  }

  static Map<String, dynamic> toJson(Evento evento) {
    return {
      'nome': evento.nome,
      'local': evento.local,
      'linkMapa': evento.linkMapa,
      'status': evento.status.name,
      'horaInicioJanela': null,
      'horaFimJanela': null,
    };
  }
}
