import 'package:flutter/material.dart';
import '../../features/evento/presentation/evento_list_page.dart';
import '../../features/tag/presentation/tag_list_page.dart';
import '../../features/link/presentation/link_util_list_page.dart';
import '../../features/conteudo/presentation/regras_evento_page.dart';

/// Menu lateral com as telas de nivel global (sem depender de um evento/mesa
/// especifico). Reutilizado em todas as paginas de topo (DRY: um unico lugar
/// define a navegacao principal do app).
class AppDrawer extends StatelessWidget {
  const AppDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          const DrawerHeader(
            decoration: BoxDecoration(color: Colors.deepPurple),
            child: Text('Quero Jogar RPG', style: TextStyle(color: Colors.white, fontSize: 20)),
          ),
          ListTile(
            leading: const Icon(Icons.event),
            title: const Text('Eventos'),
            onTap: () => _irPara(context, const EventoListPage()),
          ),
          ListTile(
            leading: const Icon(Icons.label),
            title: const Text('Tags'),
            onTap: () => _irPara(context, const TagListPage()),
          ),
          ListTile(
            leading: const Icon(Icons.link),
            title: const Text('Links uteis'),
            onTap: () => _irPara(context, const LinkUtilListPage()),
          ),
          ListTile(
            leading: const Icon(Icons.rule),
            title: const Text('Regras do evento'),
            onTap: () => _irPara(context, const RegrasEventoPage()),
          ),
        ],
      ),
    );
  }

  void _irPara(BuildContext context, Widget pagina) {
    Navigator.of(context).pop(); // fecha o drawer
    Navigator.of(context).pushReplacement(MaterialPageRoute(builder: (_) => pagina));
  }
}
