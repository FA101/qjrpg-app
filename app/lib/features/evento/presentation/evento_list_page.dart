import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/widgets/app_drawer.dart';
import '../domain/evento.dart';
import 'evento_providers.dart';
import 'evento_detail_page.dart';
import 'evento_form_dialog.dart';

class EventoListPage extends ConsumerWidget {
  const EventoListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final eventosAsync = ref.watch(eventosProvider);

    return Scaffold(
      appBar: AppBar(title: const Text('Eventos QJRPG')),
      drawer: const AppDrawer(),
      body: eventosAsync.when(
        data: (eventos) => _ListaDeEventos(eventos: eventos),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (erro, _) => Center(child: Text('Erro ao carregar eventos: $erro')),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => abrirFormularioEvento(context, ref),
        child: const Icon(Icons.add),
      ),
    );
  }
}

class _ListaDeEventos extends StatelessWidget {
  final List<Evento> eventos;
  const _ListaDeEventos({required this.eventos});

  @override
  Widget build(BuildContext context) {
    if (eventos.isEmpty) {
      return const Center(child: Text('Nenhum evento cadastrado ainda.'));
    }
    return ListView.builder(
      itemCount: eventos.length,
      itemBuilder: (context, index) {
        final evento = eventos[index];
        final dataBr = evento.data.split('-').reversed.join('/');
        return ListTile(
          title: Text(evento.nome),
          subtitle: Text('$dataBr - ${evento.local} - ${evento.status.name}'),
          trailing: const Icon(Icons.chevron_right),
          onTap: () => Navigator.of(context).push(
            MaterialPageRoute(
              builder: (_) => EventoDetailPage(eventoId: evento.id!, nomeEvento: evento.nome),
            ),
          ),
        );
      },
    );
  }
}
