import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/workshop_api_repository.dart';
import '../domain/workshop.dart';
import '../domain/workshop_repository.dart';

final workshopRepositoryProvider = Provider<WorkshopRepository>((ref) => WorkshopApiRepository(DioClient.instance));

final workshopsPorEventoProvider = FutureProvider.autoDispose.family<List<Workshop>, String>((ref, eventoId) {
  return ref.watch(workshopRepositoryProvider).listarPorEvento(eventoId);
});
