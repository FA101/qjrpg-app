import 'package:dio/dio.dart';
import '../domain/mesa.dart';
import '../domain/mesa_repository.dart';
import 'mesa_dto.dart';

class MesaApiRepository implements MesaRepository {
  final Dio _dio;
  MesaApiRepository(this._dio);

  @override
  Future<List<Mesa>> listarPorEvento(String eventoId) async {
    final r = await _dio.get('/mesas', queryParameters: {'eventoId': eventoId});
    return (r.data as List).map((j) => MesaDto.fromJson(j)).toList();
  }

  @override
  Future<Mesa> ofertar(Mesa mesa) async {
    // Erros de regra de negocio (sobreposicao, fora da janela) chegam aqui como
    // DioException com status 422 - a UI (FutureProvider) recebe e mostra o erro.
    final r = await _dio.post('/mesas', data: MesaDto.toJson(mesa));
    return MesaDto.fromJson(r.data);
  }

  @override
  Future<Mesa> atualizarStatus(String id, StatusMesa status) async {
    final r = await _dio.patch('/mesas/$id/status', data: {'status': status.name});
    return MesaDto.fromJson(r.data);
  }
}
