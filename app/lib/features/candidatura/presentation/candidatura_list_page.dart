import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/auth/auth_session.dart';
import '../../../core/utils/erro_utils.dart';
import '../domain/candidatura.dart';
import 'candidatura_providers.dart';

class CandidaturaListBody extends ConsumerWidget {
  final String mesaId;
  const CandidaturaListBody({super.key, required this.mesaId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final candidaturasAsync = ref.watch(candidaturasPorMesaProvider(mesaId));
    final jaCandidatado = candidaturasAsync.maybeWhen(
      data: (lista) => lista.any((c) => c.usuarioId == AuthSession.usuarioId),
      orElse: () => false,
    );

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(8),
          child: ElevatedButton.icon(
            icon: const Icon(Icons.how_to_reg),
            label: Text(jaCandidatado ? 'Voce ja se candidatou' : 'Candidatar-se'),
            onPressed: jaCandidatado
                ? null
                : () async {
                    try {
                      await ref.read(candidaturaRepositoryProvider).candidatar(mesaId, AuthSession.usuarioId!);
                      ref.invalidate(candidaturasPorMesaProvider(mesaId));
                    } catch (e) {
                      if (context.mounted) {
                        ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(extrairMensagemErro(e))));
                      }
                    }
                  },
          ),
        ),
        Expanded(
          child: candidaturasAsync.when(
            data: (candidaturas) {
              if (candidaturas.isEmpty) return const Center(child: Text('Nenhuma candidatura ainda.'));
              return ListView(
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
              );
            },
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (e, _) => Center(child: Text('Erro: $e')),
          ),
        ),
      ],
    );
  }
}
