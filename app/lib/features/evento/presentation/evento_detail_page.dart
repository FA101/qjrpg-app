import 'package:flutter/material.dart';
import '../../../core/utils/ics_export.dart';
import '../domain/evento.dart';
import '../../mesa/presentation/mesa_list_page.dart';
import '../../produto/presentation/produto_list_page.dart';
import '../../workshop/presentation/workshop_list_page.dart';

class EventoDetailPage extends StatelessWidget {
  final Evento evento;
  const EventoDetailPage({super.key, required this.evento});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: Text(evento.nome),
          actions: [
            if (exportacaoIcsDisponivel)
            IconButton(
              icon: const Icon(Icons.calendar_month),
              tooltip: 'Adicionar ao calendario',
              onPressed: () => baixarIcsDoEvento(
                nome: evento.nome,
                data: evento.data,
                local: evento.local,
                horaInicio: evento.horaInicioJanela,
                horaFim: evento.horaFimJanela,
              ),
            ),
          ],
          bottom: const TabBar(tabs: [Tab(text: 'Mesas'), Tab(text: 'Catalogo'), Tab(text: 'Workshops')]),
        ),
        body: TabBarView(
          children: [
            MesaListBody(eventoId: evento.id!),
            ProdutoListBody(eventoId: evento.id!),
            WorkshopListBody(eventoId: evento.id!),
          ],
        ),
      ),
    );
  }
}
