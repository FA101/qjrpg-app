import 'workshop.dart';
abstract class WorkshopRepository {
  Future<List<Workshop>> listarPorEvento(String eventoId);
  Future<Workshop> propor(Workshop workshop);
  Future<Workshop> atualizarStatus(String id, StatusWorkshop status);
}
