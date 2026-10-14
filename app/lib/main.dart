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

/// Espera a tentativa de reidratar a sessao salva (SharedPreferences) antes
/// de decidir entre LoginPage e o app - evita "piscar" a tela de login
/// toda vez que a pagina e recarregada (F5) com uma sessao ainda valida.
class _PortaDeEntrada extends ConsumerWidget {
  const _PortaDeEntrada();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final inicializacao = ref.watch(sessaoInicialProvider);
    return inicializacao.when(
      data: (_) {
        final usuario = ref.watch(authNotifierProvider);
        return usuario == null ? const LoginPage() : const EventoListPage();
      },
      loading: () => const Scaffold(body: Center(child: CircularProgressIndicator())),
      error: (_, __) => const LoginPage(),
    );
  }
}
