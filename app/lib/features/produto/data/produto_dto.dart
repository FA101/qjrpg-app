import '../domain/produto.dart';

class ProdutoDto {
  static Produto fromJson(Map<String, dynamic> json) => Produto(
        id: json['id'] as String?,
        usuarioId: json['usuarioId'] as String,
        eventoId: json['eventoId'] as String,
        tipo: TipoProduto.values.byName(json['tipo'] as String),
        titulo: json['titulo'] as String,
        descricao: json['descricao'] as String?,
        imagemUrl: json['imagemUrl'] as String?,
        linkExterno: json['linkExterno'] as String?,
      );

  static Map<String, dynamic> toJson(Produto p) => {
        'usuarioId': p.usuarioId, 'eventoId': p.eventoId, 'tipo': p.tipo.name,
        'titulo': p.titulo, 'descricao': p.descricao, 'imagemUrl': p.imagemUrl, 'linkExterno': p.linkExterno,
      };
}
