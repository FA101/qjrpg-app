import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/network/dio_client.dart';
import '../domain/denuncia.dart';

final denunciasPendentesProvider = FutureProvider.autoDispose<List<Denuncia>>((ref) async {
  final r = await DioClient.instance.get('/denuncias');
  return (r.data as List).map((j) => Denuncia.fromJson(j)).toList();
});
