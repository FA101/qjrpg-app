import 'package:dio/dio.dart';
import '../domain/evento.dart';
import '../domain/evento_repository.dart';
import 'evento_dto.dart';

/// Implementacao concreta usando Dio. So esta classe sabe que a API existe.
class EventoApiRepository implements EventoRepository {
  final Dio _dio;

  EventoApiRepository(this._dio);

  @override
  Future<List<Evento>> listarTodos() async {
    final resposta = await _dio.get('/eventos');
    final lista = resposta.data as List;
    return lista.map((json) => EventoDto.fromJson(json as Map<String, dynamic>)).toList();
  }

  @override
  Future<Evento> criar(Evento evento) async {
    final resposta = await _dio.post('/eventos', data: EventoDto.toJson(evento));
    return EventoDto.fromJson(resposta.data as Map<String, dynamic>);
  }

  @override
  Future<void> excluir(String id) async {
    await _dio.delete('/eventos/$id');
  }
}
