import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/mesa_api_repository.dart';
import '../domain/mesa.dart';
import '../domain/mesa_repository.dart';

final mesaRepositoryProvider = Provider<MesaRepository>((ref) => MesaApiRepository(DioClient.instance));

final mesasPorEventoProvider = FutureProvider.autoDispose.family<List<Mesa>, String>((ref, eventoId) {
  return ref.watch(mesaRepositoryProvider).listarPorEvento(eventoId);
});
