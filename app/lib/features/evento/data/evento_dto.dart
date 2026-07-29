import '../domain/evento.dart';

class EventoDto {
  static Evento fromJson(Map<String, dynamic> json) {
    return Evento(
      id: json['id'] as String?,
      nome: json['nome'] as String,
      data: json['data'] as String,
      local: json['local'] as String,
      linkMapa: json['linkMapa'] as String?,
      status: StatusEvento.values.byName(json['status'] as String),
      horaInicioJanela: json['horaInicioJanela'] as String?,
      horaFimJanela: json['horaFimJanela'] as String?,
    );
  }

  static Map<String, dynamic> toJson(Evento evento) {
    return {
      'nome': evento.nome,
      'data': evento.data,
      'local': evento.local,
      'linkMapa': evento.linkMapa,
      'status': evento.status.name,
      'horaInicioJanela': evento.horaInicioJanela,
      'horaFimJanela': evento.horaFimJanela,
    };
  }
}
