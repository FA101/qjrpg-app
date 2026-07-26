import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/evento_api_repository.dart';
import '../domain/evento.dart';
import '../domain/evento_repository.dart';

/// Injecao de dependencia via Riverpod: a UI pede um EventoRepository
/// (a interface), nunca a implementacao concreta diretamente.
final eventoRepositoryProvider = Provider<EventoRepository>((ref) {
  return EventoApiRepository(DioClient.instance);
});

/// Estado assincrono da lista de eventos - Riverpod cuida de loading/erro/dados.
final eventosProvider = FutureProvider.autoDispose<List<Evento>>((ref) async {
  final repository = ref.watch(eventoRepositoryProvider);
  return repository.listarTodos();
});
