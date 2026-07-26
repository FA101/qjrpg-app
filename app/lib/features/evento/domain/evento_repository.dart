import 'evento.dart';

/// Interface (Dependency Inversion): a camada de apresentacao depende
/// disto, nao de EventoApi diretamente. Facilita testar com um fake/mock.
abstract class EventoRepository {
  Future<List<Evento>> listarTodos();
  Future<Evento> criar(Evento evento);
  Future<void> excluir(String id);
}
