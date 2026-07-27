import '../domain/conteudo_institucional.dart';

class ConteudoDto {
  static ConteudoInstitucional fromJson(Map<String, dynamic> json) => ConteudoInstitucional(
        id: json['id'] as String?, secao: json['secao'] as String, titulo: json['titulo'] as String,
        corpo: json['corpo'] as String, autorId: json['autorId'] as String,
        dataAtualizacao: json['dataAtualizacao'] as String?,
      );
}
