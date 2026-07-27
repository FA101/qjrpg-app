import 'mesa.dart';
abstract class MesaRepository {
  Future<List<Mesa>> listarPorEvento(String eventoId);
  Future<Mesa> ofertar(Mesa mesa);
  Future<Mesa> atualizarStatus(String id, StatusMesa status);
}
