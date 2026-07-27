enum TipoTag { FIXA, CUSTOMIZADA }

class Tag {
  final String? id;
  final String nome;
  final String corHex;
  final TipoTag tipo;
  final String? regraAplicacao;

  const Tag({this.id, required this.nome, required this.corHex, required this.tipo, this.regraAplicacao});
}
