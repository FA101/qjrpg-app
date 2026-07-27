import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'workshop_providers.dart';

class WorkshopListPage extends ConsumerWidget {
  final String eventoId;
  const WorkshopListPage({super.key, required this.eventoId});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final workshopsAsync = ref.watch(workshopsPorEventoProvider(eventoId));
    return Scaffold(
      appBar: AppBar(title: const Text('Workshops e rodas')),
      body: workshopsAsync.when(
        data: (workshops) => ListView(
          children: workshops.map((w) => ListTile(title: Text(w.tema), subtitle: Text(w.status.name))).toList(),
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: $e')),
      ),
    );
  }
}
