import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../domain/candidatura.dart';
import 'candidatura_providers.dart';

class CandidaturaListPage extends ConsumerWidget {
  final String mesaId;
  const CandidaturaListPage({super.key, required this.mesaId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final candidaturasAsync = ref.watch(candidaturasPorMesaProvider(mesaId));
    return Scaffold(
      appBar: AppBar(title: const Text('Candidaturas da mesa')),
      body: candidaturasAsync.when(
        data: (candidaturas) => ListView(
          children: candidaturas.map((c) {
            final texto = c.bloqueada ? '${c.status.name} (bloqueada por conflito)' : c.status.name;
            return ListTile(
              title: Text(c.usuarioId),
              subtitle: Text(texto),
              trailing: c.status == StatusCandidatura.PENDENTE
                  ? Row(mainAxisSize: MainAxisSize.min, children: [
                      IconButton(
                        icon: const Icon(Icons.check, color: Colors.green),
                        onPressed: () async {
                          await ref.read(candidaturaRepositoryProvider).aceitar(c.id!);
                          ref.invalidate(candidaturasPorMesaProvider(mesaId));
                        },
                      ),
                      IconButton(
                        icon: const Icon(Icons.close, color: Colors.red),
                        onPressed: () async {
                          await ref.read(candidaturaRepositoryProvider).recusar(c.id!);
                          ref.invalidate(candidaturasPorMesaProvider(mesaId));
                        },
                      ),
                    ])
                  : null,
            );
          }).toList(),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: $e')),
      ),
    );
  }
}
