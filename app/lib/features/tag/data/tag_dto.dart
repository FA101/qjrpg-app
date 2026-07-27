import '../domain/tag.dart';

class TagDto {
  static Tag fromJson(Map<String, dynamic> json) => Tag(
        id: json['id'] as String?,
        nome: json['nome'] as String,
        corHex: json['corHex'] as String,
        tipo: TipoTag.values.byName(json['tipo'] as String),
        regraAplicacao: json['regraAplicacao'] as String?,
      );

  static Map<String, dynamic> toJson(Tag tag) => {
        'nome': tag.nome,
        'corHex': tag.corHex,
        'tipo': tag.tipo.name,
        'regraAplicacao': tag.regraAplicacao,
      };
}
