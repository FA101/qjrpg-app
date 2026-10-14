import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:app/main.dart';

void main() {
  testWidgets('Sem sessao salva, o app abre na tela de login', (WidgetTester tester) async {
    SharedPreferences.setMockInitialValues({});

    await tester.pumpWidget(const ProviderScope(child: QjrpgApp()));
    await tester.pumpAndSettle();

    expect(find.text('Entrar - Quero Jogar RPG'), findsOneWidget);
  });
}
