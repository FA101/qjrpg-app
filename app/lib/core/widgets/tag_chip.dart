import 'package:flutter/material.dart';
import '../../features/tag/domain/tag.dart';

/// Converte "#RRGGBB" em Color; cor invalida cai no cinza (nunca quebra a tela).
Color corDaTag(String corHex) {
  final hex = corHex.replaceFirst('#', '');
  final valor = int.tryParse(hex.length == 6 ? 'FF$hex' : hex, radix: 16);
  return valor == null ? Colors.grey : Color(valor);
}

/// Chip de tag acessivel (PRD secao 5 e 8): a cor nunca e o unico indicador -
/// o nome aparece em texto, com cor de texto escolhida para manter contraste.
class TagChip extends StatelessWidget {
  final Tag tag;
  final VoidCallback? onRemover;

  const TagChip({super.key, required this.tag, this.onRemover});

  @override
  Widget build(BuildContext context) {
    final fundo = corDaTag(tag.corHex);
    final texto = ThemeData.estimateBrightnessForColor(fundo) == Brightness.dark ? Colors.white : Colors.black;
    return Semantics(
      label: 'Tag ${tag.nome}',
      child: Chip(
        label: Text(tag.nome, style: TextStyle(color: texto)),
        backgroundColor: fundo,
        side: BorderSide.none,
        onDeleted: onRemover,
        deleteIconColor: texto,
        deleteButtonTooltipMessage: 'Remover tag ${tag.nome}',
      ),
    );
  }
}
