import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../data/mensagem_api_repository.dart';
import '../domain/mensagem.dart';
import '../domain/mensagem_repository.dart';

final mensagemRepositoryProvider = Provider<MensagemRepository>((ref) => MensagemApiRepository(DioClient.instance));

final mensagensPorMesaProvider = FutureProvider.autoDispose.family<List<Mensagem>, String>((ref, mesaId) {
  return ref.watch(mensagemRepositoryProvider).listarPorMesa(mesaId);
});
