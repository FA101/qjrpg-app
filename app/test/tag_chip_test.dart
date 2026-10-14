import 'package:app/core/widgets/tag_chip.dart';
import 'package:app/features/tag/domain/tag.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  test('corDaTag converte hex e tolera valor invalido', () {
    expect(corDaTag('#FF0000'), const Color(0xFFFF0000));
    expect(corDaTag('xyz'), Colors.grey);
  });

  testWidgets('TagChip sempre exibe o nome em texto (cor nao e o unico indicador)', (tester) async {
    const tag = Tag(id: '1', nome: 'Game Master', corHex: '#6A1B9A', tipo: TipoTag.FIXA);
    await tester.pumpWidget(const MaterialApp(home: Scaffold(body: TagChip(tag: tag))));

    expect(find.text('Game Master'), findsOneWidget);
    expect(find.bySemanticsLabel(RegExp('Tag Game Master')), findsOneWidget);
  });
}
