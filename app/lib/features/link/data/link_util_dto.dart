import '../domain/link_util.dart';

class LinkUtilDto {
  static LinkUtil fromJson(Map<String, dynamic> json) => LinkUtil(
        id: json['id'] as String?, titulo: json['titulo'] as String,
        url: json['url'] as String, categoria: json['categoria'] as String,
      );

  static Map<String, dynamic> toJson(LinkUtil l) => {'titulo': l.titulo, 'url': l.url, 'categoria': l.categoria};
}
