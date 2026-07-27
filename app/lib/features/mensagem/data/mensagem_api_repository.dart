import 'package:dio/dio.dart';
import '../domain/mensagem.dart';
import '../domain/mensagem_repository.dart';
import 'mensagem_dto.dart';

class MensagemApiRepository implements MensagemRepository {
  final Dio _dio;
  MensagemApiRepository(this._dio);

  @override
  Future<List<Mensagem>> listarPorMesa(String mesaId) async {
    final r = await _dio.get('/mensagens', queryParameters: {'mesaId': mesaId});
    return (r.data as List).map((j) => MensagemDto.fromJson(j)).toList();
  }

  @override
  Future<Mensagem> enviar(Mensagem m) async {
    final r = await _dio.post('/mensagens', data: MensagemDto.toJson(m));
    return MensagemDto.fromJson(r.data);
  }

  @override
  Future<void> excluir(String id) async => _dio.delete('/mensagens/$id');
}
