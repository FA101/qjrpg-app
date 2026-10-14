enum StatusDenuncia { PENDENTE, PROCEDENTE, IMPROCEDENTE }

class Denuncia {
  final String id;
  final String usuarioDenunciadoId;
  final String usuarioDenuncianteId;
  final String motivo;
  final StatusDenuncia status;
  final String criadoEm;

  const Denuncia({
    required this.id, required this.usuarioDenunciadoId, required this.usuarioDenuncianteId,
    required this.motivo, required this.status, required this.criadoEm,
  });

  factory Denuncia.fromJson(Map<String, dynamic> json) => Denuncia(
        id: json['id'] as String,
        usuarioDenunciadoId: json['usuarioDenunciadoId'] as String,
        usuarioDenuncianteId: json['usuarioDenuncianteId'] as String,
        motivo: json['motivo'] as String,
        status: StatusDenuncia.values.byName(json['status'] as String),
        criadoEm: json['criadoEm'] as String,
      );
}
