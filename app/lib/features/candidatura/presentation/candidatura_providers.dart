import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/candidatura_api_repository.dart';
import '../domain/candidatura.dart';
import '../domain/candidatura_repository.dart';

final candidaturaRepositoryProvider = Provider<CandidaturaRepository>((ref) => CandidaturaApiRepository(DioClient.instance));

final candidaturasPorMesaProvider = FutureProvider.autoDispose.family<List<Candidatura>, String>((ref, mesaId) {
  return ref.watch(candidaturaRepositoryProvider).listarPorMesa(mesaId);
});
