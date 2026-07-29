import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'features/auth/presentation/auth_providers.dart';
import 'features/auth/presentation/login_page.dart';
import 'features/evento/presentation/evento_list_page.dart';

void main() {
  runApp(const ProviderScope(child: QjrpgApp()));
}

class QjrpgApp extends StatelessWidget {
  const QjrpgApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Quero Jogar RPG',
      theme: ThemeData(colorSchemeSeed: Colors.deepPurple, useMaterial3: true),
      home: const _PortaDeEntrada(),
    );
  }
}

class _PortaDeEntrada extends ConsumerWidget {
  const _PortaDeEntrada();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final usuario = ref.watch(authNotifierProvider);
    return usuario == null ? const LoginPage() : const EventoListPage();
  }
}
