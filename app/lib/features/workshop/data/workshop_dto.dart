import '../domain/workshop.dart';

class WorkshopDto {
  static Workshop fromJson(Map<String, dynamic> json) => Workshop(
        id: json['id'] as String?,
        usuarioId: json['usuarioId'] as String,
        eventoId: json['eventoId'] as String,
        tema: json['tema'] as String,
        descricao: json['descricao'] as String?,
        horarioDesejado: json['horarioDesejado'] as String?,
        status: StatusWorkshop.values.byName(json['status'] as String),
      );

  static Map<String, dynamic> toJson(Workshop w) => {
        'usuarioId': w.usuarioId, 'eventoId': w.eventoId, 'tema': w.tema,
        'descricao': w.descricao, 'horarioDesejado': w.horarioDesejado,
      };
}
