enum TipoProduto { DIGITAL, FISICO }

class Produto {
  final String? id;
  final String usuarioId;
  final String eventoId;
  final TipoProduto tipo;
  final String titulo;
  final String? descricao;
  final String? imagemUrl;
  final String? linkExterno;

  const Produto({
    this.id, required this.usuarioId, required this.eventoId, required this.tipo,
    required this.titulo, this.descricao, this.imagemUrl, this.linkExterno,
  });
}
