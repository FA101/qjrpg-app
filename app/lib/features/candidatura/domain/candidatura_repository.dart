import 'candidatura.dart';
abstract class CandidaturaRepository {
  Future<List<Candidatura>> listarPorMesa(String mesaId);
  Future<Candidatura> candidatar(String mesaId, String usuarioId);
  Future<Candidatura> aceitar(String id);
  Future<Candidatura> recusar(String id);
  Future<void> remover(String id);
}
