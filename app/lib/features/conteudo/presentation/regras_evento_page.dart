import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/widgets/app_drawer.dart';
import 'conteudo_providers.dart';

class RegrasEventoPage extends ConsumerWidget {
  const RegrasEventoPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final conteudoAsync = ref.watch(conteudoPorSecaoProvider('regras-gerais'));
    return Scaffold(
      appBar: AppBar(title: const Text('Regras do evento')),
      drawer: const AppDrawer(),
      body: conteudoAsync.when(
        data: (c) => Padding(
          padding: const EdgeInsets.all(16),
          child: SingleChildScrollView(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(c.titulo, style: Theme.of(context).textTheme.headlineSmall),
                const SizedBox(height: 12),
                Text(c.corpo),
              ],
            ),
          ),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: $e')),
      ),
    );
  }
}
