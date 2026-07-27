import 'package:dio/dio.dart';
import '../domain/workshop.dart';
import '../domain/workshop_repository.dart';
import 'workshop_dto.dart';

class WorkshopApiRepository implements WorkshopRepository {
  final Dio _dio;
  WorkshopApiRepository(this._dio);

  @override
  Future<List<Workshop>> listarPorEvento(String eventoId) async {
    final r = await _dio.get('/workshops', queryParameters: {'eventoId': eventoId});
    return (r.data as List).map((j) => WorkshopDto.fromJson(j)).toList();
  }

  @override
  Future<Workshop> propor(Workshop w) async {
    final r = await _dio.post('/workshops', data: WorkshopDto.toJson(w));
    return WorkshopDto.fromJson(r.data);
  }

  @override
  Future<Workshop> atualizarStatus(String id, StatusWorkshop status) async {
    final r = await _dio.patch('/workshops/$id/status', data: {'status': status.name});
    return WorkshopDto.fromJson(r.data);
  }
}
