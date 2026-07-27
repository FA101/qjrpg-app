import 'package:flutter/material.dart';
import '../../mesa/presentation/mesa_list_page.dart';
import '../../produto/presentation/produto_list_page.dart';
import '../../workshop/presentation/workshop_list_page.dart';

/// Detalhe de um evento: abas de Mesas, Catalogo (Produtos) e Workshops.
class EventoDetailPage extends StatelessWidget {
  final String eventoId;
  final String nomeEvento;
  const EventoDetailPage({super.key, required this.eventoId, required this.nomeEvento});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: Text(nomeEvento),
          bottom: const TabBar(tabs: [Tab(text: 'Mesas'), Tab(text: 'Catalogo'), Tab(text: 'Workshops')]),
        ),
        body: TabBarView(
          children: [
            MesaListBody(eventoId: eventoId),
            ProdutoListBody(eventoId: eventoId),
            WorkshopListBody(eventoId: eventoId),
          ],
        ),
      ),
    );
  }
}
