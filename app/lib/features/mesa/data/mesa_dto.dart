import '../domain/mesa.dart';

class MesaDto {
  static Mesa fromJson(Map<String, dynamic> json) => Mesa(
        id: json['id'] as String?,
        eventoId: json['eventoId'] as String,
        gameMasterId: json['gameMasterId'] as String,
        gameMasterNome: json['gameMasterNome'] as String?,
        numero: json['numero'] as int?,
        tipoJogo: json['tipoJogo'] as String,
        sistemaJogo: json['sistemaJogo'] as String?,
        tituloAventura: json['tituloAventura'] as String?,
        sinopse: json['sinopse'] as String?,
        palavrasChave: json['palavrasChave'] as String?,
        observacoes: json['observacoes'] as String?,
        faixaEtaria: json['faixaEtaria'] as String?,
        horaInicio: json['horaInicio'] as String,
        horaFim: json['horaFim'] as String,
        vagasTotais: json['vagasTotais'] as int,
        vagasReservadas: json['vagasReservadas'] as int? ?? 0,
        vagasDisponiveis: json['vagasDisponiveis'] as int?,
        status: StatusMesa.values.byName(json['status'] as String),
      );

  static Map<String, dynamic> toJson(Mesa m) => {
        'eventoId': m.eventoId, 'gameMasterId': m.gameMasterId, 'numero': m.numero,
        'tipoJogo': m.tipoJogo, 'sistemaJogo': m.sistemaJogo, 'tituloAventura': m.tituloAventura,
        'sinopse': m.sinopse, 'palavrasChave': m.palavrasChave, 'observacoes': m.observacoes,
        'faixaEtaria': m.faixaEtaria, 'horaInicio': m.horaInicio, 'horaFim': m.horaFim,
        'vagasTotais': m.vagasTotais, 'vagasReservadas': m.vagasReservadas,
      };
}
