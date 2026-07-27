import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:app/main.dart';

void main() {
  testWidgets('App inicia e mostra o titulo da tela de eventos', (WidgetTester tester) async {
    await tester.pumpWidget(const ProviderScope(child: QjrpgApp()));
    await tester.pump();

    expect(find.text('Eventos QJRPG'), findsOneWidget);
  });
}