import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../features/evento/presentation/evento_list_page.dart';
import '../../features/tag/presentation/tag_list_page.dart';
import '../../features/link/presentation/link_util_list_page.dart';
import '../../features/conteudo/presentation/regras_evento_page.dart';
import '../../features/auth/presentation/auth_providers.dart';
import '../../features/auth/presentation/perfil_page.dart';
import '../../features/moderacao/presentation/denuncias_page.dart';
import '../../features/relatorio/presentation/relatorio_page.dart';

class AppDrawer extends ConsumerWidget {
  const AppDrawer({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final usuario = ref.watch(authNotifierProvider);
    final ehModeracao = usuario?.papel == 'ADMIN' || usuario?.papel == 'MODERADOR';

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
          ListTile(
            leading: const Icon(Icons.person),
            title: const Text('Meu perfil'),
            onTap: () => _irPara(context, const PerfilPage()),
          ),
          if (ehModeracao) ...[
            ListTile(
              leading: const Icon(Icons.flag),
              title: const Text('Denuncias pendentes'),
              onTap: () => _irPara(context, const DenunciasPage()),
            ),
            ListTile(
              leading: const Icon(Icons.bar_chart),
              title: const Text('Relatorios'),
              onTap: () => _irPara(context, const RelatorioPage()),
            ),
          ],
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout),
            title: const Text('Sair'),
            onTap: () async {
              await ref.read(authNotifierProvider.notifier).sair();
              if (context.mounted) Navigator.of(context).pop();
            },
          ),
        ],
      ),
    );
  }

  void _irPara(BuildContext context, Widget pagina) {
    Navigator.of(context).pop();
    Navigator.of(context).pushReplacement(MaterialPageRoute(builder: (_) => pagina));
  }
}
