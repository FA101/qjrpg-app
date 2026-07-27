import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'workshop_providers.dart';

class WorkshopListBody extends ConsumerWidget {
  final String eventoId;
  const WorkshopListBody({super.key, required this.eventoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final workshopsAsync = ref.watch(workshopsPorEventoProvider(eventoId));
    return workshopsAsync.when(
      data: (workshops) {
        if (workshops.isEmpty) return const Center(child: Text('Nenhum workshop proposto ainda.'));
        return ListView(
          children: workshops.map((w) => ListTile(title: Text(w.tema), subtitle: Text(w.status.name))).toList(),
        );
      },
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (e, _) => Center(child: Text('Erro: $e')),
    );
  }
}
