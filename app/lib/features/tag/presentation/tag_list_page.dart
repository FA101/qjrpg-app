import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../core/utils/erro_utils.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/tag_chip.dart';
import '../../auth/presentation/auth_providers.dart';
import '../domain/tag.dart';
import 'tag_providers.dart';
import 'tag_form_dialog.dart';

class TagListPage extends ConsumerWidget {
  const TagListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final tagsAsync = ref.watch(tagsProvider);
    final ehAdmin = ref.watch(authNotifierProvider)?.ehAdmin ?? false;
    return Scaffold(
      appBar: AppBar(title: const Text('Tags')),
      drawer: const AppDrawer(),
      body: tagsAsync.when(
        data: (tags) => tags.isEmpty
            ? const Center(child: Text('Nenhuma tag cadastrada.'))
            : ListView(
                children: tags
                    .map((t) => ListTile(
                          leading: CircleAvatar(backgroundColor: corDaTag(t.corHex)),
                          title: Text(t.nome),
                          subtitle: Text(t.tipo == TipoTag.FIXA ? 'Fixa do sistema' : 'Customizada'),
                        ))
                    .toList(),
              ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (e, _) => Center(child: Text('Erro: ${extrairMensagemErro(e)}')),
      ),
      // RF27: so Administrador cria tags (o back-end tambem bloqueia).
      floatingActionButton: ehAdmin
          ? FloatingActionButton(
              tooltip: 'Nova tag',
              onPressed: () => abrirFormularioTag(context, ref),
              child: const Icon(Icons.add),
            )
          : null,
    );
  }
}
