import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'mesa_providers.dart';

class MesaListPage extends ConsumerWidget {
  final String eventoId;
  const MesaListPage({super.key, required this.eventoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final mesasAsync = ref.watch(mesasPorEventoProvider(eventoId));
    return Scaffold(
      appBar: AppBar(title: const Text('Mesas ofertadas')),
      body: mesasAsync.when(
        data: (mesas) => ListView(
          children: mesas
              .map((m) => ListTile(
                    title: Text('${m.tipoJogo} - ${m.horaInicio.substring(0, 5)} as ${m.horaFim.substring(0, 5)}'),
                    subtitle: Text('${m.vagas} vagas - ${m.status.name}'),
                  ))
              .toList(),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: $e')),
      ),
    );
  }
}
