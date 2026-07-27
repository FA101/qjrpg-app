import '../domain/mesa.dart';

class MesaDto {
  static Mesa fromJson(Map<String, dynamic> json) => Mesa(
        id: json['id'] as String?,
        eventoId: json['eventoId'] as String,
        gameMasterId: json['gameMasterId'] as String,
        tipoJogo: json['tipoJogo'] as String,
        horaInicio: json['horaInicio'] as String,
        horaFim: json['horaFim'] as String,
        vagas: json['vagas'] as int,
        status: StatusMesa.values.byName(json['status'] as String),
      );

  static Map<String, dynamic> toJson(Mesa m) => {
        'eventoId': m.eventoId, 'gameMasterId': m.gameMasterId, 'tipoJogo': m.tipoJogo,
        'horaInicio': m.horaInicio, 'horaFim': m.horaFim, 'vagas': m.vagas,
      };
}
