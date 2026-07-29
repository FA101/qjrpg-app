import '../domain/candidatura.dart';

class CandidaturaDto {
  static Candidatura fromJson(Map<String, dynamic> json) => Candidatura(
        id: json['id'] as String?,
        mesaId: json['mesaId'] as String,
        usuarioId: json['usuarioId'] as String,
        usuarioNome: json['usuarioNome'] as String?,
        status: StatusCandidatura.values.byName(json['status'] as String),
        bloqueada: json['bloqueada'] as bool,
      );
}
