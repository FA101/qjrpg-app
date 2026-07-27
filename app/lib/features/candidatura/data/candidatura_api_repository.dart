import 'package:dio/dio.dart';
import '../domain/candidatura.dart';
import '../domain/candidatura_repository.dart';
import 'candidatura_dto.dart';

class CandidaturaApiRepository implements CandidaturaRepository {
  final Dio _dio;
  CandidaturaApiRepository(this._dio);

  @override
  Future<List<Candidatura>> listarPorMesa(String mesaId) async {
    final r = await _dio.get('/candidaturas', queryParameters: {'mesaId': mesaId});
    return (r.data as List).map((j) => CandidaturaDto.fromJson(j)).toList();
  }

  @override
  Future<Candidatura> candidatar(String mesaId, String usuarioId) async {
    final r = await _dio.post('/candidaturas', data: {'mesaId': mesaId, 'usuarioId': usuarioId});
    return CandidaturaDto.fromJson(r.data);
  }

  @override
  Future<Candidatura> aceitar(String id) async {
    // No back-end, isso dispara o bloqueio automatico de candidaturas conflitantes (RF46).
    final r = await _dio.patch('/candidaturas/$id/aceitar');
    return CandidaturaDto.fromJson(r.data);
  }

  @override
  Future<Candidatura> recusar(String id) async {
    final r = await _dio.patch('/candidaturas/$id/recusar');
    return CandidaturaDto.fromJson(r.data);
  }

  @override
  Future<void> remover(String id) async => _dio.delete('/candidaturas/$id');
}
